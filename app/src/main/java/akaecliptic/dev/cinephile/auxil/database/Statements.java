package akaecliptic.dev.cinephile.auxil.database;

abstract public class Statements {

    //PRAGMA
    public static final String PRAGMA_FOREIGN_KEY = "PRAGMA foreign_keys = ON;";

    //CREATE
    public static final String CREATE_TABLE_MOVIES = "CREATE TABLE IF NOT EXISTS " +
        "'movies' (" +
            "'_id' INTEGER NOT NULL PRIMARY KEY," +
            "'title' TEXT NOT NULL," +
            "'seen'	INTEGER," +
            "'description' TEXT," +
            "'user_rating' INTEGER," +
            "'native_rating' INTEGER," +
            "'release' TEXT" +
        ");";

    public static final String CREATE_TABLE_INFORMATION = "CREATE TABLE IF NOT EXISTS " +
        "'movie_information' (" +
            "'movie_id' INTEGER NOT NULL UNIQUE," +
            "'poster' TEXT," +
            "'backdrop' TEXT," +
            "'genres' TEXT," +
            "'runtime' INTEGER," +
            "'tagline' TEXT," +
            "FOREIGN KEY(movie_id) REFERENCES movies(_id) ON DELETE CASCADE" +
        ");";

    public static final String CREATE_TABLE_COLLECTIONS = "CREATE TABLE IF NOT EXISTS " +
        "'collections' (" +
            "'_name' TEXT NOT NULL PRIMARY KEY," +
            "'cover' TEXT" +
        ");";

    public static final String CREATE_LINK_MOVIE_COLLECTION = "CREATE TABLE IF NOT EXISTS " +
        "'link_movie_collection' (" +
            "'movie_id' INTEGER NOT NULL," +
            "'collection_name' TEXT NOT NULL," +
            "FOREIGN KEY(movie_id) REFERENCES movies(_id) ON DELETE CASCADE," +
            "FOREIGN KEY(collection_name) REFERENCES collections(_name) ON DELETE CASCADE," +
            "PRIMARY KEY (movie_id, collection_name)" +
        ");";

    public static final String CREATE_VIEW_MOVIE_DATA = "CREATE VIEW IF NOT EXISTS 'movie_data' AS " +
        "SELECT m.*, i.poster, i.backdrop, i.genres, i.runtime, i.tagline " +
        "FROM 'movies' m " +
        "LEFT JOIN 'movie_information' i " +
        "ON m._id = i.movie_id;";

    public static final String CREATE_VIEW_COLLECTION_DATA = "CREATE VIEW IF NOT EXISTS 'collection_data' AS " +
        "SELECT c.*, l.movie_id " +
        "FROM 'collections' c " +
        "LEFT JOIN 'link_movie_collection' l " +
        "ON c._name = l.collection_name " +
        "ORDER BY c._name;";

    public static final String CREATE_VIEW_COLLECTION_MOVIE_DATA = "CREATE VIEW IF NOT EXISTS 'collection_movie_data' AS " +
            "SELECT c._name, m.*, i.poster, i.backdrop, i.genres, i.runtime, i.tagline " +
            "FROM 'collections' c " +
            "LEFT JOIN 'link_movie_collection' l " +
            "ON c._name = l.collection_name " +
            "INNER JOIN 'movies' m " +
            "ON l.movie_id = m._id " +
            "LEFT JOIN 'movie_information' i " +
            "ON m._id = i.movie_id;";

    //SELECT
    public static final String SELECT_ALL_MOVIE_DATA = "SELECT * FROM 'movie_data'";
    public static final String SELECT_ALL_COLLECTION_DATA = "SELECT * FROM 'collection_data'";

    public static final String SELECT_MOVIE = "SELECT * FROM 'movie_data' WHERE _id = ?";
    public static final String SELECT_COLLECTION = "SELECT * FROM 'collection_data' WHERE _name = ?";
    public static final String SELECT_INFORMATION = "SELECT * FROM 'movie_information' WHERE movie_id = ?";
    public static final String SELECT_COLLECTION_MOVIES = "SELECT * FROM 'collection_movie_data' WHERE _name = ?";

    public static final String SELECT_MOVIE_DATA_LIKE = "SELECT * FROM 'movie_data' WHERE title LIKE %?%";

    //INSERT
    public static final String INSERT_COLLECTION_FAVOURITES = "INSERT OR IGNORE INTO 'collections' (_name, cover) VALUES ('favourites', 'heart');";
}
