package dms.model;

import java.util.Objects;

/**
 * Movie POJO (DB-agnostic). Fields map 1:1 to DB columns.
 */
public class Movie {
    private String movieId;        // PK e.g., "INT2010"
    private String title;
    private String director;
    private int releaseYear;       // 1888..2100
    private int durationMinutes;   // 1..999
    private String genre;
    private double rating;         // 0.0..10.0

    public Movie(String movieId, String title, String director, int releaseYear,
                 int durationMinutes, String genre, double rating) {
        if (movieId == null || movieId.isBlank())
            throw new IllegalArgumentException("movieId cannot be null or blank");
        this.movieId = movieId.trim();
        this.title = safeTrim(title);
        this.director = safeTrim(director);
        this.releaseYear = releaseYear;
        this.durationMinutes = durationMinutes;
        this.genre = safeTrim(genre);
        this.rating = rating;
    }

    private static String safeTrim(String s){ return (s==null) ? "" : s.trim(); }

    public String getMovieId(){ return movieId; }
    public void setMovieId(String movieId){
        if (movieId == null || movieId.isBlank())
            throw new IllegalArgumentException("movieId cannot be null or blank");
        this.movieId = movieId.trim();
    }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = safeTrim(title); }
    public String getDirector(){ return director; }
    public void setDirector(String director){ this.director = safeTrim(director); }
    public int getReleaseYear(){ return releaseYear; }
    public void setReleaseYear(int releaseYear){ this.releaseYear = releaseYear; }
    public int getDurationMinutes(){ return durationMinutes; }
    public void setDurationMinutes(int durationMinutes){ this.durationMinutes = durationMinutes; }
    public String getGenre(){ return genre; }
    public void setGenre(String genre){ this.genre = safeTrim(genre); }
    public double getRating(){ return rating; }
    public void setRating(double rating){ this.rating = rating; }

    @Override public boolean equals(Object o){
        if (this==o) return true;
        if (!(o instanceof Movie)) return false;
        Movie other=(Movie)o;
        return Objects.equals(movieId, other.movieId);
    }
    @Override public int hashCode(){ return Objects.hash(movieId); }

    @Override public String toString(){
        return "Movie{" +
                "movieId='" + movieId + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", releaseYear=" + releaseYear +
                ", durationMinutes=" + durationMinutes +
                ", genre='" + genre + '\'' +
                ", rating=" + rating +
                '}';
    }
}
