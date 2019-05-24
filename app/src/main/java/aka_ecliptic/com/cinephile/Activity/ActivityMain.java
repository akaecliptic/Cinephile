package aka_ecliptic.com.cinephile.Activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import aka_ecliptic.com.cinephile.Fragments.MyListFragment;
import aka_ecliptic.com.cinephile.Fragments.SearchFragment;
import aka_ecliptic.com.cinephile.Fragments.TrendingFragment;
import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.R;

public class ActivityMain extends AppCompatActivity {

    /**
     * Used to create activity, initialise bottom navigation bar, and then add entry fragment
     * to fragment container.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigationView.setSelectedItemId(R.id.menu_option_personal);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new MyListFragment()).commit();

    }

    /**
     * Used to switch fragment according to navigation button selected.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.menu_option_personal:
                        selectedFragment = new MyListFragment();
                        break;
                    case R.id.menu_option_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.menu_option_trending:
                        selectedFragment = new TrendingFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        selectedFragment).commit();

                return true;
            };
}
