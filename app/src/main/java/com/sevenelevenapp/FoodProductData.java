package com.sevenelevenapp;

import android.content.Context;
import android.util.Log;
import androidx.room.Room;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class FoodProductData {
    private static FoodAppDatabase db;
    private static boolean isDatabaseInitialized = false;
    private static final String TAG = "FoodProductData";

    public static void initializeDatabase(Context context) {
        if (!isDatabaseInitialized) {
            db = Room.databaseBuilder(context.getApplicationContext(),
                            FoodAppDatabase.class, "food-product-database")
                    .allowMainThreadQueries()
                    .addMigrations(FoodAppDatabase.MIGRATION_1_2) // Added migration
                    .build();

            db.foodProductDao().deleteAll();
            Log.d(TAG, "Food product database cleared");

            List<FoodProduct> initialProducts = new ArrayList<>();
            try {
                InputStream is = context.getResources().openRawResource(R.raw.new_product_food);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                JSONArray jsonArray = new JSONArray(json);
                Log.d(TAG, "Number of food products in JSON: " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String originalPrice = jsonObject.has("original_price") ? jsonObject.getString("original_price") : null;
                    String image = jsonObject.has("image") ? jsonObject.getString("image") : null;
                    initialProducts.add(new FoodProduct(
                            jsonObject.getString("name"),
                            jsonObject.getString("price"),
                            originalPrice,
                            jsonObject.getString("product_details"),
                            image
                    ));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load JSON data: " + e.getMessage(), e);
            }

            Log.d(TAG, "Number of food products to insert: " + initialProducts.size());
            if (!initialProducts.isEmpty()) {
                db.foodProductDao().insertAll(initialProducts);
            }

            isDatabaseInitialized = true;
        }
    }

    public static List<FoodProduct> getProducts() {
        if (!isDatabaseInitialized) {
            throw new IllegalStateException("Food product database not initialized. Call initializeDatabase() first.");
        }
        List<FoodProduct> products = db.foodProductDao().getAllProducts();
        Log.d(TAG, "Number of food products retrieved from database: " + products.size());
        return products;
    }

    public static List<FoodProduct> getSortedProducts() {
        List<FoodProduct> sortedProducts = new ArrayList<>(getProducts());
        Collections.sort(sortedProducts, (p1, p2) -> p1.getName().compareTo(p2.getName()));
        return sortedProducts;
    }
}