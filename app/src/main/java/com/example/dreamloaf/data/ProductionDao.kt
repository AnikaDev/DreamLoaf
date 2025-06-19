package com.example.dreamloaf.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductionDao {
    @Insert
    fun insert(production: Production)

    @Update
    fun update(production: Production)

    @Query("DELETE FROM production WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM production WHERE date = :date")
    fun getProductionByDate(date: String?): LiveData<MutableList<Production?>?>?

    @Query("SELECT * FROM production WHERE product_id = :productId AND date = :date LIMIT 1")
    fun getProductionForProductAndDate(productId: Int, date: String?): Production?

    @Query("DELETE FROM production WHERE product_id = :productId")
    fun deleteByProductId(productId: Int)

    @get:Query("SELECT * FROM production ORDER BY date DESC")
    val allProduction: LiveData<MutableList<Production?>?>?
}
