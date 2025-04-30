package com.sevenelevenapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.sevenelevenapp.DiscountFragment;
import com.sevenelevenapp.HomeFragment;
import com.sevenelevenapp.PreorderFragment;
import com.sevenelevenapp.ProductFragment;
import com.sevenelevenapp.LoginFragment;
import com.sevenelevenapp.RegisterFragment;
import com.sevenelevenapp.HelpFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ImageView menuIcon;
    private Button loginButton;

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
        menuIcon = findViewById(R.id.menu_icon);
        loginButton = findViewById(R.id.login_button);

        // Open Drawer on Menu Icon Click
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigate to LoginFragment on Login Button Click
        loginButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
            // Deselect all BottomNavigationView items
            bottomNavigationView.setSelectedItemId(-1);
            // Deselect all NavigationView items
            navigationView.setCheckedItem(-1);
        });

        // Navigation Drawer Item Selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .addToBackStack(null)
                            .commit();
                    // Sync BottomNavigationView with NavigationView
                    if (item.getItemId() != R.id.nav_login_register) {
                        bottomNavigationView.setSelectedItemId(item.getItemId());
                    } else {
                        bottomNavigationView.setSelectedItemId(-1);
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
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
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    // Method to set up BottomNavigationView
    public void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
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
}