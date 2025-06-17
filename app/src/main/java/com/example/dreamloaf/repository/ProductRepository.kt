package com.example.dreamloaf.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.ProductDao
import java.util.concurrent.Executors

class ProductRepository(application: Application) {
    private val productDao: ProductDao = AppDatabase.getInstance(application).productDao()

    fun insertProduct(product: Product) {
        Executors.newSingleThreadExecutor().execute {
            productDao.insert(product)
        }
    }

    fun getAllProducts(): LiveData<List<Product>> {
        return productDao.getAllProducts()
    }

    fun getProductById(productId: Int): LiveData<Product> {
        return productDao.getProductById(productId)
    }

    fun deleteProduct(productId: Int) {
        Executors.newSingleThreadExecutor().execute {
            productDao.delete(productId)
        }
    }
} 