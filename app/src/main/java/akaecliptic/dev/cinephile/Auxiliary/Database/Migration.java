package akaecliptic.dev.cinephile.Auxiliary.Database;

import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.doesTableExist;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

abstract public class Migration {

    private static final String TAG = Migration.class.getSimpleName();

    private static final String RENAME_MOVIES = "ALTER TABLE 'movies' RENAME TO 'movies_old';";
    private static final String CREATE_MOVIES = "CREATE TABLE IF NOT EXISTS 'movies' (" +
        "'_id' INTEGER NOT NULL PRIMARY KEY," +
        "'title' TEXT NOT NULL," +
        "'seen'	INTEGER," +
        "'description' TEXT," +
        "'user_rating' INTEGER," +
        "'native_rating' INTEGER," +
        "'release' TEXT );";
    private static final String INSERT_MOVIES = "INSERT OR IGNORE INTO 'movies' " +
        "(_id, title, seen, description, user_rating, native_rating, 'release') " +
        "SELECT t.ID,  t.Title, t.Seen, s.description, t.Rating, s.SiteRating, t.ReleaseDate " +
        "FROM 'movies_old' t " +
        "LEFT JOIN 'movie_statistics' s " +
        "ON t.ID = s.MovieID;";


    private static final String CREATE_INFORMATION = "CREATE TABLE IF NOT EXISTS 'movie_information' (" +
        "'movie_id' INTEGER NOT NULL UNIQUE," +
        "'poster' TEXT," +
        "'backdrop' TEXT," +
        "'genres' TEXT," +
        "'runtime' INTEGER," +
        "'tagline' TEXT," +
        "FOREIGN KEY(movie_id) REFERENCES movies(_id) ON DELETE CASCADE );";
    private static final String INSERT_INFORMATION = "INSERT OR IGNORE INTO 'movie_information' " +
            "(movie_id, poster, backdrop, runtime) " +
            "SELECT i.MovieID, i.PosterPath, i.BackdropPath, s.Runtime " +
            "FROM 'movie_images' i " +
            "LEFT JOIN 'movie_statistics' s " +
            "ON i.MovieID = s.MovieID;";

    /**
     * Used for migrating database tables from version 2 -> 3.
     * Migrating movies table to new schema and preserving existing data.
     *
     * @param database - The database to preform the migration on.
     * @return - True if the transaction finishes successfully, false otherwise.
     *
     * Throws a RuntimeException if any of the individual statements fail.
     */
    public static boolean migrateMovies(SQLiteDatabase database) {

        String oldTable = "old_movies";
        Log.i(TAG, "Checking if old table already exists before attempting transaction.");
        if(doesTableExist(database, oldTable)) return false;

        Log.i(TAG, "Beginning transaction to migrate 'movies' table.");
        database.beginTransaction();
        try {
            Log.i(TAG, "Renaming 'movies' to 'movies_old'.");
            database.execSQL(RENAME_MOVIES);

            Log.i(TAG, "Creating new 'movies' table.");
            database.execSQL(CREATE_MOVIES);

            Log.i(TAG, "Inserting old data into correct fields.");
            database.execSQL(INSERT_MOVIES);
        } catch (SQLException e) {
            Log.w(TAG, "Could not migrate tables.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        } finally {
            Log.i(TAG, "Ending transaction.");
            database.endTransaction();
        }

        Log.i(TAG, "Transaction complete.");
        return true;
    }

    /**
     * Used for migrating database tables from version 2 -> 3.
     * Using new 'movie_information' table to persist additional data on movies,
     * preserving existing data.
     *
     * @param database - The database to preform the migration on.
     * @return - True if the transaction finishes successfully, false otherwise.
     *
     * Throws a RuntimeException if any of the individual statements fail.
     */
    public static boolean migrateMovieInformation(SQLiteDatabase database) {

        Log.i(TAG, "Beginning transaction to migrate movie information.");
        database.beginTransaction();
        try {
            Log.i(TAG, "Creating 'information' table.");
            database.execSQL(CREATE_INFORMATION);

            Log.i(TAG, "Inserting old data into correct fields.");
            database.execSQL(INSERT_INFORMATION);
        } catch (SQLException e) {
            Log.w(TAG, "Could not migrate tables.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        } finally {
            Log.i(TAG, "Ending transaction.");
            database.endTransaction();
        }

        Log.i(TAG, "Transaction complete.");
        return true;
    }

}
