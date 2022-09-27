package akaecliptic.dev.cinephile.Auxiliary.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

abstract public class Functions {

    private static final String TAG = Functions.class.getSimpleName();

    /**
     * Helper function to check if a given table exists in a given database.
     * Done by querying master table to check if the given table name exists with type table,
     * then checking the count of the answer.
     *
     * Anything greater than 0 returns true - the table exists.
     *
     * @param database - The database to check.
     * @param table - The table to check existence.
     * @return True if the table exists, false otherwise.
     */
    public static boolean doesTableExist(SQLiteDatabase database, String table) {
        String query = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? COLLATE NOCASE;";

        Cursor c = database.rawQuery(query, new String[]{ table });
        int count = c.getCount();

        c.close();

        return count > 0;
    }
}
