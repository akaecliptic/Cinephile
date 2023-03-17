package akaecliptic.dev.cinephile.Database;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.time.LocalDate;
import java.util.List;

import akaecliptic.dev.cinephile.data.accessor.SQLite;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

@OrderWith(Alphanumeric.class)
@RunWith(RobolectricTestRunner.class)
public class SelectTest {
    private SQLite sqlite = null;

    @Before
    public void init() {
        if(this.sqlite != null) return;

        this.sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());
    }

    @After
    public void close() {
        if(this.sqlite != null) this.sqlite.close();
    }

    /*          SELECTS ON EMPTY DATABASE           */

    @Test
    public void test_selectInvalidMovie() {
        Movie movie = this.sqlite.selectMovie(1);
        assertThat(movie, nullValue());
    }

    @Test
    public void test_selectInvalidInformation() {
        Information information = this.sqlite.selectInformation(1);
        assertThat(information, nullValue());
    }

    @Test
    public void test_selectMoviesEmpty() {
        List<Movie> movies = this.sqlite.selectMovies();
        assertThat(movies, notNullValue());
        assertThat(movies, is(emptyIterable()));
    }

    @Test
    public void test_selectMoviesWhereIn() {
        var movie1 = new Movie(
                1, "Movie 1", true,
                "Test Movie 1", 90, 70,
                LocalDate.now(), new Information()
        );
        var movie2 = new Movie(
                2, "Movie 2", true,
                "Test Movie 2", 50, 30,
                LocalDate.now(), new Information()
        );
        var movie3 = new Movie(
                3, "Movie 3", true,
                "Test Movie 3", 65, 80,
                LocalDate.now(), new Information()
        );

        this.sqlite.insertMovie(movie1);
        this.sqlite.insertMovie(movie2);
        this.sqlite.insertMovie(movie3);

        var movies = this.sqlite.selectMoviesWhereIn(movie1.getId(), movie3.getId());
        assertThat(movies, notNullValue());
        assertThat(movies.size(), is(2));
        assertThat(movies, hasItems(movie1, movie3));

        this.sqlite.deleteMovie(movie1.getId());
        this.sqlite.deleteMovie(movie2.getId());
        this.sqlite.deleteMovie(movie3.getId());
    }

}
