package akaecliptic.dev.cinephile.Flow.Add;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static akaecliptic.dev.cinephile.Auxiliary.Matchers.childAtPosition;
import static akaecliptic.dev.cinephile.Auxiliary.Matchers.itemWithClassName;
import static akaecliptic.dev.cinephile.Auxiliary.Navigation.goBack;
import static akaecliptic.dev.cinephile.Auxiliary.SQLiteAccessor.selectAll;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;

import java.util.List;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Wrapper.FooterMore;
import dev.akaecliptic.models.Movie;

@LargeTest
@RunWith(AndroidJUnit4.class)
@OrderWith(Alphanumeric.class)
public class FlowAddMovieExploreTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        ApplicationProvider.getApplicationContext().deleteDatabase("cinephile.db");
    }

    @Test
    public void flow_test_1_addMovieFromExplore() {
        // Navigate to explore fragment
        ViewInteraction bottombarExplore = onView(withId(R.id.explore_fragment));
        bottombarExplore.perform(click());

        // Select the first movie
        ViewInteraction recyclerItemExplore = onView(childAtPosition(withId(R.id.explore_grid), 0));
        recyclerItemExplore.perform(click());

        // Add movie to watchlist
        ViewInteraction buttonAdd = onView(withId(R.id.movie_profile_frame_add));
        buttonAdd.perform(click());

        // Go back to explore fragment
        goBack();

        // Go back to watchlist fragment
        goBack();

        // Select the added movie
        ViewInteraction recyclerItemWatchlist = onView(childAtPosition(withId(R.id.watchlist_recycler), 0));
        recyclerItemWatchlist.perform(click());

        // Mark movie as seen
        ViewInteraction buttonSeen = onView(withId(R.id.movie_profile_frame_seen));
        buttonSeen.perform(click());

        // Go back to watchlist fragment
        goBack();

        // Get all movies in watchlist
        List<Movie> movies = selectAll(ApplicationProvider.getApplicationContext());

        // Assert watchlist has the single item
        assertEquals(1, movies.size());

        // Assert added movie was marked as seen
        Movie movie = movies.get(0);
        assertTrue(movie.isSeen());
    }

    @Test
    public void flow_test_2_addMovieFromExploreRow() {
        // Navigate to explore fragment
        ViewInteraction bottombarExplore = onView(withId(R.id.explore_fragment));
        bottombarExplore.perform(click());

        // Select top rated section of explore
        ViewInteraction buttonRated = onView(withId(R.id.explore_rated));
        buttonRated.perform(click());

        // Scroll to footer position
        ViewInteraction recyclerGrid = onView(withId(R.id.explore_grid));
        recyclerGrid.perform(scrollToHolder(itemWithClassName(FooterMore.class.getSimpleName())));

        // Select more button
        ViewInteraction buttonFooter = onView(withId(R.id.footer_button));
        buttonFooter.perform(click());

        // Scroll to the nth position and select item
        ViewInteraction recyclerRow = onView(withId(R.id.movie_row_recycler));
        recyclerRow.perform(scrollToPosition(10));
        recyclerRow.perform(actionOnItemAtPosition(10, click()));

        // Add movie to watchlist
        ViewInteraction buttonAdd = onView(withId(R.id.movie_profile_frame_add));
        buttonAdd.perform(click());

        // Go back to card row fragment
        goBack();

        // Go back to explore fragment
        goBack();

        // Go back to watchlist fragment
        goBack();

        // Get all movies in watchlist
        List<Movie> movies = selectAll(ApplicationProvider.getApplicationContext());

        // Assert watchlist has both items
        assertEquals(2, movies.size());
    }
}
