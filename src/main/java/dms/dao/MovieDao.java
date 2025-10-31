package dms.dao;

import dms.model.Movie;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO contract for Movie entity (String-based primary key).
 * Aligns with the database schema where movie_id is VARCHAR(10).
 */
public interface MovieDao {

    /**
     * @return all movies in the table.
     */
    List<Movie> findAll() throws SQLException;

    /**
     * Finds a movie by its String primary key (e.g., "INT2010").
     */
    Optional<Movie> findById(String id) throws SQLException;

    /**
     * Inserts a new Movie row (expects all required fields).
     */
    Movie insert(Movie movie) throws SQLException;

    /**
     * Updates an existing Movie row by its primary key inside the entity.
     * @return true if at least one row was updated.
     */
    boolean update(Movie movie) throws SQLException;

    /**
     * Deletes a Movie by its String primary key.
     * @return true if at least one row was deleted.
     */
    boolean delete(String id) throws SQLException;
}
