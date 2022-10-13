package akaecliptic.dev.cinephile.Fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import akaecliptic.dev.cinephile.Activity.MainActivity;
import akaecliptic.dev.cinephile.Adapter.List.CardSlimAdapter;
import akaecliptic.dev.cinephile.Architecture.ViewModel;
import akaecliptic.dev.cinephile.Dialog.DeleteDialog;
import akaecliptic.dev.cinephile.Interface.MovieChangeListener;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;
import dev.akaecliptic.models.Movie;

public class WatchlistFragment extends BaseFragment {

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";

    private CardSlimAdapter adapter;

    private final MovieChangeListener movieChangeListener = (movie) -> {
        if (adapter == null) return;

        int index = adapter.getItems().indexOf(movie);

        viewModel.watchlist().remove(movie);
        viewModel.deleteMovie(movie.getId());

        adapter.notifyItemRemoved(index);
    };

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_watchlist;
    }

    @Override
    protected void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.watchlist_recycler);
        adapter = new CardSlimAdapter(requireContext(), this.viewModel.watchlist());

        adapter.setOnClickCheckbox((movie, position) -> viewModel.updateSeen(movie));
        adapter.setOnClickItem((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            MainActivity activity = (MainActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setOnLongClickItem((movie, position) -> {
            DeleteDialog deleteDialog = new DeleteDialog(movie);
            deleteDialog.show(getParentFragmentManager(), TAG);
            deleteDialog.setMovieChangeListener(movieChangeListener);
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
