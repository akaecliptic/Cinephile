package akaecliptic.dev.cinephile.base;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import akaecliptic.dev.cinephile.R;

public abstract class BaseActivity extends AppCompatActivity {

    @LayoutRes protected int resource;

    protected Toolbar toolbar;
    protected NavHostFragment navigationHostFragment;
    protected NavController navigationController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResource();
        setContentView(this.resource);

        setNavigates();
        linkNavigates();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navigationController) ||
                super.onOptionsItemSelected(item);
    }

    protected abstract void setResource();

    @CallSuper
    protected void setNavigates() {
        this.toolbar = findViewById(R.id.toolbar);
        this.navigationHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_host_fragment);

        if (navigationHostFragment != null)
            navigationController = navigationHostFragment.getNavController();
    }

    protected void linkNavigates(){

    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public NavController getNavigationController() {
        return this.navigationController;
    }
}
