package dms.util;

/**
 * Optional shared validators for reuse in other layers/forms.
 * Service already contains its own checks; use this if you expand forms/utilities.
 */
public final class Validators {
    private Validators() {}

    public static void requireText(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is required");
    }

    public static void requireId(String id) {
        requireText(id, "movieId");
        if (!id.matches("[A-Z0-9]{6,10}"))
            throw new IllegalArgumentException("movieId must be 6-10 uppercase alphanumerics");
    }

    public static void requireYear(int year) {
        if (year < 1888 || year > 2100)
            throw new IllegalArgumentException("releaseYear must be between 1888 and 2100");
    }

    public static void requireRange(int n, int min, int max, String field) {
        if (n < min || n > max)
            throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
    }

    public static void requireRating(double r) {
        if (r < 0.0 || r > 10.0)
            throw new IllegalArgumentException("rating must be between 0.0 and 10.0");
    }
}
