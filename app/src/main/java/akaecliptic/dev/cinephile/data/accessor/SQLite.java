package akaecliptic.dev.cinephile.data.accessor;

import static akaecliptic.dev.cinephile.auxil.database.Functions.formatBoolean;
import static akaecliptic.dev.cinephile.auxil.database.Functions.formatList;
import static akaecliptic.dev.cinephile.auxil.database.Functions.getBool;
import static akaecliptic.dev.cinephile.auxil.database.Functions.getInt;
import static akaecliptic.dev.cinephile.auxil.database.Functions.getIntList;
import static akaecliptic.dev.cinephile.auxil.database.Functions.getLocalDate;
import static akaecliptic.dev.cinephile.auxil.database.Functions.getString;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import akaecliptic.dev.cinephile.auxil.database.Statements;
import akaecliptic.dev.cinephile.auxil.database.Tables;
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
 * <p>A newer implementation of {@link akaecliptic.dev.cinephile.data.SQLiteDAO} (now deprecated).</p>
 *
 * <p>
 *     All CRUD operations are synchronous and single threaded. For now the decision is to leave the responsibility
 *     of off-loading work off the UI thread to the repository class. This may change in a later version,
 *     but as of now (09/2022), this is the favoured approach.
 * </p>
 */
public class SQLite extends SQLiteOpenHelper {

    private static final String TAG = SQLite.class.getSimpleName();

    private static final String DATABASE_NAME = "_cinephile.db"; //Might remove underscore in future
    private static final int DATABASE_VERSION = 4;

    private static SQLiteDatabase database;
    private static SQLite sqlite;

    /*          MANAGEMENT          */

    private SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLite getInstance(Context context){
        if (sqlite == null) sqlite = new SQLite(context);

        sqlite.getWritableDatabase(); // TODO: 2022-10-05 Keep an eye on this.
        return sqlite;
    }

    // OVERRIDES

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        database = sqliteDatabase;
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

    @Override
    public void onOpen(SQLiteDatabase sqliteDatabase) {
        super.onOpen(sqliteDatabase);
        /*
            So, foreign keys are not on by default...
            So, this must also be called everytime the database is open...
         */
        database = sqliteDatabase;
        database.execSQL(Statements.PRAGMA_FOREIGN_KEY);
        database.setForeignKeyConstraintsEnabled(true);
    }

    // SET UP

    /**
     * Initialises tables in the database.
     *
     * @param database The current working database.
     */
    private void initialiseTables(SQLiteDatabase database) {
        database.execSQL(Statements.CREATE_TABLE_MOVIES);
        database.execSQL(Statements.CREATE_TABLE_INFORMATION);
        database.execSQL(Statements.CREATE_TABLE_COLLECTIONS);
        database.execSQL(Statements.CREATE_LINK_MOVIE_COLLECTION);

        database.execSQL(Statements.CREATE_VIEW_COLLECTION_DATA); //View for querying all collection data
        database.execSQL(Statements.CREATE_VIEW_MOVIE_DATA); //View for querying all movie data
    }

    /*          DATA ACCESS          */

    //INSERT
    public void insertMovie(@NonNull Movie movie) {
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

    public void insertInformation(@NonNull Pair<Integer, Information> pair) {
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

        if(!(cursor != null && cursor.moveToFirst())) return null;

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
            Log.e(TAG, "Exception '" + e + "' found.");
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
            Log.e(TAG, "Exception '" + e + "' found.");
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
            Log.e(TAG, "Exception '" + e + "' found.");
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

    public void updateRating(Movie movie) {
        Log.i(TAG, "Updating single row's user_rating value in 'movies' table.");

        ContentValues values = new ContentValues();
        values.put("user_rating", movie.getUserRating());

        String[] argument = { Integer.toString(movie.getId()) };
        String clause = "_id = ?";

        database.update(Tables.MOVIES.toString(), values, clause, argument);

        Log.i(TAG, "Updated movie's user_rating value for movie_id = " + movie.getId() + ".");
    }

    public List<Movie> query(String query) {
        Log.i(TAG, "Selecting rows in 'movie_data' view with '" + query + "' in title.");

        String[] argument = { query };
        Cursor cursor = database.rawQuery(Statements.SELECT_MOVIE_DATA_LIKE, argument);
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
}
