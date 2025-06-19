package com.example.dreamloaf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
class Product {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @JvmField
    var name: String? = null

    @JvmField
    var weight: Double = 0.0

    @JvmField
    var price: Double = 0.0

    @JvmField
    var costPrice: Double = 0.0
}