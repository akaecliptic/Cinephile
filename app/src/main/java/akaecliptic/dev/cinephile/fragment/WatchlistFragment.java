package akaecliptic.dev.cinephile.fragment;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.adapter.watchlist.WatchlistCollectionAdapter;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.dialog.DeleteDialog;
import akaecliptic.dev.cinephile.model.Collection;

public class WatchlistFragment extends BaseFragment {

    public static final String FAV = "favourites";
    public static final String ALL = "all";

    private WatchlistCollectionAdapter adapter;
    private List<String> collections = new ArrayList<>();

    @Override
    public void setResource() {
        this.resource = R.layout.fragment_watchlist;
    }

    @Override
    protected void initViews(View view) {
        adapter = new WatchlistCollectionAdapter(this, collections);

        TabLayout tabLayout = view.findViewById(R.id.watchlist_tab_layout);
        ViewPager2 pager = view.findViewById(R.id.watchlist_pager);

        this.viewModel.collections().observe(getViewLifecycleOwner(), collections -> {
            this.collections = collections.stream()
                    .map(Collection::getName)
                    .filter(name -> !name.equals("favourites"))
                    .collect(Collectors.toList());

            this.collections.sort(String::compareTo);
            this.collections.add(0, ALL);
            this.collections.add(1, FAV);

            adapter.setCollections(this.collections);
        });

        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            tab.setText(this.collections.get(position));
            tab.view.setOnLongClickListener(v -> showDeleteDialog(position));
        }).attach();
    }

    private boolean showDeleteDialog(int position) {
        String working = this.collections.get(position);

        // A little much, but just to be extra safe
        if (position == 0 || position == 1 || working.equals(ALL) || working.equals(FAV))
            return true;

        DeleteDialog<String> deleteDialog = new DeleteDialog<>(working, working);
        deleteDialog.show(getParentFragmentManager(), TAG);
        deleteDialog.setOnDeleteListener(collection -> {
            this.viewModel.deleteCollection(collection);
            adapter.notifyItemRemoved(position);
        });
        return true;
    }
}
