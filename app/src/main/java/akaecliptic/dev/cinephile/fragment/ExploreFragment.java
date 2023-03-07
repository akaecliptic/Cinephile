package akaecliptic.dev.cinephile.fragment;

import static akaecliptic.dev.cinephile.fragment.MovieRowFragment.PAGE_TYPE;
import static akaecliptic.dev.cinephile.fragment.WatchlistFragment.SELECTED_MOVIE;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.activity.MainActivity;
import akaecliptic.dev.cinephile.adapter.explore.CardAdapter;
import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseFragment;
import dev.akaecliptic.models.Movie;


public class ExploreFragment extends BaseFragment {

    private final String[] sections = {"upcoming", "top rated", "popular", "now playing"};
    private final OnClickListener onSelectSection = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (adapter == null) return;
            if (selectedSection == null) {
                selectSection(view, container.indexOfChild(view));
                return;
            }

            String text = ((TextView) view).getText().toString().toLowerCase();
            if (text.equals(selectedSection)) return;

            selectSection(view, container.indexOfChild(view));
        }

        private void selectSection(View view, int selectedIndex) {
            for (int i = 0; i < container.getChildCount(); i++) {
                if (i == selectedIndex) {
                    selectedSection = sections[selectedIndex];
                    lastSelectedIndex = selectedIndex;

                    view.setBackgroundTintList(colorSelect);
                    adapter.setItems(getListFromViewModel());

                    continue;
                }

                container.getChildAt(i).setBackgroundTintList(colorUnselect);
            }
        }

        private List<Movie> getListFromViewModel() {
            List<Movie> movies;
            List<Movie> watchlist = viewModel.watchlist();

            switch (lastSelectedIndex) {
                default: //Default should select upcoming.
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

            /*
                I don't want to do any funky stuff like last time with having subscribers.
                In that case it got convoluted quick. Instead, here movies are swapped with their
                watchlist counterparts. This ensures references are preserved so edits to the object
                will be available to other reference holders.

                This might be much harder to debug in the future. However, at the current level of
                application's complexity, it is fine for now.

                2022-10-08
             */

            return movies
                    .stream()
                    .map(movie -> {
                                if (!watchlist.contains(movie)) return movie;

                                int index = watchlist.indexOf(movie);
                                return watchlist.get(index);
                            }
                    )
                    .collect(Collectors.toList());
        }
    };

    private String selectedSection;
    private int lastSelectedIndex; //Does this need to be static?

    private CardAdapter adapter;

    private ViewGroup container;

    private ColorStateList colorUnselect;
    private ColorStateList colorSelect;

    /*          OVERRIDES          */

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_explore;
    }

    @Override
    protected void beforeViews() {
        colorUnselect = ColorStateList.valueOf(requireContext().getColor(R.color.grey));
        colorSelect = ColorStateList.valueOf(requireContext().getColor(R.color.accent));
    }

    @Override
    protected void initViews(View view) {
        RecyclerView grid = view.findViewById(R.id.explore_grid);

        adapter = new CardAdapter(requireContext(), new ArrayList<>(), viewModel.config());
        adapter.setItemClickListener((movie, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SELECTED_MOVIE, movie);

            selectedSection = null;

            MainActivity activity = (MainActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_profile_fragment, bundle);
        });
        adapter.setMoreClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PAGE_TYPE, lastSelectedIndex);

            selectedSection = null;

            MainActivity activity = (MainActivity) requireActivity();
            activity.getNavigationController().navigate(R.id.movie_row_fragment, bundle);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == adapter.getItemCount() - 1) return 2;
                return 1;
            }
        });

        grid.setLayoutManager(layoutManager);
        grid.setAdapter(adapter);
    }

    @Override
    protected void afterViews(View view) {
        container = view.findViewById(R.id.explore_container_pills);

        for (int i = 0; i < container.getChildCount(); i++) {
            TextView pill = (TextView) container.getChildAt(i);

            pill.setText(sections[i]);
            pill.setOnClickListener(onSelectSection);
            pill.setBackgroundTintList(colorUnselect);
        }

        container.getChildAt(lastSelectedIndex).performClick();
    }
}
