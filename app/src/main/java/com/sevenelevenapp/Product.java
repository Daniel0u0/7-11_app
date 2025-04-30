package com.sevenelevenapp;

public class Product {
    private String name;
    private double price;
    private String imageUrl;
    private String promotion;

    public Product(String name, double price, String imageUrl, String promotion) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.promotion = promotion;
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

    public String getPromotion() {
        return promotion;
    }
}