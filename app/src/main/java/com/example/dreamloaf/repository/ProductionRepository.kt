package com.example.dreamloaf.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Production
import com.example.dreamloaf.data.ProductionDao
import java.util.concurrent.Executors

class ProductionRepository(application: Application?) {
    private val productionDao: ProductionDao

    init {
        val db: AppDatabase = AppDatabase.getInstance(application!!.applicationContext)!!
        this.productionDao = db.productionDao()!!
    }

    fun getProductionByDate(date: String?): LiveData<MutableList<Production?>?>? {
        return productionDao.getProductionByDate(date)
    }

    val allProduction: LiveData<MutableList<Production?>?>?
        get() = productionDao.allProduction

    fun insert(production: Production) {
        Executors.newSingleThreadExecutor().execute { productionDao.insert(production) }
    }

    fun update(production: Production) {
        Executors.newSingleThreadExecutor().execute { productionDao.update(production) }
    }

    fun delete(id: Int) {
        Executors.newSingleThreadExecutor().execute(Runnable { productionDao.delete(id) })
    }

    fun getProductionForProductAndDate(productId: Int, date: String?): Production? {
        return productionDao.getProductionForProductAndDate(productId, date)
    }

    fun deleteByProductId(productId: Int) {
        Executors.newSingleThreadExecutor()
            .execute(Runnable { productionDao.deleteByProductId(productId) })
    }

    fun saveProductionData(productId: Int, quantity: Int, date: String?) {
        Executors.newSingleThreadExecutor().execute {
            val existing = productionDao.getProductionForProductAndDate(productId, date)
            if (existing != null) {
                existing.quantity = quantity
                productionDao.update(existing)
            } else {
                val production = Production()
                production.productId = productId
                production.quantity = quantity
                production.date = date
                production.userId = 1
                productionDao.insert(production)
            }
        }
    }
}