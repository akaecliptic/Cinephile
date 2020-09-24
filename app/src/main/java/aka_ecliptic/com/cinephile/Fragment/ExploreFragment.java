package aka_ecliptic.com.cinephile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aka_ecliptic.com.cinephile.Adapter.ExploreAdapter;
import aka_ecliptic.com.cinephile.Adapter.MyListAdapter;
import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.Architecture.MovieApiDAO;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;


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
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModelLink();
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

        ExploreAdapter trendingAdapter = new ExploreAdapter(requireContext(), null, imageConfig);
        ExploreAdapter recentAdapter = new ExploreAdapter(requireContext(), null, imageConfig);
        ExploreAdapter upcomingAdapter = new ExploreAdapter(requireContext(), null, imageConfig);
        ExploreAdapter favouritesAdapter = new ExploreAdapter(requireContext(), null, imageConfig);

        setUpAdapterClicks(trendingAdapter, recentAdapter, upcomingAdapter, favouritesAdapter);

        setUpAdapterList(mediaViewModel.requestMovies(), trendingAdapter, recentAdapter, upcomingAdapter, favouritesAdapter);

        trendingRecycler.setAdapter(trendingAdapter);
        recentRecycler.setAdapter(recentAdapter);
        upcomingRecycler.setAdapter(upcomingAdapter);
        favouritesRecycler.setAdapter(favouritesAdapter);

    }

    private void setUpAdapterClicks(ExploreAdapter... adapters) {
        ExploreAdapter.ItemClickListener itemClickListener = (view, movie) -> {
            Toast.makeText(requireContext(), movie.getTitle().concat(" Clicked"), Toast.LENGTH_SHORT).show();
        };

        MyListAdapter.ItemClickListener buttonClickListener = (view, position) -> {
            Toast.makeText(requireContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
        };

        for (ExploreAdapter adapter : adapters) {
            adapter.setClickListener(itemClickListener);
            adapter.setButtonClickListener(buttonClickListener);
        }
    }

    private void setUpAdapterList(Movie[][] list, ExploreAdapter... adapters) {
        for (int i = 0; i < adapters.length; i++) {
            adapters[i].setItemList(Arrays.asList(list[i]));
        }
    }

    public interface RequestResult {
        void onResolved(Movie[] movieList);
    }
}
