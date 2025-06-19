package com.example.dreamloaf.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.ProductDao
import java.util.concurrent.Executors

class ProductRepository(application: Application?) {
    private val productDao: ProductDao

    init {
        val db: AppDatabase = AppDatabase.getInstance(application!!.applicationContext)!!
        productDao = db.productDao()!!
    }

    fun insertProduct(product: Product) {
        Executors.newSingleThreadExecutor().execute { productDao.insert(product) }
    }

    val allProducts: LiveData<MutableList<Product?>?>?
        get() = productDao.allProducts

    fun getProductById(productId: Int): LiveData<Product?>? {
        return productDao.getProductById(productId)
    }

    fun deleteProduct(productId: Int) {
        Executors.newSingleThreadExecutor().execute(Runnable { productDao.delete(productId) })
    }
}