package akaecliptic.dev.cinephile.Database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.time.LocalDate;

import akaecliptic.dev.cinephile.data.accessor.SQLite;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

@OrderWith(Alphanumeric.class)
@RunWith(RobolectricTestRunner.class)
public class InsertTest {
    private SQLite sqlite = null;

    /*
        This test class must be run as a whole, sequentially to work.
     */

    @Before
    public void init() {
        if(this.sqlite != null) return;

        this.sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());
    }

    @After
    public void close() {
        if(this.sqlite != null) this.sqlite.close();
    }

    /*          MOVIE INSERTS           */

    @Test
    public void test_1_insertNewMovie() {
        // Movie parameters
        var id = 1;
        var title = "New Movie";
        var description = "This is a test Movie";
        var nativeRating = 14;
        var userRating = 35;
        var release = LocalDate.now();
        var information = new Information();

        // Instantiate dummy movie
        Movie movie = new Movie(id, title, true, description, nativeRating, userRating, release, information);

        // Insert movie into database
        this.sqlite.insertMovie(movie);

        // Query the movie from database
        Movie query = this.sqlite.selectMovie(id);

        // Asserts
        assertEquals(id, query.getId());
        assertEquals(title, query.getTitle());
        assertEquals(description, query.getDescription());

        assertTrue(query.isSeen());

        assertEquals(nativeRating, query.getNativeRating());
        assertEquals(userRating, query.getUserRating());

        assertEquals(release, query.getRelease());
    }

    @Test
    public void test_2_insertMovieClash() {
        // Movie parameters
        var id = 1;
        var title = "New Movie 2: Electric Boogaloo";
        var description = "This is a clashing Movie";
        var nativeRating = 99;
        var userRating = 99;
        var release = LocalDate.now();
        var information = new Information();

        // Instantiate clash movie
        Movie movie = new Movie(id, title, false, description, nativeRating, userRating, release, information);

        // Insert movie into database
        this.sqlite.insertMovie(movie);

        // Assert there is only one movie in database
        var movies = this.sqlite.selectMovies();
        assertEquals(1, movies.size());

        // Query the movie from database
        Movie query = this.sqlite.selectMovie(id);

        // Assert movie values were not overridden from previous test case
        assertEquals(id, query.getId());
        assertEquals(release, query.getRelease()); // Grouping values that should be the same

        assertNotEquals(title, query.getTitle());
        assertNotEquals(description, query.getDescription());

        assertTrue(query.isSeen());

        assertNotEquals(nativeRating, query.getNativeRating());
        assertNotEquals(userRating, query.getUserRating());
    }

    @Test
    public void test_3_insertMovieNullData() {
        /*
            There is a error caused by having a null 'release' in SQLite class.
            I've decided to push the fix back to the library responsible for Movie.

            Also, information being passed as null should be handled in Information tests further below.
         */

        // Movie parameters
        var id = 2;
        var title = "Home Wrecker Movie";
        var nativeRating = 21;
        var userRating = 21;
        var release = LocalDate.now();
        var information = new Information();

        // Instantiate clash movie
        Movie movie = new Movie(id, title, true, null, nativeRating, userRating, release, information);

        // Insert movie into database
        this.sqlite.insertMovie(movie);

        // Assert new movie was correctly added to database
        var movies = this.sqlite.selectMovies();
        assertEquals(2, movies.size());

        // Query the movie from database
        Movie query = this.sqlite.selectMovie(id);

        // Assert valid movie values are all correct
        assertEquals(id, query.getId());
        assertEquals(title, query.getTitle());

        assertTrue(query.isSeen());

        assertEquals(nativeRating, query.getNativeRating());
        assertEquals(userRating, query.getUserRating());

        assertEquals(release, query.getRelease());

        // Assert null data value
        assertNull(query.getDescription());

        this.sqlite.deleteMovie(1);
        this.sqlite.deleteMovie(2);
    }

    /*          INFORMATION INSERTS           */
}
