package akaecliptic.dev.cinephile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import akaecliptic.dev.cinephile.Adapter.ExploreAdapter;
import akaecliptic.dev.cinephile.Architecture.MediaViewModel;
import akaecliptic.dev.cinephile.Architecture.MovieApiDAO;
import akaecliptic.dev.cinephile.Model.Movie;
import akaecliptic.dev.cinephile.R;

import static akaecliptic.dev.cinephile.Fragment.MyListFragment.SELECTED_MOVIE;
import static akaecliptic.dev.cinephile.Fragment.MyListFragment.SELECTED_SAVED;
import static akaecliptic.dev.cinephile.Fragment.MyListFragment.SELECTED_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private MediaViewModel mediaViewModel;

    private RecyclerView trendingRecycler;
    private RecyclerView recentRecycler;
    private RecyclerView upcomingRecycler;
    private RecyclerView favouritesRecycler;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setUpViewModelLink();
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecycler();
    }

    private void setUpViewModelLink() {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
    }

    private void setUpRecycler() {

        trendingRecycler = requireActivity().findViewById(R.id.explore_trending_recycler);
        recentRecycler = requireActivity().findViewById(R.id.explore_recent_recycler);
        upcomingRecycler = requireActivity().findViewById(R.id.explore_upcoming_recycler);
        favouritesRecycler = requireActivity().findViewById(R.id.explore_favourites_recycler);

        String imageConfig = mediaViewModel.getImageConfig(MovieApiDAO.ImageType.PROFILE);

        ExploreAdapter trendingAdapter = new ExploreAdapter(requireContext(), null, imageConfig, MovieApiDAO.MovieType.TRENDING);
        ExploreAdapter recentAdapter = new ExploreAdapter(requireContext(), null, imageConfig, MovieApiDAO.MovieType.RECENT);
        ExploreAdapter upcomingAdapter = new ExploreAdapter(requireContext(), null, imageConfig, MovieApiDAO.MovieType.UPCOMING);
        ExploreAdapter favouritesAdapter = new ExploreAdapter(requireContext(), null, imageConfig, MovieApiDAO.MovieType.FAVOURITES);

        setUpAdapterClicks(trendingAdapter, recentAdapter, upcomingAdapter, favouritesAdapter);

        setUpAdapterList(mediaViewModel.requestMovies(), trendingAdapter, recentAdapter, upcomingAdapter, favouritesAdapter);

        trendingRecycler.setAdapter(trendingAdapter);
        recentRecycler.setAdapter(recentAdapter);
        upcomingRecycler.setAdapter(upcomingAdapter);
        favouritesRecycler.setAdapter(favouritesAdapter);

    }

    private void setUpAdapterClicks(ExploreAdapter... adapters) {
        ExploreAdapter.ItemClickListener itemClickListener = (view, movie) -> {
            Bundle bundle = new Bundle();
            boolean haveMovie = mediaViewModel.getItems().stream().anyMatch(m -> m.getId() == movie.getId());

            bundle.putSerializable(SELECTED_MOVIE, movie);
            bundle.putBoolean(SELECTED_SAVED, haveMovie);

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_explore_fragment_to_movie_profile_fragment, bundle);

            Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.main_coordinator),
                    "Opening '" + movie.getTitle() + "'",
                    Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
            snackbar.show();
        };

        ExploreAdapter.MoreClickListener buttonClickListener = (view, movieType) -> {
            Bundle bundle = new Bundle();

            bundle.putSerializable(SELECTED_TYPE, movieType);

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_explore_fragment_to_movie_list_fragment, bundle);
        };

        for (ExploreAdapter adapter : adapters) {
            adapter.setClickListener(itemClickListener);
            adapter.setButtonClickListener(buttonClickListener);
        }
    }

    private void setUpAdapterList(Movie[][] list, ExploreAdapter... adapters) {
        for (int i = 0; i < adapters.length; i++) {
            List<Movie> toSet = new ArrayList<>(Arrays.asList(list[i]));
            toSet.removeIf(Objects::isNull);
            adapters[i].setItemList(toSet);
        }
    }

    public interface RequestResult {
        void onResolved(Movie[] movieList);
    }
}
