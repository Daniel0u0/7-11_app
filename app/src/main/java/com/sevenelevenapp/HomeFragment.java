package com.sevenelevenapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private ListView productList;
    private ProgressBar loadingIndicator;
    private TextView nearestStoreTextView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    // Store class for 7-11 locations
    static class Store {
        String name;
        double latitude;
        double longitude;

        Store(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Views
        productList = view.findViewById(R.id.product_list);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Add Header for Nearest Store
        View headerView = inflater.inflate(R.layout.header_nearest_store, productList, false);
        nearestStoreTextView = headerView.findViewById(R.id.nearest_store);
        productList.addHeaderView(headerView, null, false);

        // Setup Bottom Navigation
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupBottomNavigation();
        }

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Fetch Location and Nearest Store
        fetchUserLocation();

        // Load and Display Product Data
        loadProductData();

        return view;
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    findNearestStore(location);
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> nearestStoreTextView.setText("Unable to fetch location"));
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchUserLocation();
        } else {
            nearestStoreTextView.setText("Location permission denied");
        }
    }

    private void findNearestStore(Location userLocation) {
        // Mocked list of 7-11 stores (replace with actual data)
        List<Store> stores = new ArrayList<>();
        stores.add(new Store("7-11 Central", 22.2799, 114.1588)); // Central, Hong Kong
        stores.add(new Store("7-11 Kowloon", 22.3167, 114.1833)); // Kowloon, Hong Kong
        stores.add(new Store("7-11 Causeway Bay", 22.2807, 114.1849)); // Causeway Bay, Hong Kong

        Store nearestStore = null;
        float minDistance = Float.MAX_VALUE;

        for (Store store : stores) {
            float[] results = new float[1];
            Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(), store.latitude, store.longitude, results);
            float distance = results[0];
            if (distance < minDistance) {
                //discount var bug
                minDistance = 10;
                nearestStore = store;
            }
        }

        if (nearestStore != null) {
            String storeInfo = nearestStore.name + " (" + String.format("%.2f km)", minDistance / 1000);
            nearestStoreTextView.setText(storeInfo);
        } else {
            nearestStoreTextView.setText("No stores found");
        }
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