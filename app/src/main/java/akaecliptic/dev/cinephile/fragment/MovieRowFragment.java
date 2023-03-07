package akaecliptic.dev.cinephile.fragment;

import static akaecliptic.dev.cinephile.fragment.WatchlistFragment.SELECTED_MOVIE;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.adapter.explore.CardRowAdapter;
import akaecliptic.dev.cinephile.interaction.IAnimatorBottombar;
import akaecliptic.dev.cinephile.interaction.callback.TMDBCallback;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseFragment;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieRowFragment extends BaseFragment implements IAnimatorBottombar {

    static final String PAGE_TYPE = "PAGE_TYPE";

    private int type;
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

    private void initPool() {
        // Copied from explore fragment:
        // See ExploreFragment#onSelectSection#getListFromViewModel(int)
        List<Movie> movies;
        List<Movie> watchlist = viewModel.watchlist();

        switch (type) {
            default:
            case 0:
                movies = viewModel.upcoming();
                break;
            case 1:
                movies = viewModel.rated();
                break;
            case 2:
                movies = viewModel.popular();
                break;
            case 3:
                movies = viewModel.playing();
                break;
        }

        List<Movie> combined = movies
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
    }

    private void paginate() {
        switch (type) {
            default:
            case 0:
                viewModel.upcoming(++page, callback);
                break;
            case 1:
                viewModel.rated(++page, callback);
                break;
            case 2:
                viewModel.popular(++page, callback);
                break;
            case 3:
                viewModel.playing(++page, callback);
                break;
        }
    }

    private void getBundle() {
        if (getArguments() == null) return;

        this.type = getArguments().getInt(PAGE_TYPE);
    }

    /*          OVERRIDES          */

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_movie_row;
    }

    @Override
    protected void beforeViews() {
        // CONSIDER: May need to look for another way to check if values are already initialed.
        if (pool != null) return; //Not sure if this is the best way to approach.

        this.getBundle();
        this.attachAnimator(requireActivity());

        this.page = 1;
        this.paginate = true;
        this.pool = new ArrayList<>();
        this.overlap = new HashSet<>();

        this.initPool();
    }

    @Override
    protected void initViews(View view) {
        RecyclerView recycler = view.findViewById(R.id.movie_row_recycler);

        adapter = new CardRowAdapter(requireContext(), pool, viewModel.config(), overlap);
        adapter.setItemClickListener((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            MainActivity activity = (MainActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setMoreClickListener(v -> {
            if (paginate) paginate();
        });
        adapter.setItemAddClickListener(((movie, position) -> {
            viewModel.insert(movie);
            overlap.add(movie.getId());
            adapter.notifyItemChanged(position);
        }));

        recycler.setAdapter(adapter);
    }

    @Override
    protected void afterViews(View view) {
        adapter.setPaginate(paginate);
    }
}
