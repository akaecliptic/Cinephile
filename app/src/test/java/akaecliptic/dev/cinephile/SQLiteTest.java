package akaecliptic.dev.cinephile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static akaecliptic.dev.cinephile.Auxiliary.Factory.movies;
import static akaecliptic.dev.cinephile.Auxiliary.IO.createDatabase;
import static akaecliptic.dev.cinephile.Auxiliary.IO.readFileLines;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import akaecliptic.dev.cinephile.Architecture.Accessors.SQLite;
import akaecliptic.dev.cinephile.Auxiliary.MockSQLiteDAO;
import dev.akaecliptic.models.Movie;

@RunWith(RobolectricTestRunner.class)
public class SQLiteTest {
    private static final String DATABASE_NAME = "_cinephile.db";
    private static final String OLD_DATABASE_NAME = "Cinephile.db";
    private static final String BACKUP_DATABASE_NAME = "OLD_DATABASE_COPY.db";

    private final List<akaecliptic.dev.cinephile.Model.Movie> movies = new ArrayList<>();

    private SQLite sqlite;
    private MockSQLiteDAO dao;

    private void initialiseOldDatabase() throws IOException {
        var path = RuntimeEnvironment.getApplication().getDataDir().getPath();
        var location = createDatabase(path);
        this.dao = new MockSQLiteDAO(location);

        var lines = readFileLines("movies.csv");
        this.movies.addAll( movies(lines, true) );
        this.movies.sort(Comparator.comparingInt(akaecliptic.dev.cinephile.Model.Media::getId));

        this.movies.forEach(this.dao::add);
    }

    private void initialiseDatabase() {
        this.dao.close();
        this.sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());
    }

    @Test
    public void test_databaseMigration() throws IOException {
        initialiseOldDatabase();
        verifyOldData();

        initialiseDatabase();
        verifyMigration();
        verifyData();
    }

    private void verifyMigration() {
        var path = this.sqlite.getWritableDatabase().getPath();

        assertTrue(path.contains(DATABASE_NAME));

        String old = path.replace(DATABASE_NAME, OLD_DATABASE_NAME);
        String backup = path.replace(DATABASE_NAME, BACKUP_DATABASE_NAME);

        assertTrue(new File(path).exists());

        assertFalse(new File(old).exists());
        assertTrue(new File(backup).exists());
    }

    void verifyOldData() {
        var o = this.dao.all();
        o.sort(Comparator.comparingInt(akaecliptic.dev.cinephile.Model.Media::getId));

        for (int i = 0; i < o.size(); i++) {
            String message = String.format("Movies do not match. ID Expected [ %s ] - Found [ %s ]", movies.get(i), o.get(i));
            assertEquals(message, movies.get(i), o.get(i));
        }
    }

    private void verifyData() {
        var n = this.sqlite.selectAll();
        n.sort(Comparator.comparingInt(Movie::getId));

        for (int i = 0; i < n.size(); i++) {
            String message1 = String.format(
                    "Movies do not match. ID Expected [ %s ] - Found [ %s ]",
                    movies.get(i).getId(),
                    n.get(i).getId()
            );
            assertEquals(
                    message1,
                    movies.get(i).getId(),
                    n.get(i).getId()
            );

            String message2 = String.format(
                    "Movies do not match. Title Expected [ %s ] - Found [ %s ]",
                    movies.get(i).getTitle(),
                    n.get(i).getTitle()
            );
            assertEquals(
                    message2,
                    movies.get(i).getTitle(),
                    n.get(i).getTitle()
            );
        }
    }
}
