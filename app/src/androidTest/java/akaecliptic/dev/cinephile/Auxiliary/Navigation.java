package akaecliptic.dev.cinephile.Auxiliary;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import androidx.test.espresso.action.ViewActions;

public abstract class Navigation {

    public static void goBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }
}
