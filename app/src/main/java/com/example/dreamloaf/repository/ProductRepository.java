package com.example.dreamloaf.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.dreamloaf.data.AppDatabase;
import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.data.ProductDao;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductRepository {
    private final ProductDao productDao;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
    }

    public void insertProduct(Product product) {
        Executors.newSingleThreadExecutor().execute(() -> productDao.insert(product));
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<Product> getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    public void deleteProduct(int productId) {
        Executors.newSingleThreadExecutor().execute(() -> productDao.delete(productId));
    }
}