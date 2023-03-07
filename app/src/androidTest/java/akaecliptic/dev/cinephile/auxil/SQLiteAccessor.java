package akaecliptic.dev.cinephile.auxil;

import android.content.Context;

import java.util.List;

import akaecliptic.dev.cinephile.data.accessor.SQLite;
import dev.akaecliptic.models.Movie;

public abstract class SQLiteAccessor {

    private static SQLite connect(Context context) {
        return SQLite.getInstance(context);
    }

    public static List<Movie> selectAll(Context context) {
        SQLite sqlite = connect(context);
        return sqlite.selectAll();
    }

    public static Movie select(Context context, int id) {
        SQLite sqlite = connect(context);
        return sqlite.selectMovie(id);
    }

    public static void insert(Context context, Movie movie) {
        SQLite sqlite = connect(context);
        sqlite.insertMovie(movie);
    }
}
