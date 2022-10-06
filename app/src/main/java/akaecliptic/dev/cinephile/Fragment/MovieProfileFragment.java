package akaecliptic.dev.cinephile.Fragment;

import static akaecliptic.dev.cinephile.Fragment.WatchListFragment.SELECTED_MOVIE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.Activity.SearchActivity;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;
import dev.akaecliptic.models.Movie;

public class MovieProfileFragment extends BaseFragment {

    private static final int ANIMATE_IN = 1;
    private static final int ANIMATE_OUT = 0;

    private Movie working = null;

    private BottomNavigationView bottombar;

    private ImageView poster;
    private ImageView backdrop;

    private TextView title;
    private TextView year;
    private TextView information;

    private ProgressBar ratingProgress;
    private TextView ratingText;
    private CheckBox seen;
    private CheckBox heart;

    private ViewGroup genreContainer;

    /*          OVERRIDES          */

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_movie_profile;
    }

    @Override
    protected void beforeViews() {
        attachAnimator();
        getBundle();
    }

    @Override
    protected void initViews(View view) {
        poster = view.findViewById(R.id.movie_profile_poster);
        backdrop = view.findViewById(R.id.movie_profile_backdrop);

        title = view.findViewById(R.id.movie_profile_text_title);
        year = view.findViewById(R.id.movie_profile_text_year);
        information = view.findViewById(R.id.movie_profile_text_information);

        ratingProgress = view.findViewById(R.id.movie_profile_progress_rating);
        ratingText = view.findViewById(R.id.movie_profile_text_rating);
        seen = view.findViewById(R.id.movie_profile_check_seen);
        heart = view.findViewById(R.id.movie_profile_button_heart);

        genreContainer = view.findViewById(R.id.movie_profile_container_genre);
    }

    @Override
    protected void afterViews(View view) {
        title.setText(working.getTitle());
        year.setText(String.valueOf(working.getRelease().getYear()));
        information.setText(working.getInfo().toStringPretty());

        // TODO: 2022-10-06 Change to user rating.
        ratingProgress.setProgress(working.getNativeRating());
        ratingText.setText(String.valueOf(working.getNativeRating()));
        seen.setChecked(working.isSeen());

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        working.getInfo().getGenres().forEach(genre -> {
            TextView pill = (TextView) inflater.inflate(R.layout.component_pill, genreContainer, false);
            pill.setText(viewModel.genres().get(genre));
            genreContainer.addView(pill);
        });
    }

    /*          INSTANCE METHODS          */

    private void attachAnimator() {
        if (requireActivity().getClass() != SearchActivity.class) {
            MainActivity activity = (MainActivity) requireActivity();
            bottombar = activity.getBottombar();

            activity.getNavigationController()
                    .addOnDestinationChangedListener((controller, destination, arguments) -> {
                        int direction = (destination.getId() == R.id.movie_profile_fragment) ?
                                ANIMATE_OUT :
                                ANIMATE_IN;

                        animateBottombar(direction);
                    });
        }
    }

    private void getBundle() {
        if (getArguments() == null) return;

        working = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
    }

    private void animateBottombar(int direction) {
        int translateY = (direction == ANIMATE_IN) ? 0 : bottombar.getHeight();
        float alpha = (direction == ANIMATE_IN) ? 1.0f : 0.0f;
        int duration = 150;
        AnimatorListenerAdapter bottombarListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (direction == ANIMATE_IN) bottombar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (direction == ANIMATE_OUT) bottombar.setVisibility(View.GONE);
            }
        };

        bottombar.animate()
                .translationY(translateY)
                .alpha(alpha)
                .setDuration(duration)
                .setListener(bottombarListener);
    }
}
