package aka_ecliptic.com.cinephile.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.Architecture.MovieApiDAO;
import aka_ecliptic.com.cinephile.Helper.MediaObjectHelper;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;
import aka_ecliptic.com.cinephile.SearchActivity;

import static aka_ecliptic.com.cinephile.Fragment.MyListFragment.SELECTED_MOVIE;
import static aka_ecliptic.com.cinephile.Fragment.MyListFragment.SELECTED_SAVED;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieProfileFragment extends Fragment {

    private static final int ANIM_IN = 1;
    private static final int ANIM_OUT = 0;
    private static final String RELEASE_TEMPLATE = "Release date: ";

    private Menu menu;
    private String configPoster;
    private String configBackdrop;
    private Movie selected = null;
    private boolean isSelectedSaved;
    private MediaViewModel mediaViewModel;

    private BottomNavigationView bottomNavigationView;

    private TextView titleText;
    private TextView descriptionText;
    private ImageView posterImage;
    private ImageView backdropImage;
    private ProgressBar ratingProgress;
    private TextView ratingText;
    private CheckBox seenCheck;
    private FloatingActionButton moreButton;
    private TextView genreText1;
    private TextView genreText2;
    private TextView genreText3;

    public MovieProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setUpViewModelLink();
        attachAnimator();
        getBundle();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movie_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        populateViews();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_add).setVisible(!isSelectedSaved);
        menu.findItem(R.id.toolbar_favourite).setVisible(isSelectedSaved);

        setFavouriteIcon(menu.findItem(R.id.toolbar_favourite), false);

        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_favourite:
                // TODO: 04/10/2020 FINISH IMPLEMENTATION AFTER ADDING FAVOURITE FEATURE.
//                setFavouriteIcon(item, !item.isChecked());
                break;
            case R.id.toolbar_add:
                isSelectedSaved = true;
                addMovie(selected);
                onPrepareOptionsMenu(menu);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setFavouriteIcon(MenuItem item, boolean checked){
        int drawableResId = (checked) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;
        item.setIcon(drawableResId);
        item.setChecked(checked);
    }

    private void addMovie(Movie movie) {
        mediaViewModel.addItem(movie);

        seenCheck.setClickable(isSelectedSaved);
        addViewListeners();

        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.main_coordinator),
                "This movie has been added to your list",
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
        snackbar.show();
    }

    private void initViews(View view) {
        titleText = view.findViewById(R.id.movie_profile_text_title);
        descriptionText = view.findViewById(R.id.movie_profile_text_description);

        posterImage = view.findViewById(R.id.movie_profile_image_poster);
        backdropImage = view.findViewById(R.id.movie_profile_image_backdrop);

        ratingProgress = view.findViewById(R.id.movie_profile_progress_rating);
        ratingText = view.findViewById(R.id.movie_profile_text_rating);
        seenCheck = view.findViewById(R.id.movie_profile_check_seen);
        moreButton = view.findViewById(R.id.movie_profile_button_more);

        genreText1 = view.findViewById(R.id.movie_profile_text_genre_1);
        genreText2 = view.findViewById(R.id.movie_profile_text_genre_2);
        genreText3 = view.findViewById(R.id.movie_profile_text_genre_3);
    }

    private void populateViews() {
        titleText.setText(selected.getTitle());
        titleText.setSelected(true);

        String description =
                RELEASE_TEMPLATE + "\n" +
                MediaObjectHelper.dateToString(selected.getReleaseDate()) + "\n\n" +
                selected.getStatistic().getDescription();
        descriptionText.setText(description);

        Picasso.get().load(configPoster + selected.getImageData().getPosterImagePath()).
                fit().centerCrop().into(posterImage);
        Picasso.get().load(configBackdrop + selected.getImageData().getBackdropImagePath()).
                fit().centerCrop().into(backdropImage);

        ratingProgress.setProgress(selected.getRating() * 10);
        String rating = selected.getRating() + "/10";
        ratingText.setText(rating);

        seenCheck.setChecked(selected.isSeen());

        genreText1.setText(selected.getGenre().toString());
        genreText2.setText(selected.getSubGenre().toString());
        genreText3.setText(selected.getMinGenre().toString());

        seenCheck.setClickable(isSelectedSaved);

        if(isSelectedSaved)
            addViewListeners();
    }

    private void addViewListeners() {
        seenCheck.setOnClickListener(view -> {
            selected.setSeen(((CheckBox)view).isChecked());
            mediaViewModel.updateItem(selected);
        });


        ratingProgress.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_rating, null));

            AlertDialog dialog = builder.create();
            dialog.show();

            TextView title = dialog.findViewById(R.id.rating_dialog_text_title);
            title.setText(R.string.dialog_title_rating);

            Spinner spinner = dialog.findViewById(R.id.rating_dialog_spinner_rating);
            spinner.setAdapter(new ArrayAdapter<>(requireContext(),
                    R.layout.genre_spinner_item,
                    IntStream.rangeClosed(0, 10).boxed().collect(Collectors.toList())));
            spinner.setSelection(selected.getRating());

            Button confirm = dialog.findViewById(R.id.rating_dialog_button_confirm);
            Button cancel = dialog.findViewById(R.id.rating_dialog_button_cancel);

            confirm.setOnClickListener(v -> {
                selected.setRating((int)spinner.getSelectedItem());
                mediaViewModel.updateItem(selected);

                ratingProgress.setProgress(selected.getRating() * 10);
                String rating = selected.getRating() + "/10";
                ratingText.setText(rating);

                dialog.dismiss();
            });

            cancel.setOnClickListener(v -> dialog.cancel());

            return true;
        });
    }

    private void setUpViewModelLink() {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
        configPoster = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.POSTER);
        configBackdrop = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.BACKDROP);
    }

    private void attachAnimator() {
        if(requireActivity().getClass() != SearchActivity.class){
            bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_bar);
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment))
                    .addOnDestinationChangedListener((controller, destination, arguments) -> {
                        int direction = (destination.getId() == R.id.movie_profile_fragment) ? ANIM_OUT : ANIM_IN;

                        if(!(direction == ANIM_IN && bottomNavigationView.getVisibility() == View.VISIBLE))
                            animateBottomNav(direction);
                    });
        }
    }

    private void getBundle() {
        if(getArguments() != null) {
            selected = (Movie) getArguments().getSerializable(SELECTED_MOVIE);
            isSelectedSaved = getArguments().getBoolean(SELECTED_SAVED);
        }
    }


    private void animateBottomNav(int direction) {
        bottomNavigationView.animate()
                .translationY((direction == ANIM_IN) ? 0 : bottomNavigationView.getHeight())
                .alpha((direction == ANIM_IN) ? 1.0f : 0.0f)
                .setDuration((direction == ANIM_IN) ? 0 : 250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (direction == ANIM_IN)
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        if (direction == ANIM_OUT)
                            bottomNavigationView.setVisibility(View.GONE);
                    }
                });
    }
}
