package akaecliptic.dev.cinephile.Fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.Adapter.List.CardSlimAdapter;
import akaecliptic.dev.cinephile.Architecture.ViewModel;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;
import dev.akaecliptic.models.Movie;

public class WatchlistFragment extends BaseFragment {

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_watchlist;
    }

    @Override
    protected void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.watchlist_recycler);
        CardSlimAdapter adapter = new CardSlimAdapter(requireContext(), this.viewModel.watchlist());

        adapter.setOnClickCheckbox((movie, position) -> viewModel.updateSeen(movie));
        adapter.setOnClickItem((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            MainActivity activity = (MainActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Movie> movies = ViewModel.drain();
        if (movies.isEmpty()) return;
        this.viewModel.watchlist().addAll(movies);
    }
}
