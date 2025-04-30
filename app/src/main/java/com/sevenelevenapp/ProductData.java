package com.sevenelevenapp;

import java.util.ArrayList;
import java.util.List;

public class ProductData {
    public static class Product {
        private String name;
        private double price;
        private String imageUrl;
        private String date; // Added for sorting by newest

        public Product(String name, double price, String imageUrl, String date) {
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getDate() {
            return date;
        }
    }

    public static List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Stitch 深度防水筆袋", 799.00, "https://www.7-eleven.com.hk/product1.jpg", "2025-04-30"));
        products.add(new Product("Stitch 26\" 防潑水日本旅行箱", 899.00, "https://www.7-eleven.com.hk/product2.jpg", "2025-04-29"));
        products.add(new Product("迪士尼真優座椅 Stitch (Stitch&Scrump)", 299.00, "https://www.7-eleven.com.hk/product3.jpg", "2025-04-28"));
        products.add(new Product("i-Smart LED 無框燈鏡", 268.00, "https://www.7-eleven.com.hk/product4.jpg", "2025-04-27"));
        products.add(new Product("Stitch 系列白色行李箱24\"", 899.00, "https://www.7-eleven.com.hk/product5.jpg", "2025-04-26"));
        products.add(new Product("Stitch 紙幣1000點快現 (2 個)", 99.00, "https://www.7-eleven.com.hk/product6.jpg", "2025-04-25"));
        products.add(new Product("i-Smart 1000點快現 Stitch", 438.00, "https://www.7-eleven.com.hk/product7.jpg", "2025-04-24"));
        return products;
    }
}