package com.example.dreamloaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductionDao {
    @Insert
    void insert(Production production);

    @Update
    void update(Production production);

    @Query("DELETE FROM production WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM production WHERE date = :date")
    LiveData<List<Production>> getProductionByDate(String date);

    @Query("SELECT * FROM production WHERE product_id = :productId AND date = :date LIMIT 1")
    Production getProductionForProductAndDate(int productId, String date);

    @Query("DELETE FROM production WHERE product_id = :productId")
    void deleteByProductId(int productId);

    @Query("SELECT * FROM production ORDER BY date DESC")
    LiveData<List<Production>> getAllProduction();
}
