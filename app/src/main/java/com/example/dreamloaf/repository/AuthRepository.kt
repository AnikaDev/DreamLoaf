package com.example.dreamloaf.repository

import android.app.Application
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.User
import com.example.dreamloaf.data.UserDao
import java.util.concurrent.Executors

class AuthRepository(application: Application) {
    private val userDao: UserDao = AppDatabase.getInstance(application).userDao()

    fun isLoginExists(login: String): Boolean {
        return userDao.getUserByLogin(login) != null
    }

    fun register(user: User) {
        Executors.newSingleThreadExecutor().execute {
            userDao.insert(user)
        }
    }

    fun login(login: String, password: String): User? {
        return userDao.getUser(login, password)
    }
} 