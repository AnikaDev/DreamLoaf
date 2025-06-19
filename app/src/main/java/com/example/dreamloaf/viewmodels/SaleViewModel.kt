package com.example.dreamloaf.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.repository.SaleRepository

class SaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SaleRepository
    val allSales: LiveData<MutableList<Sale?>?>?
    val monthlySales: LiveData<MutableMap<String?, Int?>?>?

    init {
        repository = SaleRepository(application)
        allSales = repository.allSales
        monthlySales = repository.monthlySales
    }

    fun addSale(sale: Sale) {
        repository.addSale(sale)
    }

    fun getSalesByDate(date: String?): LiveData<MutableList<Sale?>?>? {
        return repository.getSalesByDate(date)
    }

    fun saveSale(productId: Int, quantity: Int, date: String?) {
        repository.saveSale(productId, quantity, date)
    }

    fun getProductPrice(productId: Int): LiveData<Double?>? {
        return repository.getProductPrice(productId)
    }
}