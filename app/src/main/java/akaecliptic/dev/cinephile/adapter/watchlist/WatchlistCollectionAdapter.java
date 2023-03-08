package akaecliptic.dev.cinephile.adapter.watchlist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import akaecliptic.dev.cinephile.fragment.CollectionsFragment;
import akaecliptic.dev.cinephile.fragment.WatchlistFragment;

public class WatchlistCollectionAdapter extends FragmentStateAdapter {

    private final List<String> collections;

    public WatchlistCollectionAdapter(WatchlistFragment fragment, List<String> collections) {
        super(fragment);
        this.collections = collections;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new CollectionsFragment(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return this.collections.size();
    }
}
