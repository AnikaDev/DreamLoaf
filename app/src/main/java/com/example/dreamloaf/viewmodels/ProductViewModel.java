package com.example.dreamloaf.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.dreamloaf.data.AppDatabase;
import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.repository.ProductRepository;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository productRepo;
    private final LiveData<List<Product>> allProducts;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepo = new ProductRepository(application);
        allProducts = productRepo.getAllProducts();
    }

    public void insert(Product product) {
        productRepo.insertProduct(product);
    }

    public LiveData<Product> getProductById(int productId) {
        return productRepo.getProductById(productId);
    }

    public void delete(int productId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplication());
            db.productionDao().deleteByProductId(productId);
            db.saleDao().deleteByProductId(productId);
            productRepo.deleteProduct(productId);
        });
    }

    public LiveData<List<Product>> getAllProducts() {
        return productRepo.getAllProducts();
    }
}