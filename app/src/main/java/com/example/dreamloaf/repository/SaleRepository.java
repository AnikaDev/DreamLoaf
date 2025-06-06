package com.example.dreamloaf.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.dreamloaf.data.AppDatabase;
import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.data.Sale;
import com.example.dreamloaf.data.SaleDao;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class SaleRepository {
    private final SaleDao saleDao;
    private final AppDatabase database;

    public SaleRepository(Application application) {
        database = AppDatabase.getInstance(application);
        saleDao = database.saleDao();
    }

    public void addSale(int productId, int quantity, String date) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Sale sale = new Sale();
            sale.setProductId(productId);
            sale.setQuantity(quantity);
            sale.setDate(date);
            sale.setUserId(1);
            saleDao.insert(sale);
        });
    }

    public void addSale(Sale sale) {
        Executors.newSingleThreadExecutor().execute(() -> saleDao.insert(sale));
    }

    public LiveData<List<Sale>> getSalesByDate(String date) {
        return saleDao.getSalesByDate(date);
    }

    public LiveData<List<Sale>> getAllSales() {
        return saleDao.getAllSales();
    }

    public LiveData<Map<String, Integer>> getMonthlySales() {
        return saleDao.getMonthlySales();
    }

    public LiveData<Double> getProductPrice(int productId) {
        return saleDao.getProductPrice(productId);
    }

    public void saveSale(int productId, int quantity, String date) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Sale existing = saleDao.getSaleForProductAndDate(productId, date);
            if (existing != null) {
                existing.setQuantity(quantity);
                saleDao.update(existing);
            } else {
                Sale sale = new Sale();
                sale.setProductId(productId);
                sale.setQuantity(quantity);
                sale.setDate(date);
                sale.setUserId(1);
                saleDao.insert(sale);
            }
        });
    }

    public double calculateProfit(Sale sale, Product product) {
        if (product == null) return 0;
        return (product.getPrice() - product.getCostPrice()) * sale.getQuantity();
    }

    public double calculateTotalProfit(List<Sale> sales, List<Product> products) {
        if (sales == null || products == null) return 0;

        double totalProfit = 0;
        for (Sale sale : sales) {
            Product product = findProductById(products, sale.getProductId());
            if (product != null) {
                totalProfit += calculateProfit(sale, product);
            }
        }
        return totalProfit;
    }

    private Product findProductById(List<Product> products, int productId) {
        if (products == null) return null;
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
}