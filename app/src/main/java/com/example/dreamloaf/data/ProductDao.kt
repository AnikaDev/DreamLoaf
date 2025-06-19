package com.example.dreamloaf.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {
    @Insert
    fun insert(product: Product)

    @get:Query("SELECT * FROM products")
    val allProducts: LiveData<MutableList<Product?>?>?

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): LiveData<Product?>?

    @Query("DELETE FROM products WHERE id = :productId")
    fun delete(productId: Int)

    @Update
    fun update(product: Product)
}