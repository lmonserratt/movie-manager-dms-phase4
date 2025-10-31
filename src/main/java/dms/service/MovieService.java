package dms.service;

import dms.dao.MovieDao;
import dms.dao.MysqlMovieDao;
import dms.model.Movie;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service layer: validation + orchestration between GUI and DAO.
 * - Keeps business rules and validation out of the GUI.
 * - Talks to a DAO implementation (MySQL in this project).
 */
public class MovieService {

    private final MovieDao dao;

    /**
     * Constructs the service with a specific DAO implementation.
     * @param dao a non-null DAO (e.g., MysqlMovieDao)
     */
    public MovieService(MovieDao dao) {
        if (dao == null) throw new IllegalArgumentException("dao cannot be null");
        this.dao = dao;
    }

    // ---------- CONNECTION ----------

    /**
     * Opens a DB connection when the DAO supports it (MysqlMovieDao).
     * @param jdbcUrl JDBC URL, e.g. jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC
     * @param user DB user
     * @param pass DB password
     */
    public void connect(String jdbcUrl, String user, String pass) throws SQLException {
        if (dao instanceof MysqlMovieDao) {
            ((MysqlMovieDao) dao).connect(jdbcUrl, user, pass);
        } else {
            throw new IllegalStateException("Unsupported DAO type for connection");
        }
    }

    /**
     * @return true if the underlying DAO is connected (for MySQL DAO).
     */
    public boolean isConnected() {
        if (dao instanceof MysqlMovieDao) {
            return ((MysqlMovieDao) dao).isConnected();
        }
        return false;
    }

    /**
     * Closes the DB connection if the DAO supports it.
     */
    public void close() throws SQLException {
        if (dao instanceof MysqlMovieDao) {
            ((MysqlMovieDao) dao).close();
        }
    }

    // ---------- CRUD ----------

    /**
     * Creates a new movie after validating fields.
     */
    public void create(Movie movie) throws SQLException {
        validateMovie(movie);
        dao.insert(movie);
    }

    /**
     * @return all movies from the database.
     */
    public List<Movie> readAll() throws SQLException {
        return dao.findAll();
    }

    /**
     * Reads a movie by its String ID (e.g., "INT2010").
     */
    public Optional<Movie> readById(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be empty");
        }
        return dao.findById(id.trim());
    }

    /**
     * Updates an existing movie (by primary key inside the entity).
     */
    public void update(Movie movie) throws SQLException {
        validateMovie(movie);
        boolean ok = dao.update(movie);
        if (!ok) {
            throw new SQLException("Movie ID not found: " + movie.getMovieId());
        }
    }

    /**
     * Deletes a movie by String ID (matches VARCHAR(10) PK).
     */
    public boolean deleteById(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be empty");
        }
        return dao.delete(id.trim());
    }

    /**
     * Case-insensitive title search via DAO custom method.
     */
    public List<Movie> searchByTitle(String titleFragment) throws SQLException {
        if (!(dao instanceof MysqlMovieDao)) {
            throw new IllegalStateException("searchByTitle only implemented in MysqlMovieDao");
        }
        return ((MysqlMovieDao) dao).searchByTitle(titleFragment);
    }

    // ---------- CUSTOM ACTION ----------

    /**
     * Computes the average duration (in minutes) of all movies.
     */
    public double averageDuration() throws SQLException {
        List<Movie> movies = dao.findAll();
        if (movies.isEmpty()) return 0.0;
        double total = 0;
        for (Movie m : movies) total += m.getDurationMinutes();
        return total / movies.size();
    }

    // ---------- VALIDATION ----------

    /**
     * Validates required fields and value ranges according to DB constraints.
     */
    private void validateMovie(Movie m) {
        if (m == null) throw new IllegalArgumentException("Movie cannot be null");
        if (m.getMovieId() == null || m.getMovieId().trim().isEmpty())
            throw new IllegalArgumentException("Movie ID cannot be empty");
        if (m.getTitle() == null || m.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be empty");
        if (m.getDirector() == null || m.getDirector().trim().isEmpty())
            throw new IllegalArgumentException("Director cannot be empty");
        if (m.getGenre() == null || m.getGenre().trim().isEmpty())
            throw new IllegalArgumentException("Genre cannot be empty");
        if (m.getReleaseYear() < 1888 || m.getReleaseYear() > 2100)
            throw new IllegalArgumentException("Release year must be between 1888 and 2100");
        if (m.getDurationMinutes() <= 0 || m.getDurationMinutes() > 999)
            throw new IllegalArgumentException("Duration must be 1..999");
        if (m.getRating() < 0.0 || m.getRating() > 10.0)
            throw new IllegalArgumentException("Rating must be 0.0..10.0");
    }
}
