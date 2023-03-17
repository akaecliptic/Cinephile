package akaecliptic.dev.cinephile.Flow.Delete;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNull;
import static akaecliptic.dev.cinephile.auxil.Matchers.childAtPosition;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.auxil.SQLiteAccessor;
import akaecliptic.dev.cinephile.R;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

@RunWith(AndroidJUnit4.class)
public class FlowDeleteMovieTest {

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
    public void flow_test_deleteMovieWatchlist() {
        // Select dummy movie
        ViewInteraction recyclerItemExplore = onView(childAtPosition(withId(R.id.collections_recycler), 0));
        recyclerItemExplore.perform(longClick());

        // Confirm delete
        ViewInteraction buttonRating = onView(withId(R.id.delete_dialog_button_confirm));
        buttonRating.perform(click());

        // Query database for movie
        Context context = ApplicationProvider.getApplicationContext();
        Movie movie = SQLiteAccessor.select(context, MOVIE_ID);

        // Check movie is null
        assertNull(movie);
    }
}
