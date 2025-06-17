package com.example.dreamloaf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var weight: Double = 0.0,
    var price: Double = 0.0,
    var costPrice: Double = 0.0
) 