package com.sevenelevenapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PreorderFragment extends Fragment {

    private RecyclerView productList;
    private ProgressBar loadingIndicator;
    private static final String TAG = "PreorderFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preorder, container, false);

        // Initialize Views
        productList = view.findViewById(R.id.preorder_product_list);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Setup RecyclerView with GridLayoutManager (2x2 grid)
        productList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Ensure RecyclerView is visible initially
        productList.setVisibility(View.VISIBLE);

        // Setup Bottom Navigation
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupBottomNavigation();
        }

        // Load and Display Product Data
        loadProductData();

        return view;
    }

    private void loadProductData() {
        loadingIndicator.setVisibility(View.VISIBLE);

        // Load products from ProductData
        List<ProductData.Product> products = ProductData.getSortedProducts(); // Use sorted products directly
        Log.d(TAG, "Number of products loaded: " + products.size());

        // Display all products
        List<ProductData.Product> displayProducts = new ArrayList<>(products);
        Log.d(TAG, "Number of products to display: " + displayProducts.size());

        // Setup RecyclerView Adapter
        if (displayProducts.isEmpty()) {
            Log.w(TAG, "No products to display");
            productList.setVisibility(View.GONE);
        } else {
            ProductAdapter adapter = new ProductAdapter(getContext(), displayProducts);
            productList.setAdapter(adapter);
            productList.setVisibility(View.VISIBLE);
        }

        loadingIndicator.setVisibility(View.GONE);
    }
}