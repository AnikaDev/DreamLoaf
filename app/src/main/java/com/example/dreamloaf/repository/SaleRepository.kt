package com.example.dreamloaf.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.data.SaleDao
import java.util.concurrent.Executors

class SaleRepository(application: Application) {
    private val database: AppDatabase = AppDatabase.getInstance(application)
    private val saleDao: SaleDao = database.saleDao()

    fun addSale(productId: Int, quantity: Int, date: String) {
        Executors.newSingleThreadExecutor().execute {
            val sale = Sale().apply {
                this.productId = productId
                this.quantity = quantity
                this.date = date
                this.userId = 1
            }
            saleDao.insert(sale)
        }
    }

    fun addSale(sale: Sale) {
        Executors.newSingleThreadExecutor().execute {
            saleDao.insert(sale)
        }
    }

    fun getSalesByDate(date: String): LiveData<List<Sale>> {
        return saleDao.getSalesByDate(date)
    }

    fun getAllSales(): LiveData<List<Sale>> {
        return saleDao.getAllSales()
    }

    fun getMonthlySales(): LiveData<Map<String, Int>> {
        return saleDao.getMonthlySales()
    }

    fun getProductPrice(productId: Int): LiveData<Double> {
        return saleDao.getProductPrice(productId)
    }

    fun saveSale(productId: Int, quantity: Int, date: String) {
        Executors.newSingleThreadExecutor().execute {
            val existing = saleDao.getSaleForProductAndDate(productId, date)
            if (existing != null) {
                existing.quantity = quantity
                saleDao.update(existing)
            } else {
                val sale = Sale().apply {
                    this.productId = productId
                    this.quantity = quantity
                    this.date = date
                    this.userId = 1
                }
                saleDao.insert(sale)
            }
        }
    }

    fun calculateProfit(sale: Sale, product: Product?): Double {
        if (product == null) return 0.0
        return (product.price - product.costPrice) * sale.quantity
    }

    fun calculateTotalProfit(sales: List<Sale>?, products: List<Product>?): Double {
        if (sales == null || products == null) return 0.0

        return sales.sumOf { sale ->
            findProductById(products, sale.productId)?.let { product ->
                calculateProfit(sale, product)
            } ?: 0.0
        }
    }

    private fun findProductById(products: List<Product>?, productId: Int): Product? {
        return products?.find { it.id == productId }
    }
} 