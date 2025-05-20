package com.sevenelevenapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView productList;
    private ProgressBar loadingIndicator;
    private TextView nearestStoreName, nearestStoreAddress;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Hardcoded list of 7-11 stores in Hong Kong
    private List<Store> hardcodedStores;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize the Room database
        ProductData.initializeDatabase(requireContext());

        // Initialize hardcoded list of 7-11 stores
        hardcodedStores = new ArrayList<>();
        hardcodedStores.add(new Store("7-Eleven Tuen Mun", "Shop 1, Tuen Mun Shopping Centre, Hong Kong", 22.3915, 113.9772));
        hardcodedStores.add(new Store("7-Eleven Central", "Shop 2, Central Plaza, Hong Kong", 22.2800, 114.1589));
        hardcodedStores.add(new Store("7-Eleven Tsim Sha Tsui", "789 Nathan Road, Kowloon, Hong Kong", 22.3000, 114.1720));
        hardcodedStores.add(new Store("7-Eleven Sham Shui Po", "123 Cheung Sha Wan Road, Sham Shui Po, Hong Kong", 22.3300, 114.1600));
        hardcodedStores.add(new Store("7-Eleven Causeway Bay", "456 Hennessy Road, Causeway Bay, Hong Kong", 22.2790, 114.1830));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        productList = view.findViewById(R.id.product_list);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        nearestStoreName = view.findViewById(R.id.nearest_store_name);
        nearestStoreAddress = view.findViewById(R.id.nearest_store_address);

        // Setup RecyclerView with GridLayoutManager (2x2 grid)
        productList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Setup Bottom Navigation
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupBottomNavigation();
        }

        // Load product data
        loadProductData();

        // Fetch user's location and find nearest 7-11 store
        fetchUserLocation();

        return view;
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting location permissions");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        Log.d(TAG, "Fetching last known location");
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d(TAG, "Location fetched: " + location.getLatitude() + ", " + location.getLongitude());
                        findNearestSevenEleven(location.getLatitude(), location.getLongitude());
                    } else {
                        Log.w(TAG, "Location is null");
                        if (nearestStoreName != null && nearestStoreAddress != null) {
                            nearestStoreName.setText("Unable to fetch location");
                            nearestStoreAddress.setText("Address: N/A");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch location: " + e.getMessage(), e);
                    if (nearestStoreName != null && nearestStoreAddress != null) {
                        nearestStoreName.setText("Location error");
                        nearestStoreAddress.setText("Address: N/A");
                    }
                    Toast.makeText(getContext(), "Failed to fetch location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permissions granted");
                fetchUserLocation();
            } else {
                Log.w(TAG, "Location permissions denied");
                if (nearestStoreName != null && nearestStoreAddress != null) {
                    nearestStoreName.setText("Permission denied");
                    nearestStoreAddress.setText("Address: N/A");
                }
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void findNearestSevenEleven(double latitude, double longitude) {
        Log.d(TAG, "Processing hardcoded 7-11 stores");

        if (hardcodedStores.isEmpty()) {
            Log.w(TAG, "No 7-11 stores available");
            if (nearestStoreName != null && nearestStoreAddress != null) {
                nearestStoreName.setText("No 7-11 stores found");
                nearestStoreAddress.setText("Address: N/A");
            }
            return;
        }

        // Find the nearest store
        Store nearestStore = null;
        double minDistance = Double.MAX_VALUE;
        for (Store store : hardcodedStores) {
            double distance = calculateDistance(latitude, longitude, store.latitude, store.longitude);
            Log.d(TAG, "Store: " + store.name + ", distance: " + distance + " km");
            if (distance < minDistance) {
                minDistance = distance;
                nearestStore = store;
            }
        }

        if (nearestStoreName != null && nearestStoreAddress != null) {
            if (nearestStore != null) {
                Log.d(TAG, "Nearest 7-11 found: " + nearestStore.name);
                nearestStoreName.setText(nearestStore.name);
                nearestStoreAddress.setText("Address: " + nearestStore.address);
            } else {
                Log.w(TAG, "No 7-11 found nearby");
                nearestStoreName.setText("No 7-11 found nearby");
                nearestStoreAddress.setText("Address: N/A");
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }

    private void loadProductData() {
        loadingIndicator.setVisibility(View.VISIBLE);

        List<Product> products = ProductData.getSortedProducts(); // Changed from ProductData.Product to Product
        Log.d(TAG, "Number of products loaded: " + products.size());

        List<Product> displayProducts = products.subList(0, Math.min(products.size(), 5)); // Changed from ProductData.Product to Product
        Log.d(TAG, "Number of products to display: " + displayProducts.size());

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

    // Helper class to represent a store
    private static class Store {
        String name;
        String address;
        double latitude;
        double longitude;

        Store(String name, String address, double latitude, double longitude) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}