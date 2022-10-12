package akaecliptic.dev.cinephile.Activity;

import android.app.SearchManager;
import android.content.Intent;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashSet;
import java.util.Set;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.Super.BaseActivity;

public class SearchActivity extends BaseActivity {

    public String getInitialQuery() {
        Intent intent = getIntent();
        String query = null;

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }

        return query;
    }

    @Override
    protected void setResource() {
        this.resource = R.layout.activity_search;
    }

    @Override
    protected void linkNavigates() {
        setSupportActionBar(toolbar);

        Set<Integer> destinations = new HashSet<>();
        destinations.add(R.id.movie_search_fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(destinations).build();

        NavigationUI.setupWithNavController(toolbar, navigationController, appBarConfiguration);
    }
}
