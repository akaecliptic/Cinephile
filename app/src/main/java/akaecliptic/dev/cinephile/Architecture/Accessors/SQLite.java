package akaecliptic.dev.cinephile.Architecture.Accessors;

import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.doesTableExist;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.formatBoolean;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.formatList;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.getBool;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.getInt;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.getLocalDate;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.getString;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.getIntList;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Migration.migrateMovieInformation;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Migration.migrateMovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import akaecliptic.dev.cinephile.Auxiliary.Database.Statements;
import akaecliptic.dev.cinephile.Auxiliary.Database.Tables;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

/**
 * This class is responsible for managing all SQLite related areas of the application, including:
 * <ul>
 *     <li>Creating Database</li>
 *     <li>Persisting Data</li>
 *     <li>Data CRUD Operations</li>
 * </ul>
 *
 * <p>A newer implementation of {@link akaecliptic.dev.cinephile.Architecture.SQLiteDAO} (now deprecated).</p>
 *
 * <p>
 *     All CRUD operations are synchronous and single threaded. For now the decision is to leave the responsibility
 *     of off-loading work off the UI thread to the repository class. This may change in a later version,
 *     but as of now (09/2022), this is the favoured approach.
 * </p>
 */
// TEST: Migration - need to test migration cases, using old and new DAO.
public class SQLite extends SQLiteOpenHelper {

    private final String TAG = getClass().getSimpleName();

    private static final String DATABASE_NAME = "cinephile.db";
    private static final String OLD_DATABASE_NAME = "Cinephile.db"; //Needed When updating from version 2 -> 3
    private static final String BACKUP_PATH = "OLD_DATABASE_COPY.db";
    private static final int DATABASE_VERSION = 3;

    private static SQLiteDatabase database;
    private static SQLite sqlite;
    private static boolean updateDatabase = false; //Needed When updating from version 2 -> 3

    /*          MANAGEMENT          */

    private SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLite getInstance(Context context){
        if (sqlite == null) sqlite = new SQLite(context);
        database = sqlite.getWritableDatabase(); // TODO: 2022-10-05 Keep an eye on this.
        return sqlite;
    }

    // OVERRIDES

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        database = sqliteDatabase;

        if(DATABASE_VERSION == 3) checkOldDatabase(database);
        initialiseTables(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (newVersion == DATABASE_VERSION) initialiseTables(database);
    }

    @Override
    public synchronized void close() {
        if(database != null) database.close();
        super.close();
    }

    // SET UP

    /**
     * Checking for previous implementation of DAO, copying the database and creating backup.
     * Planning to delete old database in future versions, keeping for redundancy.
     *
     * @param database The current working database.
     */
    private void checkOldDatabase(SQLiteDatabase database) {
        Log.i(TAG, "Checking for old database '" + OLD_DATABASE_NAME + "'");

        String newPath = database.getPath();
        String oldPath = newPath.replace(DATABASE_NAME, OLD_DATABASE_NAME);
        String backupPath = newPath.replace(DATABASE_NAME, BACKUP_PATH);

        File newDatabaseFile = new File(newPath);
        File oldDatabaseFile = new File(oldPath);
        File backupDatabaseFile = new File(backupPath);

        if(!oldDatabaseFile.exists() || backupDatabaseFile.exists()) return;

        Log.i(TAG, "Renaming old database to new name.");
        try {
            Files.move(oldDatabaseFile.toPath(), newDatabaseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Log.w(TAG, "Could not rename database file.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        }

        Log.i(TAG, "Copying data to backup database.");
        try {
            Files.copy(newDatabaseFile.toPath(), backupDatabaseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Log.w(TAG, "Could not copy data to backup database.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        }

        Log.i(TAG, "Data copied and backup created.");
        updateDatabase = true;
    }

    /**
     * Initialises tables in the database.
     *
     * @param database The current working database.
     */
    private void initialiseTables(SQLiteDatabase database) {
        if(DATABASE_VERSION == 3 && updateDatabase) {
            migrateTables(database);
        }

        database.execSQL(Statements.PRAGMA_FOREIGN_KEY); //So, foreign keys are not on by default...
        database.execSQL(Statements.CREATE_TABLE_MOVIES);
        database.execSQL(Statements.CREATE_TABLE_INFORMATION);
        database.execSQL(Statements.CREATE_VIEW_MOVIE_DATA); //View for querying all movie data
    }

    /**
     * Migrates old data to new table schema preserving data where possible.
     *
     * @param database The current working database to migrate to.
     */
    // TODO: 2022-09-30 Needs more testing.
    private void migrateTables(SQLiteDatabase database) {

        boolean hasOldMovies = doesTableExist(database, "old_movies");
        boolean hasMovies = doesTableExist(database, "movies");

        boolean hasInformation = doesTableExist(database, "movie_information");
        boolean hasImages = doesTableExist(database, "movie_images");
        boolean hasStatistics = doesTableExist(database, "movie_statistics");

        //Has new tables and backup
        if(hasOldMovies && hasMovies && hasInformation) return;

        //No backup table
        /*
            This is a weird case - both versions of the tables have the same name.
            So, there is a case where the migration may run on the new table, and possibly crash.
            Should only happen once, but this is less than ideal.

            Luckily this should not be an issue version 4 onwards.
         */
        if(!hasOldMovies && hasMovies) migrateMovies(database);

        //No information table
        if(!hasInformation && (hasImages && hasStatistics)) migrateMovieInformation(database);
    }

    /*          DATA ACCESS          */

    //INSERT
    public void insertMovie(Movie movie) {
        Log.i(TAG, "Inserting new row in 'movies' table.");

        ContentValues values = new ContentValues();

        values.put("_id", movie.getId());
        values.put("title", movie.getTitle());
        values.put("seen", formatBoolean(movie.isSeen()));
        values.put("description", movie.getDescription());
        values.put("user_rating", movie.getUserRating());
        values.put("native_rating", movie.getNativeRating());
        values.put("release", movie.getRelease().toString());

        Log.i(TAG, "Inserting row with _id = " + movie.getId() + ".");
        database.insert(Tables.MOVIES.toString(), null, values);
        insertInformation(new Pair<>(movie.getId(), movie.getInfo()));
    }

    public void insertInformation(Pair<Integer, Information> pair) {
        Log.i(TAG, "Inserting new row in 'movie_information' table.");

        ContentValues values = new ContentValues();

        values.put("movie_id", pair.first);
        values.put("poster", pair.second.getPoster());
        values.put("backdrop", pair.second.getBackdrop());
        values.put("runtime", pair.second.getRuntime());
        values.put("tagline", pair.second.getTagline());
        values.put("genres", formatList(pair.second.getGenres()));

        Log.i(TAG, "Inserting row with movie_id = " + pair.first + ".");
        database.insert(Tables.INFORMATION.toString(), null, values);
    }

    //SELECT
    public List<Movie> selectAll() {
        Log.i(TAG, "Selecting all rows in 'movie_data' view.");

        Cursor cursor = database.rawQuery(Statements.SELECT_ALL_MOVIE_DATA, null);
        List<Movie> movies = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = getInt(cursor, "_id");
            String title = getString(cursor, "title");
            boolean seen = getBool(cursor, "seen");
            String description = getString(cursor, "description");
            int userRating = getInt(cursor, "user_rating");
            int nativeRating = getInt(cursor, "native_rating");
            LocalDate release = getLocalDate(cursor, "release");

            Movie movie = new Movie(id, title, seen, description, nativeRating, userRating, release);

            String poster = getString(cursor, "poster");
            String backdrop = getString(cursor, "backdrop");
            int runtime = getInt(cursor, "runtime");
            String tagline = getString(cursor, "tagline");
            List<Integer> genres = getIntList(cursor, "genres");

            Information information = new Information(poster, backdrop, runtime, tagline, genres);

            movie.setInfo(information);
            movies.add(movie);

            cursor.moveToNext();
        }
        cursor.close();

        return movies;
    }

    public Movie selectMovie(int id) {
        Log.i(TAG, "Selecting single row in 'movie_data' view.");

        String[] arguments = { Integer.toString(id) };
        Cursor cursor = database.rawQuery(Statements.SELECT_MOVIE, arguments);

        cursor.moveToFirst();

        int _id = getInt(cursor, "_id");
        String title = getString(cursor, "title");
        boolean seen = getBool(cursor, "seen");
        String description = getString(cursor, "description");
        int userRating = getInt(cursor, "user_rating");
        int nativeRating = getInt(cursor, "native_rating");
        LocalDate release = getLocalDate(cursor, "release");

        Movie movie = new Movie(_id, title, seen, description, nativeRating, userRating, release);

        String poster = getString(cursor, "poster");
        String backdrop = getString(cursor, "backdrop");
        int runtime = getInt(cursor, "runtime");
        String tagline = getString(cursor, "tagline");
        List<Integer> genres = getIntList(cursor, "genres");

        Information information = new Information(poster, backdrop, runtime, tagline, genres);

        movie.setInfo(information);

        cursor.close();

        return movie;
    }

    public Information selectInformation(int id) {
        Log.i(TAG, "Selecting single row in 'movie_information' table.");

        String[] arguments = { Integer.toString(id) };
        Cursor cursor = database.rawQuery(Statements.SELECT_INFORMATION, arguments);

        cursor.moveToFirst();

        String poster = getString(cursor, "poster");
        String backdrop = getString(cursor, "backdrop");
        int runtime = getInt(cursor, "runtime");
        String tagline = getString(cursor, "tagline");
        List<Integer> genres = getIntList(cursor, "genres");

        Information information = new Information(poster, backdrop, runtime, tagline, genres);

        cursor.close();

        return information;
    }

    //UPDATE
    public void updateMovie(Movie... movies) {
        Log.i(TAG, "Beginning transaction to update rows in 'movies' table.");

        database.beginTransaction();
        try {
            for (Movie movie : movies) {
                ContentValues values = new ContentValues();

                values.put("title", movie.getTitle());
                values.put("seen", formatBoolean(movie.isSeen()));
                values.put("description", movie.getDescription());
                values.put("user_rating", movie.getUserRating());
                values.put("native_rating", movie.getNativeRating());
                values.put("release", movie.getRelease().toString());

                String[] argument = { Integer.toString(movie.getId()) };
                String clause = "_id = ?";

                Log.i(TAG, "Updating row with _id = " + movie.getId() + ".");
                database.update(Tables.MOVIES.toString(), values, clause, argument);
                updateInformation(new Pair<>(movie.getId(), movie.getInfo()));
            }

            Log.i(TAG, "Committing transaction.");
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.w(TAG, "Could not update values in 'movies' table.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        } finally {
            Log.i(TAG, "Ending transaction.");
            database.endTransaction();
        }

        Log.i(TAG, "Transaction complete.");
    }

    public void updateInformation(Map<Integer, Information> map) {
        Log.i(TAG, "Beginning transaction to update rows in 'movie_information' table.");

        database.beginTransaction();
        try {
            for (Map.Entry<Integer, Information> entry : map.entrySet()) {
                ContentValues values = new ContentValues();

                values.put("poster", entry.getValue().getPoster());
                values.put("backdrop", entry.getValue().getBackdrop());
                values.put("runtime", entry.getValue().getRuntime());
                values.put("tagline", entry.getValue().getTagline());
                values.put("genres", formatList(entry.getValue().getGenres()));

                String[] argument = { Integer.toString(entry.getKey()) };
                String clause = "movie_id = ?";

                Log.i(TAG, "Updating row with _id = " + entry.getKey() + ".");
                database.update(Tables.INFORMATION.toString(), values, clause, argument);
            }

            Log.i(TAG, "Committing transaction.");
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.w(TAG, "Could not update values in 'movie_information' table.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        } finally {
            Log.i(TAG, "Ending transaction.");
            database.endTransaction();
        }

        Log.i(TAG, "Transaction complete.");
    }

    private void updateInformation(Pair<Integer, Information> pair) {
        Log.i(TAG, "Updating single row in 'movie_information' table.");

        try {
            ContentValues values = new ContentValues();

            values.put("poster", pair.second.getPoster());
            values.put("backdrop", pair.second.getBackdrop());
            values.put("runtime", pair.second.getRuntime());
            values.put("tagline", pair.second.getTagline());
            values.put("genres", formatList(pair.second.getGenres()));

            String[] argument = { Integer.toString(pair.first) };
            String clause = "movie_id = ?";

            Log.i(TAG, "Updating row with _id = " + pair.first + ".");
            database.update(Tables.INFORMATION.toString(), values, clause, argument);
        } catch (SQLException e) {
            Log.w(TAG, "Could not update values in 'movie_information' table.");
            Log.e(TAG, "Exception '" + e.getMessage() + "' found.");
            throw new RuntimeException(e);
        }

        Log.i(TAG, "Update complete.");
    }

    //DELETE
    public void deleteMovie(int id) {
        Log.i(TAG, "Deleting single row in 'movies' table.");

        String[] arguments = { Integer.toString(id) };
        String clause = "_id = ?";
        database.delete(Tables.MOVIES.toString(), clause, arguments);

        Log.i(TAG, "Deleted movie with _id = " + id + " and associated movie information.");
    }

    public void deleteInformation(int id) {
        Log.i(TAG, "Deleting single row in 'movie_information' table.");

        String[] arguments = { Integer.toString(id) };
        String clause = "movie_id = ?";
        database.delete(Tables.INFORMATION.toString(), clause, arguments);

        Log.i(TAG, "Deleted movie information with movie_id = " + id + ".");
    }

    //OTHER CASES
    public void updateSeen(Movie movie) {
        Log.i(TAG, "Updating single row's seen value in 'movies' table.");

        ContentValues values = new ContentValues();
        values.put("seen", formatBoolean(movie.isSeen()));

        String[] argument = { Integer.toString(movie.getId()) };
        String clause = "_id = ?";

        database.update(Tables.MOVIES.toString(), values, clause, argument);

        Log.i(TAG, "Updated movie's seen value for movie_id = " + movie.getId() + ".");
    }
}
