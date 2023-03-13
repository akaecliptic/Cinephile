package akaecliptic.dev.cinephile.fragment;

import static akaecliptic.dev.cinephile.fragment.CollectionsFragment.SELECTED_MOVIE;
import static akaecliptic.dev.cinephile.fragment.MovieRowFragment.PAGE_TYPE;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.adapter.explore.CardAdapter;
import akaecliptic.dev.cinephile.adapter.explore.ExploreSectionAdapter.Section;
import akaecliptic.dev.cinephile.base.BaseFragment;
import dev.akaecliptic.models.Movie;

public class ExploreSectionFragment extends BaseFragment {

    private final Section section;
    private CardAdapter adapter;

    public ExploreSectionFragment(Section section) {
        this.section = section;
    }

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_explore_section;
    }

    @Override
    protected void initViews(View view) {
        RecyclerView grid = view.findViewById(R.id.explore_grid);

        adapter = new CardAdapter(requireContext(), new ArrayList<>(), viewModel.config());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == adapter.getItemCount() - 1) ? 2 : 1;
            }
        });
        grid.setLayoutManager(layoutManager);
        grid.setAdapter(adapter);

        setItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        addAdapterListeners();
    }

    private void addAdapterListeners() {
        MainActivity activity = (MainActivity) requireActivity();

        adapter.setItemClickListener((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setMoreClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PAGE_TYPE, section);

            activity.getNavigationController().navigate(R.id.movie_row_fragment, bundle);
        });
    }

    private void setItems() {
        this.viewModel.watchlist().observe(getViewLifecycleOwner(), watchlist -> {
            List<Movie> movies;
            switch (this.section) {
                default:
                case PLAYING:
                    movies = this.viewModel.playing();
                    break;
                case UPCOMING:
                    movies = this.viewModel.upcoming();
                    break;
                case POPULAR:
                    movies = this.viewModel.popular();
                    break;
                case RATED:
                    movies = this.viewModel.rated();
                    break;
            }

            movies = movies
                    .stream()
                    .map(movie -> {
                        if (watchlist.contains(movie)) {
                            int index = watchlist.indexOf(movie);
                            return watchlist.get(index);
                        } else {
                            return movie;
                        }
                    })
                    .collect(Collectors.toList());

            this.adapter.setItems(movies);
        });
    }
}
