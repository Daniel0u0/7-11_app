package com.sevenelevenapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
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
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private RecyclerView productList;
    private ProgressBar loadingIndicator;
    private TextView nearestStoreName, nearestStoreAddress;
    private FusedLocationProviderClient fusedLocationClient;
    private OkHttpClient okHttpClient;
    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String PLACES_API_KEY = "AIzaSyC6aZ978VkmFvVe3LC27c0PfIGdiC5O-zU"; // Replace with your API key
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        okHttpClient = new OkHttpClient();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        android.net.Network network = cm.getActiveNetwork();
        if (network == null) return false;

        android.net.NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && (
                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }

    private void findNearestSevenEleven(double latitude, double longitude) {
        // Check for internet connectivity
        if (!isNetworkAvailable()) {
            Log.w(TAG, "No internet connection available");
            if (nearestStoreName != null && nearestStoreAddress != null) {
                nearestStoreName.setText("No internet connection");
                nearestStoreAddress.setText("Address: N/A");
            }
            Toast.makeText(getContext(), "No internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        // Use Places API (New) Search Nearby
        String url = "https://places.googleapis.com/v1/places:searchNearby";

        // Create JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("maxResultCount", 20);
            requestBody.put("rankPreference", "DISTANCE");
            JSONObject locationRestriction = new JSONObject();
            JSONObject circle = new JSONObject();
            JSONObject center = new JSONObject();
            center.put("latitude", latitude);
            center.put("longitude", longitude);
            circle.put("center", center);
            circle.put("radius", 20000.0); // 20 km radius
            locationRestriction.put("circle", circle);
            requestBody.put("locationRestriction", locationRestriction);
            JSONArray includedTypes = new JSONArray();
            includedTypes.put("convenience_store");
            requestBody.put("includedTypes", includedTypes);
            requestBody.put("languageCode", "en");
        } catch (Exception e) {
            Log.e(TAG, "Failed to create request body: " + e.getMessage(), e);
            requireActivity().runOnUiThread(() -> {
                if (nearestStoreName != null && nearestStoreAddress != null) {
                    nearestStoreName.setText("Error creating request");
                    nearestStoreAddress.setText("Address: N/A");
                }
                Toast.makeText(getContext(), "Failed to create request: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", PLACES_API_KEY)
                .addHeader("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.location")
                .build();

        Log.d(TAG, "Fetching nearby places with Places API (New)");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to find nearby places: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() -> {
                    if (nearestStoreName != null && nearestStoreAddress != null) {
                        nearestStoreName.setText("Error finding store");
                        nearestStoreAddress.setText("Address: N/A");
                    }
                    Toast.makeText(getContext(), "Failed to find nearby stores: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    Log.e(TAG, "Places API request failed: HTTP " + response.code() + ", Response: " + responseBody);
                    requireActivity().runOnUiThread(() -> {
                        if (nearestStoreName != null && nearestStoreAddress != null) {
                            nearestStoreName.setText("Error finding store");
                            nearestStoreAddress.setText("Address: HTTP " + response.code());
                        }
                        Toast.makeText(getContext(), "Places API request failed: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    });
                    return;
                }


                String responseBody = response.body().string();
                Log.d(TAG, "Places API response: " + responseBody);

                try {
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray places = json.getJSONArray("places");

                    JSONObject nearestPlace = null;
                    double minDistance = Double.MAX_VALUE;

                    for (int i = 0; i < places.length(); i++) {
                        JSONObject place = places.getJSONObject(i);
                        JSONObject displayName = place.getJSONObject("displayName");
                        String placeName = displayName.getString("text");
                        if (placeName.toLowerCase().contains("7-eleven") ||
                                placeName.toLowerCase().contains("seven eleven") ||
                                placeName.toLowerCase().contains("7 eleven") ||
                                placeName.toLowerCase().contains("7-11")) {
                            JSONObject location = place.getJSONObject("location");
                            double placeLat = location.getDouble("latitude");
                            double placeLon = location.getDouble("longitude");
                            double distance = calculateDistance(latitude, longitude, placeLat, placeLon);
                            Log.d(TAG, "Found 7-11: " + placeName + ", distance: " + distance + " km");
                            if (distance < minDistance) {
                                minDistance = distance;
                                nearestPlace = place;
                            }
                        }
                    }

                    final JSONObject finalNearestPlace = nearestPlace;
                    requireActivity().runOnUiThread(() -> {
                        if (nearestStoreName != null && nearestStoreAddress != null) {
                            if (finalNearestPlace != null) {
                                try {
                                    JSONObject displayName = finalNearestPlace.getJSONObject("displayName");
                                    String name = displayName.getString("text");
                                    String address = finalNearestPlace.optString("formattedAddress", "N/A");
                                    Log.d(TAG, "Nearest 7-11 found: " + name);
                                    nearestStoreName.setText(name);
                                    nearestStoreAddress.setText("Address: " + address);
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to parse place data: " + e.getMessage(), e);
                                    nearestStoreName.setText("Error parsing store");
                                    nearestStoreAddress.setText("Address: N/A");
                                }
                            } else {
                                Log.w(TAG, "No 7-11 found nearby");
                                nearestStoreName.setText("No 7-11 found nearby");
                                nearestStoreAddress.setText("Address: N/A");
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse Places API response: " + e.getMessage(), e);
                    requireActivity().runOnUiThread(() -> {
                        if (nearestStoreName != null && nearestStoreAddress != null) {
                            nearestStoreName.setText("Error parsing response");
                            nearestStoreAddress.setText("Address: N/A");
                        }
                        Toast.makeText(getContext(), "Failed to parse response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
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

        List<ProductData.Product> products = ProductData.getSortedProducts();
        Log.d(TAG, "Number of products loaded: " + products.size());

        List<ProductData.Product> displayProducts = products.subList(0, Math.min(products.size(), 5));
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
}