package com.example.selfbuy.room.dao

import androidx.room.*
import com.example.selfbuy.room.entity.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE uid = :uidProduct")
    fun getById(uidProduct: String): Product?

    @Query("SELECT * FROM product WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Product

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    fun insertAll(vararg products: Product)

    @Delete
    fun delete(product: Product)

    @Update
    fun update(product: Product)
}