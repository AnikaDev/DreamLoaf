package com.example.dreamloaf.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dreamloaf.data.AppDatabase;
import com.example.dreamloaf.data.Production;
import com.example.dreamloaf.data.ProductionDao;

import java.util.List;
import java.util.concurrent.Executors;

public class ProductionRepository {
    private final ProductionDao productionDao;

    public ProductionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.productionDao = db.productionDao();
    }

    public LiveData<List<Production>> getProductionByDate(String date) {
        return productionDao.getProductionByDate(date);
    }

    public LiveData<List<Production>> getAllProduction() {
        return productionDao.getAllProduction();
    }

    public void insert(Production production) {
        Executors.newSingleThreadExecutor().execute(() -> productionDao.insert(production));
    }

    public void update(Production production) {
        Executors.newSingleThreadExecutor().execute(() -> productionDao.update(production));
    }

    public void delete(int id) {
        Executors.newSingleThreadExecutor().execute(() -> productionDao.delete(id));
    }

    public Production getProductionForProductAndDate(int productId, String date) {
        return productionDao.getProductionForProductAndDate(productId, date);
    }

    public void deleteByProductId(int productId) {
        Executors.newSingleThreadExecutor().execute(() -> productionDao.deleteByProductId(productId));
    }

    public void saveProductionData(int productId, int quantity, String date) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Production existing = productionDao.getProductionForProductAndDate(productId, date);

            if (existing != null) {
                existing.setQuantity(quantity);
                productionDao.update(existing);
            } else {
                Production production = new Production();
                production.setProductId(productId);
                production.setQuantity(quantity);
                production.setDate(date);
                production.setUserId(1);
                productionDao.insert(production);
            }
        });
    }
}