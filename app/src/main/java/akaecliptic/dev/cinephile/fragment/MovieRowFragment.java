package akaecliptic.dev.cinephile.fragment;

import static akaecliptic.dev.cinephile.fragment.CollectionsFragment.SELECTED_MOVIE;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.adapter.explore.CardRowAdapter;
import akaecliptic.dev.cinephile.adapter.explore.ExploreSectionAdapter.Section;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.interaction.IAnimatorBottombar;
import akaecliptic.dev.cinephile.interaction.callback.TMDBCallback;
import dev.akaecliptic.models.Media;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieRowFragment extends BaseFragment implements IAnimatorBottombar {

    static final String PAGE_TYPE = "PAGE_TYPE";

    private int out = -1;
    private int page = 1;
    private Section section;
    private boolean paginate;

    private List<Movie> pool;
    private Set<Integer> overlap;

    private CardRowAdapter adapter;

    private void getBundle() {
        if (getArguments() == null) return;
        this.section = (Section) getArguments().get(PAGE_TYPE);
    }

    private void addAdapterListeners() {
        adapter.setItemClickListener((movie, position) -> {
            out = movie.getId();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            MainActivity activity = (MainActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setItemAddClickListener((movie, position) -> {
            viewModel.insert(movie);
            overlap.add(movie.getId());
            adapter.notifyItemChanged(position);
        });
        adapter.setMoreClickListener(v -> {
            if (paginate) setItems();
        });
    }

    private void setItems() {
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

        switch (this.section) {
            default:
            case PLAYING:
                this.viewModel.playing(page, callback);
                break;
            case UPCOMING:
                this.viewModel.upcoming(page, callback);
                break;
            case POPULAR:
                this.viewModel.popular(page, callback);
                break;
            case RATED:
                this.viewModel.rated(page, callback);
                break;
        }
    }

    /*          OVERRIDES          */

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_movie_row;
    }

    @Override
    protected void beforeViews() {
        if (page != 1) return;

        this.getBundle();
        this.attachAnimator(requireActivity());
        this.overlap = new HashSet<>();
        this.pool = new ArrayList<>();
    }

    @Override
    protected void initViews(View view) {
        RecyclerView recycler = view.findViewById(R.id.movie_row_recycler);
        adapter = new CardRowAdapter(requireContext(), pool, viewModel.config(), overlap);
        adapter.setPaginate(paginate);
        recycler.setAdapter(adapter);

        addAdapterListeners();
    }

    @Override
    protected void afterViews(View view) {
        if (page == 1) setItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (out == -1) return;

        this.viewModel.selectMoviesWhereIn(movies -> {
            if(movies.size() == 0) return;

            Movie add = movies.get(0);
            int index = pool.indexOf(add);

            pool.remove(index);
            pool.add(index, add);
            overlap.add(add.getId());
            out = -1;

        }, out);
    }
}
