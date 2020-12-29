package aka_ecliptic.com.cinephile;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.Fragment.MyListFragment;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class SearchActivity extends AppCompatActivity {

    private MediaViewModel mediaViewModel;
    public static RequestListener searchRequestListener;
    private static String queryString;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        queryString = null;

        setUpViewModelLink();
        setSupportActionBar(findViewById(R.id.toolbar));

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        NavigationUI.setupWithNavController(findViewById(R.id.toolbar), navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                if(destination.getId() == R.id.movie_list_fragment){
                    makeQueries(queryString);
                }
            }
        });

        ((Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> {
            if(navController.getCurrentDestination() != null){
                if(navController.getCurrentDestination().getId() == R.id.movie_list_fragment){
                    finish();
                    MyListFragment.updateCacheMyList();
                }
                navController.navigateUp();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();

        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String query) {
                    makeQueries(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(navController.getCurrentDestination() != null){
            if(navController.getCurrentDestination().getId() == R.id.movie_list_fragment)
                MyListFragment.updateCacheMyList();
        }
        super.onBackPressed();
    }

    private void setUpViewModelLink() {
        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
    }

    private void makeQueries(@Nullable String search){
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            queryString = (search == null) ? intent.getStringExtra(SearchManager.QUERY) : search;
            mediaViewModel.requestMoviesLike(queryString, 1, movies -> {
                List<Movie> online = Arrays.asList(movies);
                List<String> movieTitles = online.stream().map(Media::getTitle).collect(Collectors.toList());
                List<Movie> movieList = new ArrayList<>(mediaViewModel.getItemsLike(queryString));
                Set<Integer> savedSet = new HashSet<>(mediaViewModel.getItemsLike(movieTitles));

                movieList.addAll(online.stream().filter( m -> !movieList.contains(m)).collect(Collectors.toList()));

                searchRequestListener.onItemsRequested(movieList, savedSet);
            });
        }
    }

    public static void setRequestListener(RequestListener requestListener){
        searchRequestListener = requestListener;
    }

    public interface RequestListener{
        void onItemsRequested(List<Movie> movies, Set<Integer> savedSet);
    }
}
