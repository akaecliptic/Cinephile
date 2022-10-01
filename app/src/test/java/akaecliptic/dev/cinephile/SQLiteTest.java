package akaecliptic.dev.cinephile;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import akaecliptic.dev.cinephile.Architecture.Accessors.SQLite;

@RunWith(RobolectricTestRunner.class)
public class SQLiteTest {
    private SQLite sqlite;

    @Before
    public void init() {
        this.sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());
    }

    @Test
    public void t1() {
        SQLiteDatabase database = this.sqlite.getWritableDatabase();
        System.out.println(database.getPath());
    }
}
