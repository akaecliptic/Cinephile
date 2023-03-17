package akaecliptic.dev.cinephile.fragment;

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

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.dialog.AddCollectionDialog;
import akaecliptic.dev.cinephile.dialog.RatingDialog;
import akaecliptic.dev.cinephile.interaction.IAnimatorBottombar;
import akaecliptic.dev.cinephile.interaction.listener.MovieChangeListener;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Movie;

public class MovieProfileFragment extends BaseFragment implements IAnimatorBottombar {

    private Movie working = null;
    private boolean present = false;

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
        createImages();
        createDescription();

        title.setText(working.getTitle());
        year.setText(String.valueOf(working.getRelease().getYear()));
        seen.setChecked(working.isSeen());

        addGenres(view);
        addObserver(view);
        addHeartListener();
    }

    /*          INSTANCE INITIALIZERS          */

    private void setImageSizes() {
        String[] backdrops = viewModel.backdrops();
        String[] posters = viewModel.posters();

        backdropSize = backdrops[1];
        posterSize = posters[1];
    }

    private void getBundle() {
        if (getArguments() == null) return;
        working = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
    }

    /*          BINDING MOVIE TO VIEWS         */

    private void addGenres(View view) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        working.getInfo().getGenres().forEach(genre -> {
            TextView pill = (TextView) inflater.inflate(R.layout.component_pill, genreContainer, false);
            pill.setText(viewModel.genres().get(genre));
            genreContainer.addView(pill);
        });
    }

    private void addRating() {
        if (present) {
            ratingProgress.setProgress(working.getUserRating());
            ratingText.setText(String.valueOf(working.getUserRating()));
        } else {
            ratingProgress.setProgress(working.getNativeRating());
            ratingText.setText(String.valueOf(working.getNativeRating()));
        }
    }

    private void createImages() {
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
    }

    private void createDescription() {
        StringBuilder builder = new StringBuilder();

        if (working.getInfo().getTagline() != null)
            builder.append("\"").append(working.getInfo().getTagline()).append("\"");

        builder.append(working.getDescription()).append("\n\n");
        builder.append("Release: ").append(working.getRelease()).append("\n");

        if (working.getInfo().getRuntime() > -1)
            builder.append("Runtime: ").append(working.getInfo().getRuntime()).append("\n");

        builder.append("TMDB rating: ").append(working.getNativeRating()).append("\n");

        information.setText(builder.toString());
    }

    /*          INITIALISING MAIN BUTTONS          */

    private void addListeners(View view) {
        //ADDING LISTENERS
        FrameLayout frameAdd = view.findViewById(R.id.movie_profile_frame_add);
        FrameLayout frameSeen = view.findViewById(R.id.movie_profile_frame_seen);
        FrameLayout frameProgress = view.findViewById(R.id.movie_profile_frame_progress);

        addAddListener(frameAdd);
        addSeenListener(frameSeen);
        addProgressListener(frameProgress);
    }

    private void addAddListener(FrameLayout layout) {
        layout.setOnClickListener(v -> {
            if (present) {
                AddCollectionDialog addCollectionDialog = new AddCollectionDialog(working, this);

                setCollectionSelectedListener(addCollectionDialog);
                setNewCollectionDialogListener(addCollectionDialog);

                addCollectionDialog.show(getParentFragmentManager(), TAG);
            } else {
                viewModel.insert(working);
                String prompt = String.format("Added '%s' to Watchlist", working.getTitle());
                Toast.makeText(requireContext(), prompt, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCollectionSelectedListener(AddCollectionDialog dialog) {
        dialog.setOnCollectionSelected((movie, collection, add) -> {
            if (add) {
                viewModel.addToCollection(movie, collection);
                Toast.makeText(requireContext(), "Added movie to '" + collection + "'", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.removeFromCollection(movie, collection);
                Toast.makeText(requireContext(), "Removed movie from '" + collection + "'", Toast.LENGTH_SHORT).show();
            }

            if (collection.equals(FAV)) toggleHeart();
        });
    }

    private void setNewCollectionDialogListener(AddCollectionDialog dialog) {
        dialog.setOnCollectionCreated((movie, name, dismiss) ->
                viewModel.collection(name, result -> {
                    if (result == null) {
                        Collection add = new Collection(name);
                        add.getMembers().add(movie);
                        viewModel.insertCollection(add);

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Created and added movie to '" + name + "'", Toast.LENGTH_SHORT).show();
                            dismiss.call();
                        });
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Creation failed,'" + name + "' already exists", Toast.LENGTH_SHORT).show()
                        );
                    }
                })
        );
    }

    private void addSeenListener(FrameLayout layout) {
        layout.setOnClickListener(v -> {
            if (seen.isEnabled()) {
                seen.setChecked(!working.isSeen());
            } else {
                Toast.makeText(requireContext(), "Movie must be added to watchlist to mark as seen", Toast.LENGTH_SHORT).show();
            }
        });
        seen.setOnCheckedChangeListener((checkbox, value) -> {
            working.setSeen(value);
            viewModel.updateSeen(working);
        });
    }

    public void addProgressListener(FrameLayout layout) {
        layout.setOnClickListener(v -> {
            if (!present) {
                Toast.makeText(requireContext(), "Movie must be added to watchlist to give a rating", Toast.LENGTH_SHORT).show();
            } else {
                RatingDialog ratingDialog = new RatingDialog(working);
                ratingDialog.show(getParentFragmentManager(), TAG);
                ratingDialog.setMovieChangeListener(movieChangeListener);
            }
        });
    }

    /*          CONDITIONAL VIEWS          */

    private void addObserver(View view) {
        viewModel.watchlist().observe(getViewLifecycleOwner(), watchlist -> {
            present = watchlist.contains(working);
            initSeen();
            initHeart();
            addRating();
            addListeners(view);
        });
    }

    private void initSeen() {
        seen.setEnabled(present);
        seen.setClickable(present);
    }

    private void initHeart() {
        heart.setVisibility(present ? View.VISIBLE : View.GONE);
    }

    private void toggleHeart() {
        viewModel.collection(FAV, favourites -> this.heart.setChecked(favourites.hasMember(working.getId())));
    }

    private void addHeartListener() {
        viewModel.collection(FAV, favourites -> {
            this.heart.setOnClickListener(view -> {
                boolean add = !favourites.hasMember(working.getId());

                if (add) {
                    favourites.getMembers().add(working.getId());
                    viewModel.addToCollection(working.getId(), FAV);
                    Toast.makeText(requireContext(), "Added movie to '" + FAV + "'", Toast.LENGTH_SHORT).show();
                } else {
                    favourites.getMembers().remove(working.getId());
                    viewModel.removeFromCollection(working.getId(), FAV);
                    Toast.makeText(requireContext(), "Removed movie from '" + FAV + "'", Toast.LENGTH_SHORT).show();
                }
            });

            this.heart.setChecked(favourites.hasMember(working.getId()));
        });
    }
}
