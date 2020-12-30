package aka_ecliptic.com.cinephile.Helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MySQLiteHelper {

    public static final Map<String, String> TABLE_NAMES;
    static {
        HashMap<String, String> mutable = new HashMap<>();
            mutable.put("movies", "movies");
            mutable.put("m_stats", "movie_statistics");
            mutable.put("m_images", "movie_images");
            mutable.put("collections", "collections");
            mutable.put("c_m_link", "collections_movies");
        TABLE_NAMES = Collections.unmodifiableMap(mutable);
    }

    public static final String[] TABLE_HEADING_MOVIE = {"ID", "Seen", "ReleaseDate", "Title",
            "Rating", "Genre", "SubGenre", "MinGenre"};

    public static final String[] TABLE_HEADING_MOVIE_STATS = {"MovieID", "Description",
            "SiteRating", "Runtime"};

    public static final String[] TABLE_HEADING_MOVIE_IMAGES = {"MovieID", "PosterPath",
            "BackdropPath"};

    public static final String[] TABLE_HEADING_COLLECTIONS = {"ID", "Name", "Type"};

    public static final String[] TABLE_HEADING_COLLECTIONS_MOVIES = {"CollectionID", "MovieID"};

    private static final String CREATE_TABLE_MOVIES = "CREATE TABLE IF NOT EXISTS " +
            "'movies' (" +
                "'ID' INTEGER NOT NULL PRIMARY KEY," +
                "'Seen'	INTEGER NOT NULL," +
                "'ReleaseDate' TEXT NOT NULL," +
                "'Title' TEXT NOT NULL," +
                "'Rating' INTEGER NOT NULL," +
                "'Genre' TEXT NOT NULL," +
                "'SubGenre' TEXT NOT NULL," +
                "'MinGenre' TEXT NOT NULL" +
            ")";

    private static final String CREATE_TABLE_MOVIE_STATS = "CREATE TABLE IF NOT EXISTS " +
            "'movie_statistics' (" +
                "'MovieID' INTEGER NOT NULL UNIQUE," +
                "'Description' TEXT," +
                "'SiteRating' INTEGER," +
                "'Runtime' INTEGER," +
                "FOREIGN KEY(MovieID) REFERENCES movies(ID)" +
            ")";

    private static final String CREATE_TABLE_MOVIE_IMAGES = "CREATE TABLE IF NOT EXISTS " +
            "'movie_images' ( " +
                "'MovieID' INTEGER NOT NULL UNIQUE, " +
                "'PosterPath' TEXT, " +
                "'BackdropPath' TEXT, " +
                "FOREIGN KEY(MovieID) REFERENCES movies(ID)" +
            ")";

    private static final String CREATE_TABLE_COLLECTIONS = "CREATE TABLE IF NOT EXISTS " +
            "'collections' ( " +
                "'ID' INTEGER NOT NULL PRIMARY KEY, " +
                "'Name' TEXT NOT NULL, " +
                "'TYPE' INTEGER NOT NULL" +
            ")";

    private static final String CREATE_TABLE_COLLECTIONS_MOVIES = "CREATE TABLE IF NOT EXISTS " +
            "'collections_movies' ( " +
                "'CollectionID' INTEGER NOT NULL, " +
                "'MovieID' INTEGER NOT NULL, " +
                "FOREIGN KEY(CollectionID) REFERENCES collections(ID), " +
                "FOREIGN KEY(MovieID) REFERENCES movies(ID)" +
            ")";

    public static final String[] TABLE_CREATES = { CREATE_TABLE_MOVIES, CREATE_TABLE_MOVIE_STATS, CREATE_TABLE_MOVIE_IMAGES,
            CREATE_TABLE_COLLECTIONS, CREATE_TABLE_COLLECTIONS_MOVIES };

    public static final String DEFAULT_INSERT_COLLECTIONS = "INSERT INTO 'collections' ('ID', 'Name', 'Type') " +
            "VALUES (0, 'All', 0), (1, 'Favourites', 0), (2, 'Movies', 1), (3, 'Shows', 2), (4, 'Anime', 3)";

    public static final String[] DEFAULT_COLLECTIONS = { "All", "Favourites", "Movies",
            "Shows", "Anime" };

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

    public static String SELECT_IDS_WITH_TITLES_IN (int size) {
        StringBuilder query = new StringBuilder("SELECT m.ID " +
                "FROM 'movies' m " +
                "WHERE m.Title IN ( ?");

        if(size <= 1){
            query.append(" )");
            return query.toString();
        }

        for (int i = 2; i <= size; i++) {
            query.append(", ?");
        }

        query.append(" )");
        return query.toString();
    }

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

    public static final String SELECT_MOVIE_ID_FAVOURITES = "SELECT m.ID " +
            "FROM 'movies' m " +
            "INNER JOIN 'collections_movies' cm " +
            "ON m.ID = cm.MovieID " +
            "WHERE m.ID = ? AND cm.CollectionID = 1";

    public static final String SELECT_MOVIE_FROM_LIST = "SELECT m.*, s.Description, s.SiteRating, s.Runtime, i.PosterPath, i.BackdropPath " +
            "FROM 'movies' m " +
            "LEFT JOIN 'movie_statistics' s " +
            "ON m.ID = s.MovieID " +
            "LEFT JOIN 'movie_images' i " +
            "ON m.ID = i.MovieID " +
            "WHERE m.ID IN ( " +
                "SELECT cm.MovieID FROM 'collections_movies' cm " +
                "LEFT JOIN 'collections' c " +
                "ON c.ID = cm.CollectionID " +
                "WHERE c.Name = ?" +
            ")";

    public static final String SELECT_MOVIE_ID_FROM_LIST = "SELECT m.ID " +
            "FROM 'movies' m " +
            "LEFT  JOIN 'collections_movies' cm " +
            "ON m.ID = cm.MovieID" +
            "LEFT JOIN 'collections' c " +
            "ON c.ID = cm.CollectionID " +
            "WHERE c.Name = ?";

    public static final String SELECT_ASSOCIATED_COLLECTIONS = "SELECT c.Name " +
            "FROM 'collections' c " +
            "INNER JOIN 'collections_movies' cm " +
            "ON c.ID = cm.CollectionID " +
            "WHERE cm.MovieID = ?";


    public static final String SELECT_COLLECTION_HEADINGS = "SELECT c.Name FROM 'collections' c WHERE c.ID < 5";

    public static final String SELECT_COLLECTION_NAMES = "SELECT c.Name FROM 'collections' c WHERE c.ID > 4 OR c.ID = 1";

    public static final String INSERT_COLLECTION_MOVIE = "INSERT INTO 'collections_movies' VALUES " +
            "(" +
                "(SELECT c.ID FROM 'collections' c WHERE c.Name = ?)," +
                " ?" +
            ")";

    public static final String DELETE_COLLECTION_LINK = "DELETE FROM 'collections_movies' " +
            "WHERE CollectionID = (" +
                "SELECT c.ID FROM 'collections' c WHERE c.Name = ?" +
            ")";

    public static final String DELETE_FROM_COLLECTION = "DELETE FROM 'collections_movies' " +
            "WHERE CollectionID = (" +
                "SELECT c.ID FROM 'collections' c WHERE c.Name = ?" +
            ") AND MovieID = ?";
}
