package akaecliptic.dev.cinephile.auxil.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class Functions {

    /**
     * Helper function to check if a given table exists in a given database.
     * Done by querying master table to check if the given table name exists with type table,
     * then checking the count of the answer.
     *
     * <p>Anything greater than 0 returns true - the table exists.</p>
     *
     * @param database The database to check.
     * @param table    The table to check existence.
     * @return True if the table exists, false otherwise.
     */
    public static boolean doesTableExist(SQLiteDatabase database, String table) {
        String query = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? COLLATE NOCASE;";

        Cursor c = database.rawQuery(query, new String[]{table});
        int count = c.getCount();

        c.close();

        return count > 0;
    }

    /**
     * Helper function to create a map for getting data from cursor position.
     * Maps column names to their index.
     *
     * @param cursor The cursor from query to get column information from.
     * @return Map of column names to their index.
     */
    public static Map<String, Integer> nameIndexMap(Cursor cursor) {
        String[] names = cursor.getColumnNames();
        Map<String, Integer> map = new HashMap<>();

        for (String name : names) {
            map.put(name, cursor.getColumnIndex(name));
        }

        return map;
    }

    /**
     * Helper function to format lists for database persistence.
     *
     * @param list The list of int to format.
     * @return String value of list in comma separated values, null if list is empty.
     */
    public static String formatList(List<Integer> list) {
        if (list.isEmpty()) return null;

        String temp = list.toString();
        return temp.substring(1, temp.length() - 1);
    }

    /**
     * Helper function to format booleans for database persistence.
     *
     * @param value The boolean value to format.
     * @return Int value of boolean, true = 1 and false = 0.
     */
    public static int formatBoolean(boolean value) {
        return (value) ? 1 : 0;
    }

    //CURSOR ACCESS
    /*
        These functions are used as an alternative to only being able to get data by index from a cursor.
        The column name passed is used to get an index. The index is checked for null value, and parsed.
     */

    /**
     * Helper function to get int at cursor position for specified column.
     *
     * @param cursor The cursor from the query to get data.
     * @param column The column to get data.
     * @return Integer value of the data, -1 if null.
     */
    public static int getInt(Cursor cursor, String column) {
        int index = cursor.getColumnIndexOrThrow(column);
        return (cursor.isNull(index)) ? -1 : cursor.getInt(index);
    }

    /**
     * Helper function to get String at cursor position for specified column.
     *
     * @param cursor The cursor from the query to get data.
     * @param column The column to get data.
     * @return String value of the data, null if null.
     */
    public static String getString(Cursor cursor, String column) {
        int index = cursor.getColumnIndexOrThrow(column);
        return (cursor.isNull(index)) ? null : cursor.getString(index);
    }

    /**
     * Helper function to get boolean at cursor position for specified column.
     *
     * @param cursor The cursor from the query to get data.
     * @param column The column to get data.
     * @return Boolean value of the data, false if null.
     */
    public static boolean getBool(Cursor cursor, String column) {
        int index = cursor.getColumnIndexOrThrow(column);
        return !cursor.isNull(index) && (cursor.getInt(index) == 1);
    }

    /**
     * Helper function to get LocalDate at cursor position for specified column.
     *
     * @param cursor The cursor from the query to get data.
     * @param column The column to get data.
     * @return LocalDate value of the data, null if null.
     */
    public static LocalDate getLocalDate(Cursor cursor, String column) {
        int index = cursor.getColumnIndexOrThrow(column);
        return (cursor.isNull(index)) ? null : LocalDate.parse(cursor.getString(index));
    }

    /**
     * Helper function to get IntegerList at cursor position for specified column.
     *
     * @param cursor The cursor from the query to get data.
     * @param column The column to get data.
     * @return IntegerList value of the data, empty list if null.
     */
    public static List<Integer> getIntList(Cursor cursor, String column) {
        int index = cursor.getColumnIndexOrThrow(column);
        if (cursor.isNull(index)) return new ArrayList<>();

        List<Integer> list = new ArrayList<>();
        String[] array = cursor.getString(index).split(",");

        for (String data : array) {
            list.add(Integer.parseInt(data.trim()));
        }

        return list;
    }

    /**
     * Helper function to prepare a cursor for operations,
     * checking whether a cursor is safe to retrieve data from,
     * and safely closing the cursor otherwise.
     *
     * @param cursor The cursor from the query to validate
     * @return True if the cursor is safe to preform operations, false otherwise.
     */
    public static boolean isValidCursor(Cursor cursor) {
        boolean valid = cursor != null && cursor.moveToFirst();
        if (!valid && cursor != null) cursor.close();
        return valid;
    }

    /**
     * Helper function that builds a query string to select a group of movies from a set of ids.
     *
     * @param count The number of arguments to template
     * @return Formatted string with the number of argument templates equal to given count
     */
    public static String selectMovieWhereIn(int count) {
        StringBuilder base = new StringBuilder("SELECT * FROM 'movie_data' WHERE _id IN (");
        for (int i = 0; i < count; i++) {
            base.append(" ?");
            if (i != (count - 1)) base.append(",");
        }
        base.append(" )");

        return base.toString();
    }
}
