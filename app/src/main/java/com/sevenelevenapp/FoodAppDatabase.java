package com.sevenelevenapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {FoodProduct.class}, version = 2, exportSchema = false) // Incremented version to 2
public abstract class FoodAppDatabase extends RoomDatabase {
    public abstract FoodProductDao foodProductDao();

    // Define the migration from version 1 to version 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add the new 'image' column to the 'food_products' table
            // Since this is a new column, we can set it as nullable and leave existing rows as NULL
            database.execSQL("ALTER TABLE food_products ADD COLUMN image TEXT");
        }
    };
}