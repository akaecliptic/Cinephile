package akaecliptic.dev.cinephile.Auxiliary;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import akaecliptic.dev.cinephile.Super.BaseMovieAdapter;

public abstract class Matchers {

    public static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<RecyclerView.ViewHolder> itemWithClassName(final String name) {
        return new BoundedMatcher<>(BaseMovieAdapter.BaseMovieViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("View holder with class name: " + name);
            }

            @Override
            protected boolean matchesSafely(BaseMovieAdapter.BaseMovieViewHolder item) {
                return item.getClass().getSimpleName().equalsIgnoreCase(name);
            }
        };
    }
}
