package com.sevenelevenapp;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import java.util.List;

public class ProductFragment extends Fragment {
    private RecyclerView productRecyclerView;
    private CardView productDetailsContainer;
    private ImageView productImageView;
    private TextView productNameTextView, productPriceTextView, productOriginalPriceTextView, productDetailsTextView;
    private Button backButton;
    private ProgressBar loadingIndicator;
    private FoodProductAdapter adapter;
    private List<FoodProduct> products;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        productRecyclerView = view.findViewById(R.id.product_list);
        productDetailsContainer = view.findViewById(R.id.product_details_container);
        productImageView = view.findViewById(R.id.product_image);
        productNameTextView = view.findViewById(R.id.product_name);
        productPriceTextView = view.findViewById(R.id.product_price);
        productOriginalPriceTextView = view.findViewById(R.id.product_original_price);
        productDetailsTextView = view.findViewById(R.id.product_details);
        backButton = view.findViewById(R.id.back_button);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Set up RecyclerView with GridLayoutManager (2 columns)
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        productRecyclerView.setLayoutManager(layoutManager);

        // Initialize database and load products
        loadingIndicator.setVisibility(View.VISIBLE);
        FoodProductData.initializeDatabase(requireContext());
        products = FoodProductData.getProducts();

        // Set up adapter with a custom click listener for details
        adapter = new FoodProductAdapter(requireContext(), products) {
            @Override
            public void onBindViewHolder(@NonNull FoodProductViewHolder holder, int position) {
                FoodProduct product = products.get(position);

                // Bind data to views
                holder.productName.setText(product.getName());
                holder.productPrice.setText(product.getPrice());

                // Handle Details click
                holder.detailsLink.setOnClickListener(v -> {
                    showProductDetails(product);
                });

                // Add to Cart Button click listener (still present in the list view)
                holder.addToCartButton.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                });
            }
        };
        productRecyclerView.setAdapter(adapter);

        loadingIndicator.setVisibility(View.GONE);

        // Back button to return to the list
        backButton.setOnClickListener(v -> showProductList());
    }

    // Show the product list view
    private void showProductList() {
        productRecyclerView.setVisibility(View.VISIBLE);
        productDetailsContainer.setVisibility(View.GONE);
    }

    // Show the product details view
    private void showProductDetails(FoodProduct product) {
        productRecyclerView.setVisibility(View.GONE);
        productDetailsContainer.setVisibility(View.VISIBLE);

        // Bind product details
        // Load the image using the drawable name from the product's image field
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            int resId = getResources().getIdentifier(product.getImage(), "drawable", requireContext().getPackageName());
            if (resId != 0) { // Check if the resource exists
                productImageView.setImageResource(resId);
            } else {
                productImageView.setImageResource(R.drawable.placeholder_image); // Fallback to placeholder
            }
        } else {
            productImageView.setImageResource(R.drawable.placeholder_image); // Fallback to placeholder
        }

        productNameTextView.setText(product.getName());
        productPriceTextView.setText(product.getPrice());
        if (product.getOriginal_price() != null && !product.getOriginal_price().isEmpty()) {
            productOriginalPriceTextView.setText(product.getOriginal_price());
            productOriginalPriceTextView.setVisibility(View.VISIBLE);
            productOriginalPriceTextView.setPaintFlags(
                    productOriginalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            productOriginalPriceTextView.setVisibility(View.GONE);
            productOriginalPriceTextView.setPaintFlags(
                    productOriginalPriceTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
        }
        productDetailsTextView.setText(product.getProduct_details() != null && !product.getProduct_details().isEmpty()
                ? product.getProduct_details()
                : "No details available");
    }
}