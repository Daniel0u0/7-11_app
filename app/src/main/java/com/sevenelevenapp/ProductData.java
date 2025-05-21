package com.sevenelevenapp;

import android.content.Context;
import android.util.Log;
import androidx.room.Room;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProductData {
    private static AppDatabase db;
    private static boolean isDatabaseInitialized = false;
    private static final String TAG = "ProductData";

    // Initialize the Room database
    public static void initializeDatabase(Context context) {
        if (!isDatabaseInitialized) {
            db = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "product-database")
                    .allowMainThreadQueries() // For simplicity; in production, use background threads
                    .build();

            // Clear the database to ensure fresh data (for testing purposes)
            db.clearAllTables();
            Log.d(TAG, "Database cleared");

            // Load data from JSON file
            List<Product> initialProducts = new ArrayList<>();
            try {
                // Read JSON file from res/raw
                InputStream is = context.getResources().openRawResource(R.raw.product_data);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                // Parse JSON
                JSONArray jsonArray = new JSONArray(json);
                Log.d(TAG, "Number of products in JSON: " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    initialProducts.add(new Product(
                            jsonObject.getString("product_name"),
                            jsonObject.getString("price"),
                            jsonObject.getString("origin"),
                            jsonObject.getString("pre_order_date"),
                            jsonObject.getString("pickup_date"),
                            jsonObject.getString("product_details"),
                            jsonObject.getString("link")
                    ));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load JSON data: " + e.getMessage(), e);
            }

            // Insert into database
            Log.d(TAG, "Number of products to insert: " + initialProducts.size());
            if (!initialProducts.isEmpty()) {
                db.productDao().insertAll(initialProducts);
            }

            isDatabaseInitialized = true;
        }
    }

    public static List<Product> getProducts() {
        if (!isDatabaseInitialized) {
            throw new IllegalStateException("Database not initialized. Call initializeDatabase() first.");
        }
        List<Product> products = db.productDao().getAllProducts();
        Log.d(TAG, "Number of products retrieved from database: " + products.size());
        return products;
    }

    // Helper method to sort products by pre_order_date (newest first)
    public static List<Product> getSortedProducts() {
        List<Product> sortedProducts = new ArrayList<>(getProducts());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Collections.sort(sortedProducts, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                try {
                    // Extract the start date from the pre_order_date range (e.g., "2024年06月26日 00:00 - 2025年12月31日 23:59")
                    String startDate1 = p1.getPre_order_date().split(" - ")[0];
                    String startDate2 = p2.getPre_order_date().split(" - ")[0];
                    Date date1 = dateFormat.parse(startDate1);
                    Date date2 = dateFormat.parse(startDate2);
                    return date2.compareTo(date1); // Sort descending (newest first)
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return sortedProducts;
    }
}