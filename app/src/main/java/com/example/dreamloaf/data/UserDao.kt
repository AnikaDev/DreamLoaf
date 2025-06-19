package com.example.dreamloaf.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    fun getUser(login: String?, password: String?): User?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String?): MutableList<User?>?

    @get:Query("SELECT * FROM users")
    val allUsers: LiveData<MutableList<User?>?>?

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    fun getUserByLogin(login: String?): User?
}
