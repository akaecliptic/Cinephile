package akaecliptic.dev.cinephile;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static SortClickListener sortListener;

    /**
     * Creates activity, initialises bottom navigation bar, and then adds entry fragment
     * to fragment container.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);

        NavigationUI.setupWithNavController(findViewById(R.id.toolbar), navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);

        ((Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> {
            if(navController.getCurrentDestination() != null){
                if(navController.getCurrentDestination().getId() == R.id.mylist_fragment)
                    sortListener.onSort();
                navController.navigateUp();
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                if(destination.getId() == R.id.mylist_fragment){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_sort);
                }else {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                }
            }
        });

        bottomNav.setOnNavigationItemReselectedListener(menuItem -> {

        });
    }

    /**
     * Inflates menu with custom options.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    public static void setSortClickListener(SortClickListener sortClickListener){
        sortListener = sortClickListener;
    }

    public interface SortClickListener{
        void onSort();
    }
}
