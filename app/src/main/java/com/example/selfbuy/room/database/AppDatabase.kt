package com.example.selfbuy.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.selfbuy.room.dao.ProductDao
import com.example.selfbuy.room.entity.Product

@Database(entities = [Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}