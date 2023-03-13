package akaecliptic.dev.cinephile.Database;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.junit.Assert.assertFalse;
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
import akaecliptic.dev.cinephile.model.Collection;
import akaecliptic.dev.cinephile.model.Collection.Cover;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

@OrderWith(Alphanumeric.class)
@RunWith(RobolectricTestRunner.class)
public class CollectionTest {
    private SQLite sqlite = null;
    private Movie movie1;
    private Movie movie2;
    private Movie movie3;

    @Before
    public void init() {
        if (this.sqlite != null) return;

        this.sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());

        this.movie1 = new Movie(
                1, "Movie 1", true,
                "Test Movie 1", 90, 70,
                LocalDate.now(), new Information()
        );
        this.movie2 = new Movie(
                2, "Movie 2", true,
                "Test Movie 2", 50, 30,
                LocalDate.now(), new Information()
        );
        this.movie3 = new Movie(
                3, "Movie 3", true,
                "Test Movie 3", 65, 80,
                LocalDate.now(), new Information()
        );

        this.sqlite.insertMovie(movie1);
        this.sqlite.insertMovie(movie2);
        this.sqlite.insertMovie(movie3);
    }

    @After
    public void close() {
        if (this.sqlite == null) return;

        sqlite.deleteMovie(1);
        sqlite.deleteMovie(2);
        sqlite.deleteMovie(3);

        this.sqlite.close();
    }

    /*          COLLECTION INSERTS           */

    @Test
    public void test_favouritesExists() {
        var collections = this.sqlite.selectCollections();

        assertFalse(collections.isEmpty());

        var select = this.sqlite.selectCollection("favourites");
        var expect = new Collection("favourites", Cover.HEART);

        assertThat(select, notNullValue());
        assertThat(select, is(expect));
    }

    @Test
    public void test_insertCollection() {
        Collection insert = new Collection("Good Movies", Cover.GOOD);
        this.sqlite.insertCollection(insert);

        var collections = this.sqlite.selectCollections();
        boolean present = collections.contains(insert);

        assertFalse(collections.isEmpty());
        assertTrue(present);
    }

    @Test
    public void test_updateCollection() {
        Collection insert = new Collection("Bad Movies", Cover.GOOD);
        this.sqlite.insertCollection(insert);

        var select = this.sqlite.selectCollection("Bad Movies");

        assertThat(select, notNullValue());

        select.setCover(Cover.BAD);
        this.sqlite.updateCollection(select);

        var update = this.sqlite.selectCollection("Bad Movies");

        assertThat(select, notNullValue());
        assertThat(update, both(is(select)).and(not(insert)));
    }

    @Test
    public void test_addToCollection() {
        this.sqlite.addToCollection(1, "favourites");
        this.sqlite.addToCollection(2, "favourites");
        var favourites = this.sqlite.selectCollection("favourites");

        assertThat(favourites, notNullValue());
        assertThat(favourites.getMembers(), hasItems(1, 2));
    }

    @Test
    public void test_removeFromCollection() {
        this.sqlite.addToCollection(3, "favourites");
        var favourites = this.sqlite.selectCollection("favourites");

        assertThat(favourites, notNullValue());
        assertThat(favourites.getMembers(), hasItem(3));

        this.sqlite.removeFromCollection(3, "favourites");
        var updated = this.sqlite.selectCollection("favourites");

        assertThat(updated, notNullValue());
        assertThat(updated.getMembers(), is(emptyIterable()));
    }

    @Test
    public void test_selectMoviesFromCollections() {
        Collection insert = new Collection("Some Movies");
        this.sqlite.insertCollection(insert);

        var movies = this.sqlite.selectMoviesFromCollection(insert.getName());

        assertThat(movies, is(emptyIterable()));

        this.sqlite.addToCollection(1, insert.getName());
        this.sqlite.addToCollection(3, insert.getName());

        movies = this.sqlite.selectMoviesFromCollection(insert.getName());

        assertThat(movies, not(emptyIterable()));
        assertThat(movies.size(), is(2));
        assertThat(movies, hasItems(this.movie1, this.movie3));
    }

}
