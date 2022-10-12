package akaecliptic.dev.cinephile.Fragment;

import static akaecliptic.dev.cinephile.Fragment.WatchlistFragment.SELECTED_MOVIE;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.Activity.SearchActivity;
import akaecliptic.dev.cinephile.Adapter.Explore.CardRowAdapter;
import akaecliptic.dev.cinephile.Architecture.ViewModel;
import akaecliptic.dev.cinephile.Interface.TMDBCallback;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseFragment;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieSearchFragment extends BaseFragment {

    private String query;
    private int page;
    private boolean paginate;

    private List<Movie> pool;
    private Set<Integer> overlap;

    private CardRowAdapter adapter;

    private final TMDBCallback<Page> callback = response -> {
        List<Movie> watchlist = viewModel.watchlist();
        List<Movie> combined = response.results()
                .stream()
                .map(movie -> {
                            if (!watchlist.contains(movie)) return movie;

                            int index = watchlist.indexOf(movie);
                            overlap.add(movie.getId());

                            return watchlist.get(index);
                        }
                )
                .collect(Collectors.toList());

        pool.addAll(combined);
        page = response.number();
        paginate = response.paginate();

        if (adapter != null) {
            int start = (pool.size() - combined.size()) + 1;
            int count = combined.size();

            adapter.notifyItemRangeInserted(start, count);
            adapter.setPaginate(paginate);
        }
    };

    private void paginate() {
        viewModel.search(query, ++page, callback);
    }

    private void getQuery() {
        if (requireActivity().getClass() != SearchActivity.class) return;

        SearchActivity activity = (SearchActivity) requireActivity();
        query = activity.getInitialQuery();
    }

    private void initAdapter(View view, Configuration config) {
        RecyclerView recycler = view.findViewById(R.id.movie_search_recycler);

        adapter = new CardRowAdapter(requireContext(), pool, config, overlap);
        adapter.setItemClickListener((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            SearchActivity activity = (SearchActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setMoreClickListener(v -> {
            if (paginate) paginate();
        });
        adapter.setItemAddClickListener(((movie, position) -> {
            viewModel.insert(movie);
            overlap.add(movie.getId());
            adapter.notifyItemChanged(position);

            ViewModel.pool(movie);
        }));

        recycler.setAdapter(adapter);
        paginate();
    }

    /*          OVERRIDES          */

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_movie_search;
    }

    @Override
    protected void beforeViews() {
        // CONSIDER: May need to look for another way to check if values are already initialed.
        if (pool != null) return;

        this.getQuery();
        this.page = 0;
        this.paginate = true;
        this.pool = new ArrayList<>();
        this.overlap = new HashSet<>();
    }

    @Override
    protected void initViews(View view) {

        if (viewModel.config() != null) {
            initAdapter(view, viewModel.config());
            return;
        }

        viewModel.config(config -> initAdapter(view, config));

        EditText searchbar = view.findViewById(R.id.searchbar_search);
        searchbar.setText(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Movie> movies = ViewModel.pool();
        if (movies.isEmpty()) return;
        overlap.addAll(movies.stream().map(Movie::getId).collect(Collectors.toSet()));
    }
}