package akaecliptic.dev.cinephile.fragment;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.adapter.watchlist.WatchlistCollectionAdapter;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.data.ViewModel;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Movie;

public class WatchlistFragment extends BaseFragment {

    public static final String FAV = "favourites";
    public static final String ALL = "all";

    private List<String> collections;

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_watchlist;
    }

    @Override
    protected void beforeViews() {
         this.collections = this.viewModel.collections()
                .stream()
                .map(Collection::getName)
                .filter(name -> !name.equals("favourites"))
                .collect(Collectors.toList());
         this.collections.add(0, ALL);
         this.collections.add(1, FAV);
    }

    @Override
    protected void initViews(View view) {
        WatchlistCollectionAdapter adapter = new WatchlistCollectionAdapter(this, collections);
        ViewPager2 pager = view.findViewById(R.id.watchlist_pager);
        pager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.watchlist_tab_layout);
        new TabLayoutMediator(
                tabLayout,
                pager,
                (tab, position) -> tab.setText(this.collections.get(position))
        ).attach();
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Movie> movies = ViewModel.drain();

        if (movies.isEmpty()) return;

        List<Movie> watchlist = this.viewModel.watchlist();
        watchlist.addAll(movies);
        ViewModel.sort(watchlist);
    }
}
