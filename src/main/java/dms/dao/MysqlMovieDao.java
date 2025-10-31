package dms.dao;

import dms.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of MovieDao using JDBC.
 * Assumes the table definition from your dms_movies schema with VARCHAR(10) PK.
 */
public class MysqlMovieDao implements MovieDao {

    private Connection conn;

    // ---------- CONNECTION ----------
    /**
     * Opens a JDBC connection. Example jdbcUrl:
     * jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
     */
    public void connect(String jdbcUrl, String user, String pass) throws SQLException {
        if (jdbcUrl == null || jdbcUrl.isBlank())
            throw new IllegalArgumentException("JDBC URL cannot be empty");

        // Force-load MySQL JDBC driver (robust against classpath issues)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath.", e);
        }

        this.conn = DriverManager.getConnection(jdbcUrl, user, pass);
    }

    /** @return true if the connection is open. */
    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /** Closes the connection if open. */
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    // ---------- HELPERS ----------
    private static Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getString("movie_id"),
                rs.getString("title"),
                rs.getString("director"),
                rs.getInt("release_year"),
                rs.getInt("duration_minutes"),
                rs.getString("genre"),
                rs.getDouble("rating")
        );
    }

    // ---------- CRUD ----------
    @Override
    public List<Movie> findAll() throws SQLException {
        String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies ORDER BY title ASC";
        List<Movie> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Optional<Movie> findById(String id) throws SQLException {
        String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public Movie insert(Movie movie) throws SQLException {
        String sql = "INSERT INTO movies (movie_id,title,director,release_year,duration_minutes,genre,rating) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movie.getMovieId());
            ps.setString(2, movie.getTitle());
            ps.setString(3, movie.getDirector());
            ps.setInt(4, movie.getReleaseYear());
            ps.setInt(5, movie.getDurationMinutes());
            ps.setString(6, movie.getGenre());
            ps.setDouble(7, movie.getRating());
            ps.executeUpdate();
            return movie;
        }
    }

    @Override
    public boolean update(Movie movie) throws SQLException {
        String sql = "UPDATE movies " +
                "SET title=?, director=?, release_year=?, duration_minutes=?, genre=?, rating=? " +
                "WHERE movie_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getDirector());
            ps.setInt(3, movie.getReleaseYear());
            ps.setInt(4, movie.getDurationMinutes());
            ps.setString(5, movie.getGenre());
            ps.setDouble(6, movie.getRating());
            ps.setString(7, movie.getMovieId());
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // ---------- CUSTOM QUERY ----------
    /**
     * Case-insensitive title search using LIKE.
     */
    public List<Movie> searchByTitle(String titleFragment) throws SQLException {
        if (titleFragment == null || titleFragment.isBlank())
            throw new IllegalArgumentException("Title fragment cannot be empty");
        String sql = "SELECT movie_id,title,director,release_year,duration_minutes,genre,rating " +
                "FROM movies WHERE LOWER(title) LIKE LOWER(?) ORDER BY title ASC";
        List<Movie> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + titleFragment.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
}
