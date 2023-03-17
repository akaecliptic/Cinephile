package akaecliptic.dev.cinephile.interaction;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.activity.SearchActivity;
import akaecliptic.dev.cinephile.R;

/**
 * IAnimatorBottombar is the isolation of animating the bottombar when transitioning between fragments.
 * This feature, as of now (2022-10-14), is only required in two fragments -
 * {@link akaecliptic.dev.cinephile.fragment.MovieProfileFragment},
 * {@link akaecliptic.dev.cinephile.fragment.MovieRowFragment}.
 *
 * <p>
 *     As a result it didn't feel quite right to add this code to {@link akaecliptic.dev.cinephile.base.BaseFragment}.
 *     Given the amount of variables and method calls it is composed off, it seems appropriate as an interface.
 *     At least for now.
 * </p>
 */
public interface IAnimatorBottombar {

    // Animation directions
    int ANIMATE_IN = 1;
    int ANIMATE_OUT = 0;

    /**
     * Used to attach a listener to the current activities navigation controller,
     * via {@link androidx.navigation.NavController#addOnDestinationChangedListener(NavController.OnDestinationChangedListener)}.
     *
     * The listener checks the navigation target's id versus a set of fragment ids that implement this interface.
     * Then animates the bottombar accordingly.
     *
     * @param fragmentActivity The current working activity.
     */
    default void attachAnimator(FragmentActivity fragmentActivity) {
        if (fragmentActivity.getClass() != SearchActivity.class) {

            MainActivity activity = (MainActivity) fragmentActivity;
            BottomNavigationView bottombar = activity.getBottombar();

            Set<Integer> destinations = new HashSet<>();
            destinations.add(R.id.movie_profile_fragment);
            destinations.add(R.id.movie_row_fragment);

            activity.getNavigationController()
                    .addOnDestinationChangedListener((controller, destination, arguments) -> {
                        int direction = (destinations.contains(destination.getId())) ?
                                ANIMATE_OUT :
                                ANIMATE_IN;

                        animateBottombar(direction, bottombar);
                    });
        }
    }

    /**
     * Used to animate the bottombar. The passed direction is used to calculate how the bottombar should be animated.
     *
     * @param direction The direction of the animation, i.e IN or OUT.
     * @param bottombar The bottombar to animate.
     */
    default void animateBottombar(int direction, BottomNavigationView bottombar) {
        int translateY = (direction == ANIMATE_IN) ? 0 : bottombar.getHeight();
        float alpha = (direction == ANIMATE_IN) ? 1.0f : 0.0f;
        int duration = (direction == ANIMATE_IN) ? 200 : 0;
        AnimatorListenerAdapter bottombarListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (direction == ANIMATE_IN) bottombar.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (direction == ANIMATE_OUT) bottombar.setVisibility(GONE);
            }
        };

        bottombar.animate()
                .translationY(translateY)
                .alpha(alpha)
                .setDuration(duration)
                .setListener(bottombarListener);
    }
}
