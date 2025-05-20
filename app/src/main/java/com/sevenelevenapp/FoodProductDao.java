package com.sevenelevenapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FoodProductDao {
    @Insert
    void insertAll(List<FoodProduct> products);

    @Query("SELECT * FROM food_products")
    List<FoodProduct> getAllProducts();

    @Query("DELETE FROM food_products")
    void deleteAll();
}