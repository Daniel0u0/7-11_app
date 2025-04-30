package com.sevenelevenapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiscountFragment extends Fragment {

    private ListView productList;
    private ProgressBar loadingIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discount, container, false);

        // Initialize Views
        productList = view.findViewById(R.id.discount_product_list);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Add Header
        View headerView = inflater.inflate(R.layout.header_discount, productList, false);
        productList.addHeaderView(headerView, null, false);

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
        List<ProductData.Product> products = ProductData.getProducts();

        // Sort products by date (newest first)
        Collections.sort(products, new Comparator<ProductData.Product>() {
            @Override
            public int compare(ProductData.Product p1, ProductData.Product p2) {
                return p2.getDate().compareTo(p1.getDate());
            }
        });

        // Display all products
        List<ProductData.Product> displayProducts = new ArrayList<>(products);

        // Setup ListView Adapter
        ProductAdapter adapter = new ProductAdapter(getContext(), displayProducts);
        productList.setAdapter(adapter);

        loadingIndicator.setVisibility(View.GONE);
    }
}