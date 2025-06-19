package com.example.dreamloaf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @JvmField
    @ColumnInfo(name = "login")
    var login: String? = null

    @JvmField
    @ColumnInfo(name = "password")
    var password: String? = null

    @JvmField
    @ColumnInfo(name = "role")
    var role: String? = null
}
