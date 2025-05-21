package com.sevenelevenapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertAll(List<Product> products);

    @Query("SELECT * FROM products ORDER BY pre_order_date DESC")
    List<Product> getAllProducts();
}