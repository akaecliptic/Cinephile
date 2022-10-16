package akaecliptic.dev.cinephile.Auxiliary;

import android.content.Context;

import java.util.List;

import akaecliptic.dev.cinephile.Architecture.Accessors.SQLite;
import dev.akaecliptic.models.Movie;

public abstract class SQLiteAccessor {

    private static SQLite connect(Context context) {
        return SQLite.getInstance(context);
    }

    public static List<Movie> selectAll(Context context) {
        SQLite sqlite = connect(context);
        return sqlite.selectAll();
    }
}
