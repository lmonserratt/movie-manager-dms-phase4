package dms.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MysqlMovieDao
 * -------------
 * JDBC-based implementation for MySQL.
 */
public class MysqlMovieDao implements MovieDao {

    private Connection conn;

    @Override
    public void connect(String jdbcUrl) throws SQLException {
        this.conn = DriverManager.getConnection(jdbcUrl);
    }

    // Convenience overload for explicit user/pass (used by MovieService)
    public void connect(String jdbcUrl, String user, String pass) throws SQLException {
        this.conn = DriverManager.getConnection(jdbcUrl, user, pass);
    }

    @Override
    public boolean isConnected() {
        try { return conn != null && !conn.isClosed(); }
        catch (SQLException e) { return false; }
    }

    @Override
    public void close() throws SQLException {
        if (conn != null) { conn.close(); conn = null; }
    }

    @Override
    public void create(Movie m) throws SQLException {
        ensureConnected();
        final String sql = "INSERT INTO movies " +
                "(movie_id, title, director, release_year, duration_minutes, genre, rating) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getMovieId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getDirector());
            ps.setInt(4, m.getReleaseYear());
            ps.setInt(5, m.getDurationMinutes());
            ps.setString(6, m.getGenre());
            ps.setDouble(7, m.getRating());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Movie> readById(String movieId) throws SQLException {
        ensureConnected();
        final String sql = "SELECT * FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Movie> readAll() throws SQLException {
        ensureConnected();
        final String sql = "SELECT * FROM movies ORDER BY title ASC, movie_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Movie> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    @Override
    public void update(Movie m) throws SQLException {
        ensureConnected();
        final String sql = "UPDATE movies SET " +
                "title=?, director=?, release_year=?, duration_minutes=?, genre=?, rating=? " +
                "WHERE movie_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDirector());
            ps.setInt(3, m.getReleaseYear());
            ps.setInt(4, m.getDurationMinutes());
            ps.setString(5, m.getGenre());
            ps.setDouble(6, m.getRating());
            ps.setString(7, m.getMovieId());
            int updated = ps.executeUpdate();
            if (updated == 0) throw new SQLException("No rows updated. Movie id not found: " + m.getMovieId());
        }
    }

    @Override
    public boolean deleteById(String movieId) throws SQLException {
        ensureConnected();
        final String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public double averageDuration() throws SQLException {
        ensureConnected();
        final String sql = "SELECT AVG(duration_minutes) AS avg_min FROM movies";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double v = rs.getDouble("avg_min");
                if (rs.wasNull()) return 0.0;
                return v;
            }
            return 0.0;
        }
    }

    @Override
    public List<Movie> searchByTitle(String fragment) throws SQLException {
        ensureConnected();
        final String sql = "SELECT * FROM movies WHERE LOWER(title) LIKE LOWER(?) " +
                "ORDER BY title ASC, movie_id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + fragment + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Movie> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    @Override
    public boolean existsById(String movieId) throws SQLException {
        ensureConnected();
        final String sql = "SELECT 1 FROM movies WHERE movie_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private Movie map(ResultSet rs) throws SQLException {
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

    private void ensureConnected() throws SQLException {
        if (!isConnected()) throw new SQLException("Not connected to MySQL. Call connect() first.");
        // Optional: conn.setAutoCommit(true); // default
    }
}
