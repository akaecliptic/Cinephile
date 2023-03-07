package akaecliptic.dev.cinephile.Database;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

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
        if(sqlite != null) return;

        sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());
    }

    /*          SELECTS ON EMPTY DATABASE           */

    @Test
    public void test_selectInvalidMovie() {
        Movie movie = this.sqlite.selectMovie(1);
        assertThat(movie, nullValue());
        this.sqlite.close();
    }

    @Test
    public void test_selectInvalidInformation() {
        Information information = this.sqlite.selectInformation(1);
        assertThat(information, nullValue());
        this.sqlite.close();
    }

    @Test
    public void test_selectMoviesEmpty() {
        List<Movie> movies = this.sqlite.selectMovies();
        assertThat(movies, notNullValue());
        assertThat(movies, is(emptyIterable()));
        this.sqlite.close();
    }

}
