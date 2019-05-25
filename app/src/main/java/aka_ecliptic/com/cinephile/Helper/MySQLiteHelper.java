package aka_ecliptic.com.cinephile.Helper;

public class MySQLiteHelper {

    public static final String MOVIE_TABLE = "movie_data";
    public static final String POSTER_TABLE = "movie_posters";
    public static final String DESCRIPTOR_TABLE = "movie_descriptors";

    //SELECTS
    public static final String SELECT_ALL_MOVIES = "SELECT * FROM 'movie_data'";
    public static final String SELECT_POSTERS_BY_ID = "SELECT Backdrop, ProfilePoster FROM 'movie_posters' WHERE MovieID = ?";
    public static final String SELECT_DESCRIPTORS_BY_ID = "SELECT Description FROM 'movie_descriptors' WHERE MovieID = ?";

    public static final String SELECT_ALL_MOVIE_INFO = "SELECT m.*, d.Description, p.Backdrop, p.ProfilePoster " +
                                                        "FROM 'movie_data' m " +
                                                        "LEFT JOIN 'movie_descriptors' d " +
                                                        "ON m.ID = d.MovieID " +
                                                        "LEFT JOIN 'movie_posters' p " +
                                                        "ON m.ID = p.MovieID";

}
