package akaecliptic.dev.cinephile.Flow.Update;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static akaecliptic.dev.cinephile.auxil.Matchers.childAtPosition;
import static akaecliptic.dev.cinephile.auxil.Navigation.goBack;

import android.content.Context;

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

import java.time.LocalDate;

import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.auxil.SQLiteAccessor;
import akaecliptic.dev.cinephile.R;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

@LargeTest
@RunWith(AndroidJUnit4.class)
@OrderWith(Alphanumeric.class)
public class FlowUpdateMovieRatingTest {

    private static final int MOVIE_ID = 1;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        ApplicationProvider.getApplicationContext().deleteDatabase("cinephile.db");

        // Insert new dummy movie
        Context context = ApplicationProvider.getApplicationContext();
        Movie movie = new Movie(MOVIE_ID, "New Movie", true, "Test Movie", 14, 35, LocalDate.now(), new Information());
        SQLiteAccessor.insert(context, movie);
    }

    @Test
    public void flow_test_1_cantUpdateRating() {
        // Navigate to explore fragment
        ViewInteraction bottombarExplore = onView(withId(R.id.explore_fragment));
        bottombarExplore.perform(click());

        // Select the first movie
        ViewInteraction recyclerItemExplore = onView(childAtPosition(withId(R.id.explore_grid), 0));
        recyclerItemExplore.perform(click());

        // Select rating button
        ViewInteraction buttonRating = onView(withId(R.id.movie_profile_frame_progress));
        buttonRating.perform(click());

        // Check rating panel does not exist
        ViewInteraction dialogTitle = onView(allOf(withId(R.id.rating_dialog_text_title), withText(R.string.dialog_title_rating)));
        dialogTitle.check(doesNotExist());
    }

    @Test
    public void flow_test_2_updateRatingWatchlist() {
        // Select dummy movie
        ViewInteraction recyclerItemExplore = onView(childAtPosition(withId(R.id.watchlist_recycler), 0));
        recyclerItemExplore.perform(click());

        // Select rating button
        ViewInteraction buttonRating = onView(withId(R.id.movie_profile_frame_progress));
        buttonRating.perform(click());

        // Enter new rating
        ViewInteraction dialogRating = onView(withId(R.id.rating_dialog_edit_rating));
        dialogRating.perform(clearText());
        dialogRating.perform(typeText("90"));

        // Confirm new rating
        ViewInteraction dialogConfirm = onView(withId(R.id.rating_dialog_button_confirm));
        dialogConfirm.perform(click());

        // Go back to watchlist fragment
        goBack();

        // Query database for movie
        Context context = ApplicationProvider.getApplicationContext();
        Movie movie = SQLiteAccessor.select(context, MOVIE_ID);

        // Check user rating was updated
        assertEquals(90, movie.getUserRating());
    }
}
