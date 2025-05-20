package com.sevenelevenapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DiscountFragment extends Fragment {

    private RecyclerView productList;
    private ProgressBar loadingIndicator;
    private static final String TAG = "DiscountFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Room database
        ProductData.initializeDatabase(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discount, container, false);

        productList = view.findViewById(R.id.discount_product_list); // Updated ID to match fragment_discount.xml
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Setup RecyclerView with GridLayoutManager (2x2 grid)
        productList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Setup Bottom Navigation (if applicable)
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupBottomNavigation();
        }

        // Load product data
        loadProductData();

        return view;
    }

    private void loadProductData() {
        loadingIndicator.setVisibility(View.VISIBLE);

        List<Product> products = ProductData.getSortedProducts(); // Using standalone Product class
        Log.d(TAG, "Number of products loaded: " + products.size());

        // Filter for discounted products (example)
        List<Product> discountedProducts = new ArrayList<>();
        for (Product product : products) {
            // Example: Consider products with price < $200 as discounted
            try {
                double price = Double.parseDouble(product.getPrice().replace("$", ""));
                if (price < 200) {
                    discountedProducts.add(product);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Failed to parse price: " + product.getPrice(), e);
            }
        }

        List<Product> displayProducts = discountedProducts.subList(0, Math.min(discountedProducts.size(), 5));
        Log.d(TAG, "Number of products to display: " + displayProducts.size());

        if (displayProducts.isEmpty()) {
            Log.w(TAG, "No discounted products to display");
            productList.setVisibility(View.GONE);
        } else {
            ProductAdapter adapter = new ProductAdapter(getContext(), displayProducts);
            productList.setAdapter(adapter);
            productList.setVisibility(View.VISIBLE);
        }

        loadingIndicator.setVisibility(View.GONE);
    }
}