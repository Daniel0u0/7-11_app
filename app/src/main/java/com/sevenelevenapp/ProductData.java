package com.sevenelevenapp;

import android.content.Context;
import androidx.room.Room;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ProductData {
    private static AppDatabase db;
    private static boolean isDatabaseInitialized = false;

    // Initialize the Room database
    public static void initializeDatabase(Context context) {
        if (!isDatabaseInitialized) {
            db = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "product-database")
                    .allowMainThreadQueries() // For simplicity; in production, use background threads
                    .build();

            // Preload the database with data if it's empty
            List<Product> existingProducts = db.productDao().getAllProducts();
            if (existingProducts.isEmpty()) {
                List<Product> initialProducts = new ArrayList<>();
                // Add sample data (from your JSON)
                initialProducts.add(new Product(
                        "Care Bears粉紅色不鏽鋼杯連立體造型蓋",
                        "$159.00",
                        "China",
                        "2024年06月26日 00:00 - 2025年12月31日 23:59",
                        "N/A",
                        "今次7-11推出Care Bears的便攜杯，立體杯蓋用上了兩款Care Bears公仔既可愛頭型，令人一見即愛﹗提提你「見杯飲水」，令你使用時倍感窩心！不鏽鋼杯身連飲管保溫之餘，方便隨時隨地享用，而且又衛生又環保，粉絲們仲唔快啲帶晒佢哋返屋企﹗\n產品尺寸:约12.2 x 12.2 x 29.5 cm\n條款及細則",
                        "https://www.7-eleven.com.hk/zh-hant/p/Care%20Bears%E7%B2%89%E7%B4%85%E8%89%B2%E4%B8%8D%E9%8F%BD%E9%8B%BC%E6%9D%AF%E9%80%A0%E5%9E%8B%E8%93%8B/i/114977471.html"
                ));
                // Add more products as needed
                db.productDao().insertAll(initialProducts);
            }

            isDatabaseInitialized = true;
        }
    }

    public static List<Product> getProducts() {
        if (!isDatabaseInitialized) {
            throw new IllegalStateException("Database not initialized. Call initializeDatabase() first.");
        }
        return db.productDao().getAllProducts();
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