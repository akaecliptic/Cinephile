package aka_ecliptic.com.cinephile.Helper;

public class MySQLiteHelper {

    public static final String[] TABLE_NAMES = {"movies", "movie_statistics", "movie_images"};

    public static final String[] TABLE_HEADING_MOVIE = {"ID", "Seen", "ReleaseDate", "Title",
            "Rating", "Genre", "SubGenre", "MinGenre"};

    public static final String[] TABLE_HEADING_MOVIE_STATS = {"MovieID", "Description",
            "SiteRating", "Runtime"};

    public static final String[] TABLE_HEADING_MOVIE_IMAGES = {"MovieID", "PosterPath",
            "BackdropPath"};

    public static final String SELECT_ALL_MOVIE_DATA = "SELECT m.*, s.Description, s.SiteRating, s.Runtime, i.PosterPath, i.BackdropPath " +
                                                        "FROM 'movies' m " +
                                                        "LEFT JOIN 'movie_statistics' s " +
                                                        "ON m.ID = s.MovieID " +
                                                        "LEFT JOIN 'movie_images' i " +
                                                        "ON m.ID = i.MovieID";

    public static final String SELECT_MOVIE_LIKE = "SELECT m.*, s.Description, s.SiteRating, s.Runtime, i.PosterPath, i.BackdropPath " +
            "FROM 'movies' m " +
            "LEFT JOIN 'movie_statistics' s " +
            "ON m.ID = s.MovieID " +
            "LEFT JOIN 'movie_images' i " +
            "ON m.ID = i.MovieID " +
            "WHERE m.Title LIKE ?";

    public static final String SELECT_MOVIE_BY_ID = "SELECT m.*, s.Description, s.SiteRating, s.Runtime, i.PosterPath, i.BackdropPath " +
            "FROM 'movies' m " +
            "LEFT JOIN 'movie_statistics' s " +
            "ON m.ID = s.MovieID " +
            "LEFT JOIN 'movie_images' i " +
            "ON m.ID = i.MovieID " +
            "WHERE m.ID = ?";

    public static final String SELECT_MOVIE_ID = "SELECT m.ID " +
            "FROM 'movies' m " +
            "WHERE m.ID = ?";
}
