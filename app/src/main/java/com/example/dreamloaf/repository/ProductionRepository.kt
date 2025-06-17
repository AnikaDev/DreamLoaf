package com.example.dreamloaf.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Production
import com.example.dreamloaf.data.ProductionDao
import java.util.concurrent.Executors

class ProductionRepository(application: Application) {
    private val productionDao: ProductionDao = AppDatabase.getInstance(application).productionDao()

    fun getProductionByDate(date: String): LiveData<List<Production>> {
        return productionDao.getProductionByDate(date)
    }

    fun getAllProduction(): LiveData<List<Production>> {
        return productionDao.getAllProduction()
    }

    fun insert(production: Production) {
        Executors.newSingleThreadExecutor().execute {
            productionDao.insert(production)
        }
    }

    fun update(production: Production) {
        Executors.newSingleThreadExecutor().execute {
            productionDao.update(production)
        }
    }

    fun delete(id: Int) {
        Executors.newSingleThreadExecutor().execute {
            productionDao.delete(id)
        }
    }

    fun getProductionForProductAndDate(productId: Int, date: String): Production? {
        return productionDao.getProductionForProductAndDate(productId, date)
    }

    fun deleteByProductId(productId: Int) {
        Executors.newSingleThreadExecutor().execute {
            productionDao.deleteByProductId(productId)
        }
    }

    fun saveProductionData(productId: Int, quantity: Int, date: String) {
        Executors.newSingleThreadExecutor().execute {
            val existing = productionDao.getProductionForProductAndDate(productId, date)

            if (existing != null) {
                existing.quantity = quantity
                productionDao.update(existing)
            } else {
                val production = Production().apply {
                    this.productId = productId
                    this.quantity = quantity
                    this.date = date
                    this.userId = 1
                }
                productionDao.insert(production)
            }
        }
    }
} 