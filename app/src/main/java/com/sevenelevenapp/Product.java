package com.sevenelevenapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "products")
public class Product implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String product_name;
    private String price;
    private String origin;
    private String pre_order_date;
    private String pickup_date;
    private String product_details;
    private String link;

    // Constructor
    public Product(String product_name, String price, String origin, String pre_order_date, String pickup_date, String product_details, String link) {
        this.product_name = product_name;
        this.price = price;
        this.origin = origin;
        this.pre_order_date = pre_order_date;
        this.pickup_date = pickup_date;
        this.product_details = product_details;
        this.link = link;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPre_order_date() {
        return pre_order_date;
    }

    public void setPre_order_date(String pre_order_date) {
        this.pre_order_date = pre_order_date;
    }

    public String getPickup_date() {
        return pickup_date;
    }

    public void setPickup_date(String pickup_date) {
        this.pickup_date = pickup_date;
    }

    public String getProduct_details() {
        return product_details;
    }

    public void setProduct_details(String product_details) {
        this.product_details = product_details;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}