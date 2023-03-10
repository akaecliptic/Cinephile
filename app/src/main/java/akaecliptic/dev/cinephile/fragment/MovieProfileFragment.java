package akaecliptic.dev.cinephile.fragment;

import static android.view.View.GONE;
import static akaecliptic.dev.cinephile.fragment.CollectionsFragment.SELECTED_MOVIE;
import static akaecliptic.dev.cinephile.fragment.WatchlistFragment.FAV;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.activity.SearchActivity;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.data.ViewModel;
import akaecliptic.dev.cinephile.dialog.AddCollectionDialog;
import akaecliptic.dev.cinephile.dialog.RatingDialog;
import akaecliptic.dev.cinephile.interaction.IAnimatorBottombar;
import akaecliptic.dev.cinephile.interaction.listener.MovieChangeListener;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Movie;

public class MovieProfileFragment extends BaseFragment implements IAnimatorBottombar {

    private Movie working = null;
    private boolean present = false;
    private Collection favourites;

    private String backdropSize;
    private String posterSize;

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
        this.attachAnimator(requireActivity());
        this.setImageSizes();
        this.getBundle();
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
        seen.setChecked(working.isSeen());

        if (present) {
            ratingProgress.setProgress(working.getUserRating());
            ratingText.setText(String.valueOf(working.getUserRating()));

        } else {
            ratingProgress.setProgress(working.getNativeRating());
            ratingText.setText(String.valueOf(working.getNativeRating()));
        }

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        working.getInfo().getGenres().forEach(genre -> {
            TextView pill = (TextView) inflater.inflate(R.layout.component_pill, genreContainer, false);
            pill.setText(viewModel.genres().get(genre));
            genreContainer.addView(pill);
        });

        addListeners(view);
    }

    /*          INSTANCE METHODS          */

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
                List<Collection> collections = this.viewModel.collections();
                AddCollectionDialog addCollectionDialog = new AddCollectionDialog(working, collections);
                addCollectionDialog.setOnCollectionSelected((movie, collection, add) -> {
                    if (add) {
                        this.viewModel.addToCollection(movie, collection);
                        Toast.makeText(requireContext(), "Added movie to '" + collection + "'", Toast.LENGTH_SHORT).show();
                    } else {
                        this.viewModel.removeFromCollection(movie, collection);
                        Toast.makeText(requireContext(), "Removed movie from '" + collection + "'", Toast.LENGTH_SHORT).show();
                    }

                    if (collection.equals(FAV)) toggleHeart();
                });
                addCollectionDialog.setOnCollectionAdded((movie, collection) -> {
                    long count = this.viewModel.collections().stream().filter(c -> c.getName().equals(collection)).count();

                    if (count == 0) {
                        Collection add = new Collection(collection);
                        add.getMembers().add(movie);
                        this.viewModel.insertCollection(add);

                        Toast.makeText(requireContext(), "Created and added movie to '" + collection + "'", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(requireContext(), "Creation failed,'" + collection + "' already exists", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                addCollectionDialog.show(getParentFragmentManager(), TAG);
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

        initHeart();
        toggleSeen();
    }

    private void toggleSeen() {
        seen.setEnabled(present);
        seen.setClickable(present);
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

        favourites = this.viewModel.collections()
                .stream()
                .filter(collection -> collection.getName().equals(FAV))
                .findFirst()
                .orElse(null);
    }

    private void toggleHeart() {
        this.heart.setVisibility(favourites == null ? GONE : View.VISIBLE);
        this.heart.setChecked(favourites.hasMember(working.getId()));
    }

    private void initHeart() {
        toggleHeart();
        if (favourites == null) return;
        this.heart.setOnClickListener( view -> {
            boolean add = !favourites.hasMember(working.getId());
            if (add) {
                favourites.getMembers().add(working.getId());
                this.viewModel.addToCollection(working.getId(), FAV);
                Toast.makeText(requireContext(), "Added movie to '" + FAV + "'", Toast.LENGTH_SHORT).show();
            } else {
                favourites.getMembers().remove(working.getId());
                this.viewModel.removeFromCollection(working.getId(), FAV);
                Toast.makeText(requireContext(), "Removed movie from '" + FAV + "'", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
