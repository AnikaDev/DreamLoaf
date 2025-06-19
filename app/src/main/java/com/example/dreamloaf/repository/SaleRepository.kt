package com.example.dreamloaf.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dreamloaf.data.AppDatabase
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.data.SaleDao
import java.util.concurrent.Executors

class SaleRepository(application: Application?) {
    private val saleDao: SaleDao
    private val database: AppDatabase

    init {
        database = AppDatabase.getInstance(application!!.applicationContext)!!
        saleDao = database.saleDao()!!
    }

    fun addSale(productId: Int, quantity: Int, date: String?) {
        Executors.newSingleThreadExecutor().execute {
            val sale = Sale()
            sale.productId = productId
            sale.quantity = quantity
            sale.date = date
            sale.userId = 1
            saleDao.insert(sale)
        }
    }

    fun addSale(sale: Sale) {
        Executors.newSingleThreadExecutor().execute { saleDao.insert(sale) }
    }

    fun getSalesByDate(date: String?): LiveData<MutableList<Sale?>?>? {
        return saleDao.getSalesByDate(date)
    }

    val allSales: LiveData<MutableList<Sale?>?>?
        get() = saleDao.allSales

    val monthlySales: LiveData<MutableMap<String?, Int?>?>?
        get() = saleDao.getMonthlySales()

    fun getProductPrice(productId: Int): LiveData<Double?>? {
        return saleDao.getProductPrice(productId)
    }

    fun saveSale(productId: Int, quantity: Int, date: String?) {
        Executors.newSingleThreadExecutor().execute {
            val existing = saleDao.getSaleForProductAndDate(productId, date)
            if (existing != null) {
                existing.quantity = quantity
                saleDao.update(existing)
            } else {
                val sale = Sale()
                sale.productId = productId
                sale.quantity = quantity
                sale.date = date
                sale.userId = 1
                saleDao.insert(sale)
            }
        }
    }

    fun calculateProfit(sale: Sale, product: Product?): Double {
        if (product == null) return 0.0
        return (product.price - product.costPrice) * sale.quantity
    }

    fun calculateTotalProfit(sales: MutableList<Sale>?, products: MutableList<Product>?): Double {
        if (sales == null || products == null) return 0.0

        var totalProfit = 0.0
        for (sale in sales) {
            val product = findProductById(products, sale.productId)
            if (product != null) {
                totalProfit += calculateProfit(sale, product)
            }
        }
        return totalProfit
    }

    private fun findProductById(products: MutableList<Product>?, productId: Int): Product? {
        if (products == null) return null
        for (product in products) {
            if (product.id.toInt() == productId) {
                return product
            }
        }
        return null
    }
}