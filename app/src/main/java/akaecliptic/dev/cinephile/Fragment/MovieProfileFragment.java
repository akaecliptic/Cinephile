package akaecliptic.dev.cinephile.Fragment;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import akaecliptic.dev.cinephile.Adapter.CollectionsArrayAdapter;
import akaecliptic.dev.cinephile.Architecture.MovieViewModel;
import akaecliptic.dev.cinephile.Architecture.MovieApiDAO;
import akaecliptic.dev.cinephile.Helper.MediaObjectHelper;
import akaecliptic.dev.cinephile.Model.Movie;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Activity.SearchActivity;

import static akaecliptic.dev.cinephile.Fragment.WatchListFragment.SELECTED_MOVIE;
import static akaecliptic.dev.cinephile.Fragment.WatchListFragment.SELECTED_SAVED;


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
    private boolean isSelectedFavourited;
    private MovieViewModel viewModel;
    private OnFavourite onFavourite;

    private BottomNavigationView bottomNavigationView;

    private TextView titleText;
    private TextView descriptionText;
    private ImageView posterImage;
    private ImageView backdropImage;
    private ProgressBar ratingProgress;
    private TextView ratingText;
    private CheckBox seenCheck;
    private FrameLayout addCollection;
    private TextView genreText1;
    private TextView genreText2;
    private TextView genreText3;

    public MovieProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setUpViewModelLink();
        attachAnimator();
        getBundle();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.old_fragment_movie_profile, container, false);
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
        MenuItem favouriteButton = menu.findItem(R.id.toolbar_favourite);

        favouriteButton.setVisible(isSelectedSaved);
        setFavouriteIcon(favouriteButton, isSelectedFavourited);

        onFavourite = (set) -> setFavouriteIcon(favouriteButton, set);

        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_favourite:
                isSelectedFavourited = !isSelectedFavourited;
                favouriteMovie(selected, isSelectedFavourited);
                setFavouriteIcon(item, isSelectedFavourited);
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
        int drawableResId = (checked) ? R.drawable.icon_heart : R.drawable.icon_heart_border;
        item.setIcon(drawableResId);
        item.setChecked(checked);
    }

    private void addMovie(Movie movie) {
        viewModel.addItem(movie);

        seenCheck.setClickable(isSelectedSaved);
        addViewListeners();

        Snackbar snackbar = Snackbar.make(assignCoordinator(),
                "This movie has been added to your list",
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
        snackbar.show();
    }

    private void favouriteMovie(Movie movie, boolean favourited) {
        viewModel.toggleFavourite(movie.getId(), favourited);

        String message = (favourited) ?
                "You have favourited this movie." : "You have un-favourited this movie.";

        Snackbar snackbar = Snackbar.make(assignCoordinator(),
                message,
                Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
        snackbar.show();
    }

    private View assignCoordinator(){
        String origin = requireActivity().getLocalClassName();

        if(origin.equals(SearchActivity.class.getSimpleName())) {
            return requireActivity().findViewById(R.id.search_coordinator);
        }else{
            return requireActivity().findViewById(R.id.main_coordinator);
        }
    }

    private void initViews(View view) {
        titleText = view.findViewById(R.id.movie_profile_text_title);
        descriptionText = view.findViewById(R.id.movie_profile_text_description);

        posterImage = view.findViewById(R.id.movie_profile_image_poster);
        backdropImage = view.findViewById(R.id.movie_profile_image_backdrop);

        ratingProgress = view.findViewById(R.id.movie_profile_progress_rating);
        ratingText = view.findViewById(R.id.movie_profile_text_rating);
        seenCheck = view.findViewById(R.id.movie_profile_check_seen);
        addCollection = view.findViewById(R.id.movie_profile_selection_more);

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
            viewModel.updateItem(selected);
        });

        ratingProgress.setOnLongClickListener(this::createRatingDialog);

        addCollection.setOnClickListener(this::createCollectionDialog);
    }

    private void setUpViewModelLink() {
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        configPoster = viewModel.getImageConfig(MovieApiDAO.ImageType.POSTER);
        configBackdrop = viewModel.getImageConfig(MovieApiDAO.ImageType.BACKDROP);
    }

    private void attachAnimator() {
        if(requireActivity().getClass() != SearchActivity.class){
            bottomNavigationView = requireActivity().findViewById(R.id.bottombar);
            Navigation.findNavController(requireActivity().findViewById(R.id.navigation_host_fragment))
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
            isSelectedFavourited = viewModel.isFavourited(selected.getId());
        }
    }

    private boolean createRatingDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_rating, (ViewGroup) requireView(), false));

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView title = dialog.findViewById(R.id.rating_dialog_text_title);
        title.setText(R.string.dialog_title_rating);

        Spinner spinner = dialog.findViewById(R.id.rating_dialog_spinner_rating);
        spinner.setAdapter(new ArrayAdapter<>(requireContext(),
                R.layout.underline_spinner_item,
                IntStream.rangeClosed(0, 10).boxed().collect(Collectors.toList())));
        spinner.setSelection(selected.getRating());

        Button confirm = dialog.findViewById(R.id.rating_dialog_button_confirm);
        Button cancel = dialog.findViewById(R.id.rating_dialog_button_cancel);

        confirm.setOnClickListener(v -> {
            selected.setRating((int) spinner.getSelectedItem());
            viewModel.updateItem(selected);

            ratingProgress.setProgress(selected.getRating() * 10);
            String rating = selected.getRating() + "/10";
            ratingText.setText(rating);

            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.cancel());

        return true;
    }

    private void createCollectionDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_add_collection, (ViewGroup) requireView(), false));

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView title = dialog.findViewById(R.id.add_collection_dialog_text_title);
        title.setText(R.string.dialog_title_add_collection);

        ListView list = dialog.findViewById(R.id.add_collection_dialog_list_collections);
        List<String> collections = viewModel.getCollectionNames();
        Collections.reverse(collections);
        CollectionsArrayAdapter<String> listAdapter = new CollectionsArrayAdapter<>(requireContext(),
                R.layout.list_item_collection,
                R.id.list_collection_text_title,
                collections,
                viewModel.getCollectionsIn(selected));

        listAdapter.setOnCollectionSelected((pos, set) -> {
            viewModel.toggleCollection(collections.get(pos), selected.getId(), set);
            if(collections.get(pos).equals("Favourites"))
                onFavourite.toggle(set);
        });
        list.setAdapter(listAdapter);

        Button create = dialog.findViewById(R.id.add_collection_dialog_button_new);
        Button done = dialog.findViewById(R.id.add_collection_dialog_button_done);

        done.setOnClickListener(v -> dialog.dismiss());
        create.setOnClickListener(v -> {
            builder.setView(inflater.inflate(R.layout.dialog_new_collection, (ViewGroup) requireView(), false));

            AlertDialog newCollection = builder.create();
            newCollection.show();

            Button confirm = newCollection.findViewById(R.id.new_collection_dialog_button_confirm);
            Button cancel = newCollection.findViewById(R.id.new_collection_dialog_button_cancel);

            confirm.setOnClickListener(vw -> {
                EditText collection = newCollection.findViewById(R.id.new_collection_dialog_text_collection_title);
                String toCreate = collection.getText().toString();

                boolean allowed = !(viewModel.getCollectionHeadings().contains(toCreate) || viewModel.getCollectionNames().contains(toCreate));

                if(allowed){
                    viewModel.addCollection(toCreate);
                    listAdapter.add(toCreate);
                    listAdapter.notifyDataSetChanged();
                    newCollection.dismiss();
                }else {
                    Toast.makeText(requireContext(), "Collection name is already in use. Try another.", Toast.LENGTH_LONG).show();
                }
            });

            cancel.setOnClickListener(vw -> newCollection.cancel());
        });
    }

    interface OnFavourite {
        void toggle(boolean set);
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
