package akaecliptic.dev.cinephile.Auxiliary.Database;

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

    public static final String CREATE_VIEW_MOVIE_DATA = "CREATE VIEW IF NOT EXISTS 'movie_data' AS " +
        "SELECT m.*, i.poster, i.backdrop, i.genres, i.runtime, i.tagline " +
        "FROM 'movies' m " +
        "LEFT JOIN 'movie_information' i " +
        "ON m._id = i.movie_id;";

    //SELECT
    public static final String SELECT_ALL_MOVIE_DATA = "SELECT * FROM 'movie_data';";
    public static final String SELECT_MOVIE = "SELECT * FROM 'movie' WHERE _id = ?;";
    public static final String SELECT_INFORMATION = "SELECT * FROM 'movie_information' WHERE movie_id = ?;";
}