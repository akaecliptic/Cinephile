package akaecliptic.dev.cinephile.fragment;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;
import static akaecliptic.dev.cinephile.fragment.CollectionsFragment.SELECTED_MOVIE;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.activity.SearchActivity;
import akaecliptic.dev.cinephile.adapter.explore.CardRowAdapter;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.interaction.callback.TMDBCallback;
import dev.akaecliptic.models.Media;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieSearchFragment extends BaseFragment {

    private int page = 1;
    private String query;
    private boolean paginate;

    private List<Movie> pool;
    private Set<Integer> overlap;

    private CardRowAdapter adapter;

    private final OnEditorActionListener editorAction = (view, id, event) -> {
        if (id != IME_ACTION_SEARCH) return false;

        int count = pool.size();

        this.page = 1;
        this.paginate = true;
        this.pool.clear();
        this.overlap.clear();
        this.query = view.getText().toString();

        adapter.notifyItemRangeRemoved(0, count);

        searchItems();

        SearchActivity activity = (SearchActivity) requireActivity();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        return true;
    };

    private void searchItems() {
        TMDBCallback<Page> callback = response -> {
            int[] ids = response.results().stream().mapToInt(Media::getId).toArray();
            this.viewModel.selectMoviesWhereIn(movies -> {
                List<Movie> combined = response.results()
                        .stream()
                        .map(movie -> {
                                    if (!movies.contains(movie)) return movie;

                                    int index = movies.indexOf(movie);
                                    overlap.add(movie.getId());
                                    return movies.get(index);
                                }
                        )
                        .collect(Collectors.toList());

                page = response.number() + 1;
                paginate = response.paginate();

                requireActivity().runOnUiThread(() -> {
                    adapter.addItems(combined);
                    adapter.setPaginate(paginate);
                });
            }, ids);
        };

        viewModel.search(query, page, callback);
    }

    private void getQuery() {
        if (requireActivity().getClass() != SearchActivity.class) return;
        SearchActivity activity = (SearchActivity) requireActivity();
        query = activity.getInitialQuery();
    }

    private void addAdapterListeners() {
        adapter.setItemClickListener((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            SearchActivity activity = (SearchActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setItemAddClickListener((movie, position) -> {
            viewModel.insert(movie);
            overlap.add(movie.getId());
            adapter.notifyItemChanged(position);
        });
        adapter.setMoreClickListener(v -> {
            if (paginate) searchItems();
        });
    }

    private void setBackNavigate() {
        SearchActivity activity = (SearchActivity) requireActivity();
        activity.getToolbar().setNavigationOnClickListener(view -> activity.onBackPressed());
    }

    /*          OVERRIDES          */

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_movie_search;
    }

    @Override
    protected void beforeViews() {
        setBackNavigate();
        if (page != 1) return;

        this.getQuery();
        this.overlap = new HashSet<>();
        this.pool = new ArrayList<>();
    }

    @Override
    protected void initViews(View view) {
        viewModel.config(config -> requireActivity().runOnUiThread(() -> {
            EditText searchbar = view.findViewById(R.id.searchbar_search);
            searchbar.setText(query);
            searchbar.setOnEditorActionListener(editorAction);

            RecyclerView recycler = view.findViewById(R.id.movie_search_recycler);
            adapter = new CardRowAdapter(requireContext(), pool, config, overlap);
            recycler.setAdapter(adapter);

            addAdapterListeners();
            if (page == 1) searchItems();
        }));
    }

}
