package com.sevenelevenapp;

import android.os.Bundle;
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

public class DiscountFragment extends Fragment {

    private RecyclerView productList;
    private ProgressBar loadingIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discount, container, false);

        // Initialize Views
        productList = view.findViewById(R.id.discount_product_list);
        loadingIndicator = view.findViewById(R.id.loading_indicator); // Fixed typo: findViewId -> findViewById

        // Setup RecyclerView with GridLayoutManager (2x2 grid)
        productList.setLayoutManager(new GridLayoutManager(getContext(), 2));

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

        // Display all products
        List<ProductData.Product> displayProducts = new ArrayList<>(products);

        // Setup RecyclerView Adapter
        ProductAdapter adapter = new ProductAdapter(getContext(), displayProducts);
        productList.setAdapter(adapter);

        loadingIndicator.setVisibility(View.GONE);
    }
}