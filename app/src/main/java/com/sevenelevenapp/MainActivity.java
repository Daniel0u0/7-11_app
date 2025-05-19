package com.sevenelevenapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Button loginButton;
    private TextView toolbarTitle, toolbarUsername;
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Drawer and Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loginButton = findViewById(R.id.login_button);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarUsername = findViewById(R.id.toolbar_username);

        // Set up ActionBarDrawerToggle for the hamburger icon
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Navigation Drawer Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (item.getItemId() == R.id.nav_logout) {
                logoutUser();
                return true;
            }
            if (selectedFragment != null) {
                navigateToFragment(selectedFragment);
                if (item.getItemId() != R.id.nav_login_register) {
                    bottomNavigationView.setSelectedItemId(item.getItemId());
                } else {
                    bottomNavigationView.setSelectedItemId(-1);
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Check user authentication state
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (savedInstanceState == null) {
            if (currentUser != null) {
                fetchUserDataAndNavigate(currentUser, new HomeFragment(), R.id.nav_home);
            } else {
                updateUIForLoggedOutUser();
                navigateToFragment(new LoginFragment());
                navigationView.setCheckedItem(R.id.nav_login_register);
                bottomNavigationView.setSelectedItemId(-1);
            }
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
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (!(currentFragment instanceof HomeFragment) && !(currentFragment instanceof LoginFragment)) {
                        fetchUserDataAndNavigate(mAuth.getCurrentUser(), new HomeFragment(), R.id.nav_home);
                    } else if (currentFragment instanceof LoginFragment) {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                        setEnabled(true);
                    } else {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                        setEnabled(true);
                    }
                }
            }
        });

        // Update login button based on user state
        loginButton.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                logoutUser();
            } else {
                navigateToFragment(new LoginFragment());
                bottomNavigationView.setSelectedItemId(-1);
                navigationView.setCheckedItem(R.id.nav_login_register);
            }
        });
    }

    public void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (selectedFragment != null) {
                navigateToFragment(selectedFragment);
                navigationView.setCheckedItem(item.getItemId());
            }
            return true;
        });
    }

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
            return new LoginFragment();
        }
        return null;
    }

    private void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        // Show username only on HomeFragment when logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && fragment instanceof HomeFragment) {
            toolbarUsername.setText(currentUsername != null ? currentUsername : "");
            toolbarUsername.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            toolbarUsername.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserDataAndNavigate(FirebaseUser user, Fragment fragment, int navItemId) {
        if (user == null) {
            updateUIForLoggedOutUser();
            navigateToFragment(new LoginFragment());
            navigationView.setCheckedItem(R.id.nav_login_register);
            bottomNavigationView.setSelectedItemId(-1);
            return;
        }

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        RegisterFragment.User userData = document.toObject(RegisterFragment.User.class);
                        if (userData != null) {
                            currentUsername = userData.getUsername();
                        } else {
                            currentUsername = null;
                        }
                    } else {
                        currentUsername = null;
                    }
                    updateUIForLoggedInUser(user);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                    navigationView.setCheckedItem(navItemId);
                    bottomNavigationView.setSelectedItemId(navItemId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    updateUIForLoggedOutUser();
                    navigateToFragment(new LoginFragment());
                    navigationView.setCheckedItem(R.id.nav_login_register);
                    bottomNavigationView.setSelectedItemId(-1);
                });
    }

    public void onUserLoggedIn(FirebaseUser user) {
        fetchUserDataAndNavigate(user, new HomeFragment(), R.id.nav_home);
    }

    private void updateUIForLoggedInUser(FirebaseUser user) {
        loginButton.setText("Logout");
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment) {
            toolbarUsername.setText(currentUsername != null ? currentUsername : "");
            toolbarUsername.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }

        // Update NavigationView menu
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_login_register).setVisible(false);
        menu.findItem(R.id.nav_logout).setVisible(true);
    }

    private void updateUIForLoggedOutUser() {
        loginButton.setText("Login");
        toolbarTitle.setText("Hong Kong 7-11");
        toolbarUsername.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        currentUsername = null;

        // Update NavigationView menu
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_login_register).setVisible(true);
        menu.findItem(R.id.nav_logout).setVisible(false);
    }

    private void logoutUser() {
        mAuth.signOut();
        updateUIForLoggedOutUser();
        navigateToFragment(new LoginFragment());
        navigationView.setCheckedItem(R.id.nav_login_register);
        bottomNavigationView.setSelectedItemId(-1);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}