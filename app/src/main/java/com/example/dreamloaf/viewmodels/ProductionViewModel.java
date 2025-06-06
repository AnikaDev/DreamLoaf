package com.example.dreamloaf.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dreamloaf.data.Production;
import com.example.dreamloaf.repository.ProductionRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class ProductionViewModel extends AndroidViewModel {
    private final ProductionRepository repository;
    private final LiveData<List<Production>> allProduction;

    public ProductionViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductionRepository(application);
        allProduction = repository.getAllProduction();
    }

    public LiveData<List<Production>> getProductionByDate(String date) {
        return repository.getProductionByDate(date);
    }

    public void addProduction(Production production) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Production existing = repository.getProductionForProductAndDate(
                    production.getProductId(),
                    production.getDate()
            );

            if (existing != null) {
                existing.setQuantity(production.getQuantity());
                repository.update(existing);
            } else {
                repository.insert(production);
            }
        });
    }

    public void saveProductionData(int productId, int quantity, String date) {
        repository.saveProductionData(productId, quantity, date);
    }

    public void updateProduction(Production production) {
        repository.update(production);
    }

    public void deleteProduction(Production production) {
        repository.delete(production.getId());
    }

    public LiveData<List<Production>> getAllProduction() {
        return allProduction;
    }
}