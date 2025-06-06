package com.example.dreamloaf.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.dreamloaf.data.Sale;
import com.example.dreamloaf.repository.SaleRepository;
import java.util.List;
import java.util.Map;

public class SaleViewModel extends AndroidViewModel {
    private final SaleRepository repository;

    public SaleViewModel(@NonNull Application application) {
        super(application);
        repository = new SaleRepository(application);
    }

    public void addSale(int productId, int quantity, String date) {
        repository.addSale(productId, quantity, date);
    }

    public void addSale(Sale sale) {
        repository.addSale(sale);
    }

    public LiveData<List<Sale>> getSalesByDate(String date) {
        return repository.getSalesByDate(date);
    }

    public LiveData<Map<String, Integer>> getMonthlySales() {
        return repository.getMonthlySales();
    }

    public void saveSale(int productId, int quantity, String date) {
        repository.saveSale(productId, quantity, date);
    }

    public LiveData<Double> getProductPrice(int productId) {
        return repository.getProductPrice(productId);
    }

    public LiveData<List<Sale>> getAllSales() {
        return repository.getAllSales();
    }
}