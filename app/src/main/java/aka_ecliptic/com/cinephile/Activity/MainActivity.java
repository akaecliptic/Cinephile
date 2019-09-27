package aka_ecliptic.com.cinephile.Activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import aka_ecliptic.com.cinephile.Fragments.MyListFragment;
import aka_ecliptic.com.cinephile.Fragments.TrendingFragment;
import aka_ecliptic.com.cinephile.Handler.SQLiteHandler;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

public class MainActivity extends AppCompatActivity {

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigationView.setSelectedItemId(R.id.menu_option_watch_list);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                new MyListFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Used to switch fragment according to navigation button selected.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.menu_option_watch_list:
                        selectedFragment = new MyListFragment();
                        break;
                    case R.id.menu_option_trending:
                        selectedFragment = new MyListFragment(); //new TrendingFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        selectedFragment).commit();

                return true;
            };
}
