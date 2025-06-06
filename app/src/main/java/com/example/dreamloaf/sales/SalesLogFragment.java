package com.example.dreamloaf.sales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.dreamloaf.R;
import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.data.Sale;
import com.example.dreamloaf.databinding.FragmentSalesLogBinding;
import com.example.dreamloaf.viewmodels.ProductViewModel;
import com.example.dreamloaf.viewmodels.SaleViewModel;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesLogFragment extends Fragment {
    private FragmentSalesLogBinding binding;
    private SaleViewModel saleViewModel;
    private ProductViewModel productViewModel;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSalesLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        loadSalesData();
    }

    private void loadSalesData() {
        saleViewModel.getAllSales().observe(getViewLifecycleOwner(), sales -> {
            if (sales == null || sales.isEmpty()) {
                showEmptyState();
                return;
            }

            productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                if (products == null || products.isEmpty()) {
                    showEmptyState();
                    return;
                }

                calculateAndDisplayResults(sales, products);
                binding.emptyState.setVisibility(View.GONE);
            });
        });
    }

    private void calculateAndDisplayResults(List<Sale> sales, List<Product> products) {
        double totalRevenue = 0;
        double totalCost = 0;
        Map<Integer, ProductSalesData> productDataMap = new HashMap<>();

        for (Sale sale : sales) {
            Product product = findProductById(products, sale.getProductId());
            if (product != null) {
                double productRevenue = sale.getQuantity() * product.getPrice();
                double productCost = sale.getQuantity() * product.getCostPrice();

                totalRevenue += productRevenue;
                totalCost += productCost;

                ProductSalesData data = productDataMap.getOrDefault(product.getId(),
                        new ProductSalesData(product.getName()));
                data.addSale(sale.getQuantity(), productRevenue, productCost);
                productDataMap.put(product.getId(), data);
            }
        }

        binding.tvTotalRevenue.setText(currencyFormat.format(totalRevenue));
        binding.tvTotalCost.setText(currencyFormat.format(totalCost));
        binding.tvTotalProfit.setText(currencyFormat.format(totalRevenue - totalCost));
        binding.layoutProductsBreakdown.removeAllViews();
        for (ProductSalesData data : productDataMap.values()) {
            addProductBreakdownRow(data);
        }
    }

    private void addProductBreakdownRow(ProductSalesData data) {
        View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_product_breakdown, binding.layoutProductsBreakdown, false);

        TextView tvProductName = itemView.findViewById(R.id.tvProductName);
        TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
        TextView tvRevenue = itemView.findViewById(R.id.tvRevenue);
        TextView tvCost = itemView.findViewById(R.id.tvCost);
        TextView tvProfit = itemView.findViewById(R.id.tvProfit);

        tvProductName.setText(data.productName);
        tvQuantity.setText(String.valueOf(data.totalQuantity));
        tvRevenue.setText(currencyFormat.format(data.totalRevenue));
        tvCost.setText(currencyFormat.format(data.totalCost));
        tvProfit.setText(currencyFormat.format(data.totalRevenue - data.totalCost));

        binding.layoutProductsBreakdown.addView(itemView);
    }

    private static class ProductSalesData {
        String productName;
        int totalQuantity;
        double totalRevenue;
        double totalCost;

        ProductSalesData(String productName) {
            this.productName = productName;
        }

        void addSale(int quantity, double revenue, double cost) {
            this.totalQuantity += quantity;
            this.totalRevenue += revenue;
            this.totalCost += cost;
        }
    }

    private Product findProductById(List<Product> products, int productId) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    private void showEmptyState() {
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.salesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}