package com.sevenelevenapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "food_products")
public class FoodProduct implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String price;
    private String original_price;
    private String product_details;
    private String image; // Added image field to store drawable name

    // Updated constructor
    public FoodProduct(String name, String price, String original_price, String product_details, String image) {
        this.name = name;
        this.price = price;
        this.original_price = original_price;
        this.product_details = product_details;
        this.image = image;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
    }

    public String getProduct_details() {
        return product_details;
    }

    public void setProduct_details(String product_details) {
        this.product_details = product_details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}