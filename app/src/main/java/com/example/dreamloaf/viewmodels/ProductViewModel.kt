package com.example.dreamloaf.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.repository.ProductRepository
import java.util.concurrent.Executors

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepo: ProductRepository
    val allProducts: LiveData<MutableList<Product?>?>?

    init {
        productRepo = ProductRepository(application)
        allProducts = productRepo.allProducts
    }

    fun insert(product: Product) {
        productRepo.insertProduct(product)
    }

    fun getProductById(productId: Int): LiveData<Product?>? {
        return productRepo.getProductById(productId)
    }

    fun delete(productId: Int) {
        Executors.newSingleThreadExecutor().execute {
            val db = AppDatabase.getInstance(getApplication<Application>().applicationContext)!!
            db.productionDao()!!.deleteByProductId(productId)
            db.saleDao()!!.deleteByProductId(productId)
            productRepo.deleteProduct(productId)
        }
    }

    fun update(product: Product) {
        Executors.newSingleThreadExecutor().execute {
            val db = AppDatabase.getInstance(getApplication<Application>().applicationContext)!!
            db.productDao()!!.update(product)
        }
    }
}