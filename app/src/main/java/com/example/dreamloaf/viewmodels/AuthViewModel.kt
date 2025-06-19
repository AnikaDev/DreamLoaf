package com.example.dreamloaf.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dreamloaf.data.User
import com.example.dreamloaf.repository.AuthRepository
import java.util.concurrent.Executors

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo: AuthRepository
    private val currentUserRole: MutableLiveData<String?> = MutableLiveData<String?>()

    init {
        authRepo = AuthRepository(application)
    }

    fun checkLoginAvailable(login: String?): LiveData<Boolean?> {
        val result: MutableLiveData<Boolean?> = MutableLiveData<Boolean?>()
        Executors.newSingleThreadExecutor().execute(Runnable {
            result.postValue(!authRepo.isLoginExists(login))
        })
        return result
    }

    fun registerUser(user: User): LiveData<Boolean?> {
        val result: MutableLiveData<Boolean?> = MutableLiveData<Boolean?>()
        Executors.newSingleThreadExecutor().execute(Runnable {
            if (authRepo.isLoginExists(user.login)) {
                result.postValue(false)
            } else {
                authRepo.register(user)
                currentUserRole.postValue(user.role)
                result.postValue(true)
            }
        })
        return result
    }

    fun loginUser(login: String?, password: String?): LiveData<User?> {
        val result: MutableLiveData<User?> = MutableLiveData<User?>()
        Executors.newSingleThreadExecutor().execute(Runnable {
            val user = authRepo.login(login, password)
            result.postValue(user)
        })
        return result
    }
}
