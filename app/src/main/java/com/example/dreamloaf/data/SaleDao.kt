package com.example.dreamloaf.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import androidx.room.Update

@Dao
interface SaleDao {
    @Insert
    fun insert(sale: Sale)

    @Update
    fun update(sale: Sale)

    @Query("DELETE FROM sales WHERE product_id = :productId")
    fun deleteByProductId(productId: Int)

    @Query("SELECT * FROM sales WHERE date = :date ORDER BY id DESC")
    fun getSalesByDate(date: String): LiveData<List<Sale>>

    @Query("SELECT * FROM sales WHERE product_id = :productId AND date = :date LIMIT 1")
    fun getSaleForProductAndDate(productId: Int, date: String): Sale?

    @MapInfo(keyColumn = "month", valueColumn = "total")
    @Query("""
        SELECT strftime('%Y-%m', date) as month, SUM(quantity) as total 
        FROM sales 
        GROUP BY strftime('%Y-%m', date) 
        ORDER BY month DESC
    """)
    fun getMonthlySales(): LiveData<Map<String, Int>>

    @Query("SELECT price FROM products WHERE id = :productId")
    fun getProductPrice(productId: Int): LiveData<Double>

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): LiveData<List<Sale>>
} 