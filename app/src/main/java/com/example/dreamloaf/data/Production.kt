package com.example.dreamloaf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "production")
class Production {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @JvmField
    @ColumnInfo(name = "product_id")
    var productId: Int = 0

    @JvmField
    var quantity: Int = 0
    @JvmField
    var date: String? = null

    @JvmField
    @ColumnInfo(name = "user_id")
    var userId: Int = 0
}
