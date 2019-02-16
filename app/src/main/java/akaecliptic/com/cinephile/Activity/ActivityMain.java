package akaecliptic.com.cinephile.Activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import akaecliptic.com.cinephile.R;

public class ActivityMain extends AppCompatActivity {

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
