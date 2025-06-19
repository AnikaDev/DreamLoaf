package com.example.dreamloaf.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.Production
import com.example.dreamloaf.repository.ProductionRepository
import java.util.concurrent.Executors

class ProductionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductionRepository
    val allProduction: LiveData<MutableList<Production?>?>?

    init {
        repository = ProductionRepository(application)
        allProduction = repository.allProduction
    }

    fun getProductionByDate(date: String?): LiveData<MutableList<Production?>?>? {
        return repository.getProductionByDate(date)
    }

    fun addProduction(production: Production) {
        Executors.newSingleThreadExecutor().execute {
            val existing = repository.getProductionForProductAndDate(
                production.productId,
                production.date
            )
            if (existing != null) {
                existing.quantity = production.quantity
                repository.update(existing)
            } else {
                repository.insert(production)
            }
        }
    }

    fun saveProductionData(productId: Int, quantity: Int, date: String?) {
        repository.saveProductionData(productId, quantity, date)
    }

    fun updateProduction(production: Production) {
        repository.update(production)
    }

    fun deleteProduction(production: Production) {
        repository.delete(production.id)
    }
}