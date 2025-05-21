package com.sevenelevenapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView nameTextView, priceTextView, originTextView, preOrderDateTextView, pickupDateTextView, detailsTextView, linkTextView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back button
            getSupportActionBar().setTitle("Product Details");
        }

        // Initialize Views
        nameTextView = findViewById(R.id.detail_product_name);
        priceTextView = findViewById(R.id.detail_product_price);
        originTextView = findViewById(R.id.detail_product_origin);
        preOrderDateTextView = findViewById(R.id.detail_pre_order_date);
        pickupDateTextView = findViewById(R.id.detail_pickup_date);
        detailsTextView = findViewById(R.id.detail_product_details);
        linkTextView = findViewById(R.id.detail_product_link);

        // Initialize Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        // Get Product Data from Intent
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            nameTextView.setText(product.getProduct_name());
            priceTextView.setText(product.getPrice());
            originTextView.setText("Origin: " + product.getOrigin());
            preOrderDateTextView.setText("Pre-Order Date: " + product.getPre_order_date());
            pickupDateTextView.setText("Pickup Date: " + product.getPickup_date());
            detailsTextView.setText(product.getProduct_details());
            linkTextView.setText(product.getLink()); // Removed "Link: " prefix

            // Make the link clickable
            linkTextView.setOnClickListener(v -> {
                String url = product.getLink();
                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        } else {
            nameTextView.setText("Error: Product data not found");
            linkTextView.setText("Link: N/A");
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = new Intent(this, MainActivity.class);
            if (itemId == R.id.nav_home) {
                intent.putExtra("fragment", "home");
            } else if (itemId == R.id.nav_discount) {
                intent.putExtra("fragment", "discount");
            } else if (itemId == R.id.nav_preorder) {
                intent.putExtra("fragment", "preorder");
            } else {
                return false;
            }
            startActivity(intent);
            finish(); // Close ProductDetailActivity to return to MainActivity
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close the activity when the back button is pressed
        return true;
    }
}