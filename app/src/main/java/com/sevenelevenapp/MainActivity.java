package com.sevenelevenapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Button loginButton;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Drawer and Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loginButton = findViewById(R.id.login_button);

        // Set up ActionBarDrawerToggle for the hamburger icon
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Navigate to LoginFragment on Login Button Click
        loginButton.setOnClickListener(v -> {
            navigateToFragment(new LoginFragment());
            // Deselect all BottomNavigationView and NavigationView items
            bottomNavigationView.setSelectedItemId(-1);
            navigationView.setCheckedItem(-1);
        });

        // Navigation Drawer Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (selectedFragment != null) {
                navigateToFragment(selectedFragment);
                // Sync BottomNavigationView with NavigationView
                if (item.getItemId() != R.id.nav_login_register) {
                    bottomNavigationView.setSelectedItemId(item.getItemId());
                } else {
                    bottomNavigationView.setSelectedItemId(-1);
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            navigateToFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // Setup BottomNavigationView
        setupBottomNavigation();

        // Handle Back Press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If not on HomeFragment, navigate back to HomeFragment
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (!(currentFragment instanceof HomeFragment)) {
                        navigateToFragment(new HomeFragment());
                        navigationView.setCheckedItem(R.id.nav_home);
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                    } else {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                        setEnabled(true);
                    }
                }
            }
        });
    }

    // Method to set up BottomNavigationView
    public void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (selectedFragment != null) {
                navigateToFragment(selectedFragment);
                // Sync NavigationView with BottomNavigationView
                navigationView.setCheckedItem(item.getItemId());
            }
            return true;
        });
    }

    // Method to determine the fragment based on menu item ID
    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.nav_home) {
            return new HomeFragment();
        } else if (itemId == R.id.nav_discount) {
            return new DiscountFragment();
        } else if (itemId == R.id.nav_preorder) {
            return new PreorderFragment();
        } else if (itemId == R.id.nav_product) {
            return new ProductFragment();
        } else if (itemId == R.id.nav_help) {
            return new HelpFragment();
        } else if (itemId == R.id.nav_login_register) {
            return new LoginFragment(); // Navigate to LoginFragment by default
        }
        return null;
    }

    // Helper method to navigate to a fragment
    private void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}