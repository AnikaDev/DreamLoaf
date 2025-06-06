package com.example.dreamloaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;
import androidx.room.MapInfo;

import java.util.List;
import java.util.Map;

@Dao
public interface SaleDao {
    @Insert
    void insert(Sale sale);

    @Update
    void update(Sale sale);

    @Query("DELETE FROM sales WHERE product_id = :productId")
    void deleteByProductId(int productId);

    @Query("SELECT * FROM sales WHERE date = :date ORDER BY id DESC")
    LiveData<List<Sale>> getSalesByDate(String date);

    @Query("SELECT * FROM sales WHERE product_id = :productId AND date = :date LIMIT 1")
    Sale getSaleForProductAndDate(int productId, String date);

    @MapInfo(keyColumn = "month", valueColumn = "total")
    @Query("SELECT strftime('%Y-%m', date) as month, SUM(quantity) as total " +
            "FROM sales " +
            "GROUP BY strftime('%Y-%m', date) " +
            "ORDER BY month DESC")
    LiveData<Map<String, Integer>> getMonthlySales();

    @Query("SELECT price FROM products WHERE id = :productId")
    LiveData<Double> getProductPrice(int productId);

    @Query("SELECT * FROM sales ORDER BY date DESC")
    LiveData<List<Sale>> getAllSales();
}