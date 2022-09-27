package akaecliptic.dev.cinephile.Architecture.Accessors;

import static akaecliptic.dev.cinephile.Auxiliary.Database.Functions.doesTableExist;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Migration.migrateMovieInformation;
import static akaecliptic.dev.cinephile.Auxiliary.Database.Migration.migrateMovies;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import akaecliptic.dev.cinephile.Auxiliary.Database.Statements;
import akaecliptic.dev.cinephile.Auxiliary.Database.Tables;

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
        sqlite.getWritableDatabase();
        return sqlite;
    }

    // OVERRIDES

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        database = sqliteDatabase;

        if(DATABASE_VERSION == 3) checkOldDatabase();
        initialiseTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion == DATABASE_VERSION) initialiseTables();
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
     */
    private void checkOldDatabase() {
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
     */
    private void initialiseTables() {
        if(DATABASE_VERSION == 3 && updateDatabase) {
            migrateTables();
        }

        database.execSQL(Statements.PRAGMA_FOREIGN_KEY); //So, foreign keys are not on by default...
        database.execSQL(Statements.CREATE_TABLE_MOVIES);
        database.execSQL(Statements.CREATE_TABLE_INFORMATION);
        database.execSQL(Statements.CREATE_VIEW_MOVIE_DATA); //View for querying all movie data
    }

    /**
     * Migrates old data to new table schema preserving data where possible.
     */
    private void migrateTables() {

        boolean hasOldMovies = doesTableExist(database, "old_movies");
        boolean hasMovies = doesTableExist(database, "movies");
        boolean hasInformation = doesTableExist(database, "movie_information");

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
        if(!hasInformation) migrateMovieInformation(database);
    }

    /*          DATA ACCESS          */

    //INSERT
    void insertMovie() {}
    void insertInformation() {}

    //SELECT
    void selectAll() {
        Cursor cursor = database.rawQuery(Statements.SELECT_ALL_MOVIE_DATA, null);

        cursor.moveToFirst();
        //Processing

        cursor.close();
        //Return
    }

    void selectMovie(int id) {
        String[] arguments = { Integer.toString(id) };
        Cursor cursor = database.rawQuery(Statements.SELECT_MOVIE, arguments);

        cursor.moveToFirst();
        //Processing

        cursor.close();
        //Return
    }

    void selectInformation(int id) {
        String[] arguments = { Integer.toString(id) };
        Cursor cursor = database.rawQuery(Statements.SELECT_INFORMATION, arguments);

        cursor.moveToFirst();
        //Processing

        cursor.close();
        //Return
    }

    //UPDATE
    void updateMovie() {}
    void updateInformation() {}

    //DELETE
    void deleteMovie(int id) {
        String[] arguments = { Integer.toString(id) };
        String clause = "_id = ?";
        database.delete(Tables.MOVIES.toString(), clause, arguments);
    }

    void deleteInformation(int id) {
        String[] arguments = { Integer.toString(id) };
        String clause = "movie_id = ?";
        database.delete(Tables.INFORMATION.toString(), clause, arguments);
    }
}
