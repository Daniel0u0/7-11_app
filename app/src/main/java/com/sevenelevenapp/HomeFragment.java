package com.sevenelevenapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView productList;
    private ProgressBar loadingIndicator;

    // Product class (same as before)
    static class Product {
        String name;
        String price;
        String imageUrl;
        String storeLocation;
        String sentimentScore;

        Product(String name, String price, String imageUrl, String storeLocation, String sentimentScore) {
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.storeLocation = storeLocation;
            this.sentimentScore = sentimentScore;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Views
        productList = view.findViewById(R.id.product_list);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Setup RecyclerView
        productList.setLayoutManager(new LinearLayoutManager(getContext()));
        productList.setAdapter(new ProductAdapter(new ArrayList<>()));

        // Setup Bottom Navigation
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupBottomNavigation();
        }

        // Fetch and Display Data
        new Thread(this::fetchAndProcessData).start();

        return view;
    }

    // Fetch and Process Data (same as before)
    private void fetchAndProcessData() {
        List<Product> products = new ArrayList<>();
        try {
            // Simulate API call
            String apiUrl = "http://your-python-api.example.com/scrape";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String name = jsonResponse.getString("name");
            String price = jsonResponse.getString("price");
            String imageUrl = jsonResponse.getString("image_url");
            String location = jsonResponse.getString("store_location");
            String sentimentScore = performSentimentAnalysis(name);

            Product product = new Product(name, price, imageUrl, location, sentimentScore);
            products.add(product);

            // Update UI
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    productList.setAdapter(new ProductAdapter(products));
                    loadingIndicator.setVisibility(View.GONE);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> loadingIndicator.setVisibility(View.GONE));
            }
        }
    }

    // Simulate Sentiment Analysis (same as before)
    private String performSentimentAnalysis(String productName) {
        try {
            String sentimentApiUrl = "http://your-python-api.example.com/sentiment?product=" + productName;
            URL url = new URL(sentimentApiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("sentiment_score");

        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    // RecyclerView Adapter
    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private List<Product> products;

        public ProductAdapter(List<Product> products) {
            this.products = products;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product product = products.get(position);
            holder.productName.setText(product.name);
            holder.productPrice.setText("Price: " + product.price);
            holder.storeLocation.setText("Store: " + product.storeLocation);
            holder.sentimentScore.setText("Sentiment Score: " + product.sentimentScore);
            // Load image with Glide/Picasso if needed
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView productName, productPrice, storeLocation, sentimentScore;
            ImageView productImage;

            ViewHolder(View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.product_name);
                productPrice = itemView.findViewById(R.id.product_price);
                storeLocation = itemView.findViewById(R.id.store_location);
                sentimentScore = itemView.findViewById(R.id.sentiment_score);
                productImage = itemView.findViewById(R.id.product_image);
            }
        }
    }
}