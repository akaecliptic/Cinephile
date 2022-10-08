package akaecliptic.dev.cinephile.Activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

import akaecliptic.dev.cinephile.R;

public class MainActivity extends AppCompatActivity {

    private NavController navigationController;
    private Toolbar toolbar;
    private BottomNavigationView bottombar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottombar = findViewById(R.id.bottombar);
        NavHostFragment navigationHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_host_fragment);

        if (navigationHostFragment != null)
            navigationController = navigationHostFragment.getNavController();

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
        SearchView searchView = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navigationController) ||
                super.onOptionsItemSelected(item);
    }

    public NavController getNavigationController() {
        return this.navigationController;
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public BottomNavigationView getBottombar() {
        return this.bottombar;
    }
}
