package com.example.dreamloaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :productId")
    LiveData<Product> getProductById(int productId);

    @Query("DELETE FROM products WHERE id = :productId")
    void delete(int productId);

    @Update
    void update(Product product);
}