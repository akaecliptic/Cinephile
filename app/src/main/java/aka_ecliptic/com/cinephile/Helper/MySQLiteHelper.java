package aka_ecliptic.com.cinephile.Helper;

public class MySQLiteHelper {

    public static final String[] TABLE_NAMES = {"movies", "movie_statistics", "movie_images"};

    public static final String[] TABLE_HEADING_MOVIE = {"ID", "Seen", "ReleaseDate", "Title",
            "Rating", "Genre", "SubGenre", "MinGenre"};

    public static final String[] TABLE_HEADING_MOVIE_STATS = {"MovieID", "Description",
            "SiteRating", "Runtime"};

    public static final String[] TABLE_HEADING_MOVIE_IMAGES = {"MovieID", "PosterPath",
            "BackdropPath"};

    public static final String SELECT_ALL_MOVIE_DATA = "SELECT m.*, d.Description, d.SiteRating, d.Runtime, p.PosterPath, p.BackdropPath " +
                                                        "FROM 'movie_data' m " +
                                                        "LEFT JOIN 'movie_descriptors' d " +
                                                        "ON m.ID = d.MovieID " +
                                                        "LEFT JOIN 'movie_posters' p " +
                                                        "ON m.ID = p.MovieID";

}
