package akaecliptic.dev.cinephile.fragment;

import static akaecliptic.dev.cinephile.fragment.WatchlistFragment.ALL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.adapter.watchlist.CardSlimAdapter;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.data.ViewModel;
import akaecliptic.dev.cinephile.dialog.DeleteDialog;
import akaecliptic.dev.cinephile.interaction.listener.MovieChangeListener;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Movie;

public class CollectionsFragment extends BaseFragment {

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";

    private CardSlimAdapter adapter;
    private final String name;

    private final MovieChangeListener movieChangeListener = (movie) -> {
        if (adapter == null) return;

        int index = adapter.getItems().indexOf(movie);

        viewModel.watchlist().remove(movie);
        viewModel.deleteMovie(movie.getId());

        adapter.notifyItemRemoved(index);
    };
    @SuppressLint("NotifyDataSetChanged")
    private final Toolbar.OnMenuItemClickListener onToolbarSort = (item) -> {
        if (item.getItemId() != R.id.toolbar_sort || adapter == null) return false;

        List<Movie> watchlist = this.viewModel.watchlist();
        String message = ViewModel.cycleSort(watchlist);

        /*
            Hmm, I'm only using a straight notifyDataSetChanged because it is faster on my testing virtual device.
            Will try to run on physical device in future to see if that is still the case.

            2022-10-14
         */
        // CONSIDER: changing back to notifyItemRangeChanged. See comment above.
        adapter.notifyDataSetChanged();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        return true;
    };

    public CollectionsFragment(String name) {
        this.name = name;
    }

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_collection;
    }

    @Override
    protected void beforeViews() {
        MainActivity activity = (MainActivity) requireActivity();
        Toolbar toolbar = activity.getToolbar();
        toolbar.setOnMenuItemClickListener(onToolbarSort);

        List<Movie> list = new ArrayList<>(this.viewModel.watchlist());
        if(!this.name.equals(ALL)) {
            Collection collection = this.viewModel.collections()
                    .stream()
                    .filter(item -> item.getName().equals(this.name))
                    .findFirst()
                    .orElse(null);

            list = list
                    .stream()
                    .filter(item -> collection == null || collection.getMembers().contains(item.getId()))
                    .collect(Collectors.toList());
        }

        ViewModel.sort(list);
        adapter = new CardSlimAdapter(requireContext(), list);
    }

    @Override
    protected void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.collections_recycler);

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
}
