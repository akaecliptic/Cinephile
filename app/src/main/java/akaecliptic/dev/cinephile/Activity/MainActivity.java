package akaecliptic.dev.cinephile.Activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseActivity;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottombar;

    public BottomNavigationView getBottombar() {
        return this.bottombar;
    }

    @Override
    protected void setResource() {
        this.resource = R.layout.activity_main;
    }

    @Override
    protected void setNavigates() {
        super.setNavigates();
        this.bottombar = findViewById(R.id.bottombar);
    }

    @Override
    protected void linkNavigates() {
        setSupportActionBar(toolbar);

        Set<Integer> destinations = new HashSet<>();
        destinations.add(R.id.watchlist_fragment);
        destinations.add(R.id.explore_fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(destinations).build();

        NavigationUI.setupWithNavController(toolbar, navigationController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottombar, navigationController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        }

        //This closes the search view when the keyboard is closed.
        searchView.setOnQueryTextFocusChangeListener((view, focused) -> {
            if (focused) return;

            searchItem.collapseActionView();
            searchView.setQuery("", false);
        });

        //This closes keyboard and hides search view when in movie profile.
        navigationController.addOnDestinationChangedListener((controller, destination, bundle) -> {
            if (destination.getId() != R.id.movie_profile_fragment) return; // TODO: 2022-10-12 Watch this.

            searchItem.collapseActionView();
            searchView.setQuery("", false);
            searchItem.setVisible(false);
        });

        return true;
    }
}
