package com.example.dreamloaf.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.repository.SaleRepository

class SaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SaleRepository(application)

    fun addSale(productId: Int, quantity: Int, date: String) {
        repository.addSale(productId, quantity, date)
    }

    fun addSale(sale: Sale) {
        repository.addSale(sale)
    }

    fun getSalesByDate(date: String): LiveData<List<Sale>> {
        return repository.getSalesByDate(date)
    }

    fun getMonthlySales(): LiveData<Map<String, Int>> {
        return repository.getMonthlySales()
    }

    fun saveSale(productId: Int, quantity: Int, date: String) {
        repository.saveSale(productId, quantity, date)
    }

    fun getProductPrice(productId: Int): LiveData<Double> {
        return repository.getProductPrice(productId)
    }

    fun getAllSales(): LiveData<List<Sale>> {
        return repository.getAllSales()
    }
} 