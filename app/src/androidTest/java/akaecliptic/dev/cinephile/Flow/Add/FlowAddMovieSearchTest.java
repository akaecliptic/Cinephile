package akaecliptic.dev.cinephile.Flow.Add;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static akaecliptic.dev.cinephile.auxil.Navigation.goBack;
import static akaecliptic.dev.cinephile.auxil.SQLiteAccessor.selectAll;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.R;
import dev.akaecliptic.models.Movie;

@RunWith(AndroidJUnit4.class)
public class FlowAddMovieSearchTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        ApplicationProvider.getApplicationContext().deleteDatabase("cinephile.db");
    }

    @Test
    public void flow_test_addMovieFromSearch() {
        String query = "Logan";
        String year = "2017";

        // Select toolbar search item
        ViewInteraction toolbarSearch = onView(withId(R.id.toolbar_search));
        toolbarSearch.perform(click());

        // Enter in movie title and search
        ViewInteraction searchbar = onView(withId(androidx.appcompat.R.id.search_src_text));
        searchbar.perform(typeText(query));
        searchbar.perform(pressImeActionButton());

        /*
            I don't like this, but this section has to wait for the query to return its result.
            The wait time is arbitrary and clunky. This will need to be revisited, but I'll leave it for now.

            2022-10-17
         */
        // CONSIDER: Rework this. See comment above.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Select item matching search string
        ViewInteraction searchItem = onView(allOf(
                withText(query),
                withId(R.id.movie_card_row_text_title),
                withParent(withId(R.id.movie_card_row_container_text)),
                hasSibling(allOf(withId(R.id.movie_card_row_text_year), withText(year)))
        ));
        searchItem.perform(click());

        // Add movie to watchlist
        ViewInteraction buttonAdd = onView(withId(R.id.movie_profile_frame_add));
        buttonAdd.perform(click());

        // Mark movie as seen
        ViewInteraction buttonSeen = onView(withId(R.id.movie_profile_frame_seen));
        buttonSeen.perform(click());

        // Go back to search fragment
        goBack();

        // Go back to watchlist fragment
        goBack();

        // (Not needed) Close keyboard
        searchbar.perform(pressBack());

        // Get all movies in watchlist
        List<Movie> movies = selectAll(ApplicationProvider.getApplicationContext());

        // Assert watchlist has the single items
        assertEquals(1, movies.size());

        // Assert added movie was marked as seen
        Movie movie = movies.get(0);
        assertTrue(movie.isSeen());
    }
}
