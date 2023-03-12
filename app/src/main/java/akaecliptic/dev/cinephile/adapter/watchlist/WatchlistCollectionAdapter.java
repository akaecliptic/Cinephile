package akaecliptic.dev.cinephile.adapter.watchlist;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import akaecliptic.dev.cinephile.fragment.CollectionsFragment;
import akaecliptic.dev.cinephile.fragment.WatchlistFragment;

public class WatchlistCollectionAdapter extends FragmentStateAdapter {

    private List<String> collections;

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

    @SuppressLint("NotifyDataSetChanged")
    public void setCollections(List<String> collections) {
        /*
            This might be unnecessary, may be able to get away with just clearing list then adding new items.
            Leaving for now, to make sure tabs are updated correctly.
         */
        // CONSIDER: 2023-03-12 Removing
        this.collections = collections;
        this.notifyDataSetChanged();
    }
}
