package com.example.dreamloaf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production")
data class Production(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "product_id")
    var productId: Int = 0,

    var quantity: Int = 0,
    var date: String = "",

    @ColumnInfo(name = "user_id")
    var userId: Int = 0
) 