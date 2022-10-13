package akaecliptic.dev.cinephile.Fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static akaecliptic.dev.cinephile.Fragment.WatchlistFragment.SELECTED_MOVIE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.Activity.SearchActivity;
import akaecliptic.dev.cinephile.Architecture.ViewModel;
import akaecliptic.dev.cinephile.Dialog.RatingDialog;
import akaecliptic.dev.cinephile.Interface.MovieChangeListener;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;
import dev.akaecliptic.models.Movie;

public class MovieProfileFragment extends BaseFragment {

    private static final int ANIMATE_IN = 1;
    private static final int ANIMATE_OUT = 0;

    private Movie working = null;
    private boolean present = false;

    private String backdropSize;
    private String posterSize;

    private BottomNavigationView bottombar;

    private ShapeableImageView poster;
    private ImageView backdrop;

    private TextView title;
    private TextView year;
    private TextView information;

    private ProgressBar ratingProgress;
    private TextView ratingText;
    private CheckBox seen;
    private CheckBox heart;

    private ViewGroup genreContainer;

    private final MovieChangeListener movieChangeListener = (movie) -> {
        ratingProgress.setProgress(working.getUserRating());
        ratingText.setText(String.valueOf(working.getUserRating()));
        viewModel.updateRating(movie);
    };

    /*          OVERRIDES          */

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_movie_profile;
    }

    @Override
    protected void beforeViews() {
        attachAnimator();
        setImageSizes();
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
        //ASSIGNING DEFAULT VALUES
        Picasso.get()
                .load(viewModel.image(backdropSize, working.getInfo().getBackdrop()))
                .fit()
                .centerCrop()
                .into(backdrop);
        Picasso.get()
                .load(viewModel.image(posterSize, working.getInfo().getPoster()))
                .fit()
                .centerCrop()
                .into(poster);

        title.setText(working.getTitle());
        year.setText(String.valueOf(working.getRelease().getYear()));
        information.setText(createDescription());

        if (present) {
            ratingProgress.setProgress(working.getUserRating());
            ratingText.setText(String.valueOf(working.getUserRating()));

        } else {
            ratingProgress.setProgress(working.getNativeRating());
            ratingText.setText(String.valueOf(working.getNativeRating()));
        }

        seen.setChecked(working.isSeen());
        heart.setVisibility(GONE); // TODO: 2022-10-07 Reintroduce feature.

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        working.getInfo().getGenres().forEach(genre -> {
            TextView pill = (TextView) inflater.inflate(R.layout.component_pill, genreContainer, false);
            pill.setText(viewModel.genres().get(genre));
            genreContainer.addView(pill);
        });

        addListeners(view);
    }

    private String createDescription() {
        StringBuilder builder = new StringBuilder();

        if (working.getInfo().getTagline() != null) builder.append("\"").append(working.getInfo().getTagline()).append("\"");
        builder.append(working.getDescription()).append("\n\n");
        builder.append("Release: ").append(working.getRelease()).append("\n");
        if (working.getInfo().getRuntime() > -1) builder.append("Runtime: ").append(working.getInfo().getRuntime()).append("\n");
        builder.append("TMDB rating: ").append(working.getNativeRating()).append("\n");

        return builder.toString();
    }

    private void addListeners(View view) {
        //ADDING LISTENERS
        FrameLayout frameAdd = view.findViewById(R.id.movie_profile_frame_add);
        FrameLayout frameSeen = view.findViewById(R.id.movie_profile_frame_seen);
        FrameLayout frameProgress = view.findViewById(R.id.movie_profile_frame_progress);

        frameAdd.setOnClickListener(v -> {
            if (present) {
                Toast.makeText(requireContext(), "Movie already in watchlist, collections coming soon", Toast.LENGTH_SHORT).show();
                return;
            }

            present = true;
            toggleSeen();
            viewModel.insert(working);
            String prompt = String.format("Added '%s' to Watchlist", working.getTitle());
            Toast.makeText(requireContext(), prompt, Toast.LENGTH_SHORT).show();

            if (requireActivity().getClass() != SearchActivity.class) return;

            ViewModel.pool(working);
        });
        frameSeen.setOnClickListener(v -> {
            if (seen.isEnabled()) {
                seen.setChecked(!working.isSeen());
                return;
            }

            Toast.makeText(requireContext(), "Movie must be added to watchlist to mark as seen", Toast.LENGTH_SHORT).show();
        });
        frameProgress.setOnClickListener(v -> {
            if (!present) {
                Toast.makeText(requireContext(), "Movie must be added to watchlist to give a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            RatingDialog ratingDialog = new RatingDialog(working);
            ratingDialog.show(getParentFragmentManager(), TAG);
            ratingDialog.setMovieChangeListener(movieChangeListener);
        });

        seen.setOnCheckedChangeListener((checkbox, value) -> {
            working.setSeen(value);
            viewModel.updateSeen(working);
        });

        toggleSeen();
    }

    /*          INSTANCE METHODS          */

    private void toggleSeen() {
        seen.setEnabled(present);
        seen.setClickable(present);
    }

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

    private void setImageSizes() {
        String[] backdrops = viewModel.backdrops();
        String[] posters = viewModel.posters();

        backdropSize = backdrops[1];
        posterSize = posters[1];
    }

    private void getBundle() {
        if (getArguments() == null) return;

        working = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
        present = viewModel.watchlist().contains(working);
    }

    private void animateBottombar(int direction) {
        int translateY = (direction == ANIMATE_IN) ? 0 : bottombar.getHeight();
        float alpha = (direction == ANIMATE_IN) ? 1.0f : 0.0f;
        int duration = 150;
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
