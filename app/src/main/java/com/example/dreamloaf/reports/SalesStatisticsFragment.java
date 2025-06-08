package com.example.dreamloaf.reports;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.data.Sale;
import com.example.dreamloaf.databinding.FragmentSalesStatisticsBinding;
import com.example.dreamloaf.viewmodels.ProductViewModel;
import com.example.dreamloaf.viewmodels.SaleViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesStatisticsFragment extends Fragment {
    private FragmentSalesStatisticsBinding binding;
    private SaleViewModel saleViewModel;
    private ProductViewModel productViewModel;
    private final int[] colors = {
            Color.parseColor("#FF6200EE"),
            Color.parseColor("#FF03DAC5"),
            Color.parseColor("#FF018786"),
            Color.parseColor("#FFBB86FC"),
            Color.parseColor("#FF3700B3"),
            Color.parseColor("#FFFF5722"),
            Color.parseColor("#FFE91E63"),
            Color.parseColor("#FF8BC34A"),
            Color.parseColor("#FF795548"),
            Color.parseColor("#FF9C27B0"),
            Color.parseColor("#FF3F51B5"),
            Color.parseColor("#FF00BCD4"),
            Color.parseColor("#FFCDDC39"),
            Color.parseColor("#FFFF9800"),
            Color.parseColor("#FF607D8B")
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSalesStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saleViewModel = new ViewModelProvider(requireActivity()).get(SaleViewModel.class);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        loadData();
    }


    private void loadData() {
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                saleViewModel.getAllSales().observe(getViewLifecycleOwner(), sales -> {
                    if (sales != null && !sales.isEmpty()) {
                        Map<String, Map<String, Integer>> lineData = prepareLineChartData(sales, products);
                        Map<String, Integer> pieData = preparePieChartData(sales, products);
                        binding.lineChartView.setPieData(pieData);
                        binding.lineChartView.setLineData(lineData);
                        setupCombinedLegend(lineData.keySet());
                    } else {
                        showEmptyState();
                    }
                });
            } else {
                showEmptyState();
            }
        });
    }

    private void setupCombinedLegend(Iterable<String> productNames) {
        LinearLayout legendContainer = binding.legendContainer;
        legendContainer.removeAllViews();

        Context context = requireContext();
        int colorIndex = 0;
        TextView legendTitle = new TextView(context);
        legendTitle.setText("Легенда:");
        legendTitle.setTextSize(16);
        legendTitle.setTextColor(Color.BLACK);
        legendTitle.setTypeface(null, Typeface.BOLD);
        legendContainer.addView(legendTitle);
        List<String> productList = new ArrayList<>();
        for (String productName : productNames) {
            productList.add(productName);
        }

        for (int i = 0; i < productList.size(); i++) {
            String productName = productList.get(i);
            LinearLayout legendItem = new LinearLayout(context);
            legendItem.setOrientation(LinearLayout.HORIZONTAL);
            legendItem.setGravity(Gravity.CENTER_VERTICAL);
            legendItem.setPadding(0, dpToPx(8), 0, 0);

            View colorLine = new View(context);
            colorLine.setBackgroundColor(colors[i % colors.length]);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    dpToPx(40), dpToPx(3));
            lineParams.setMargins(0, 0, dpToPx(12), 0);
            colorLine.setLayoutParams(lineParams);

            TextView productText = new TextView(context);
            productText.setText(productName);
            productText.setTextSize(14);
            productText.setTextColor(Color.BLACK);

            legendItem.addView(colorLine);
            legendItem.addView(productText);
            legendContainer.addView(legendItem);
        }
    }


    private Map<String, Integer> preparePieChartData(List<Sale> sales, List<Product> products) {
        Map<String, Integer> result = new HashMap<>();
        for (Sale sale : sales) {
            String productName = getProductName(products, sale.getProductId());
            if (productName != null) {
                result.put(productName, result.getOrDefault(productName, 0) + sale.getQuantity());
            }
        }
        return result;
    }

    private Map<String, Map<String, Integer>> prepareLineChartData(List<Sale> sales, List<Product> products) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (Product product : products) {
            result.put(product.getName(), new HashMap<>());
        }
        for (Sale sale : sales) {
            String productName = getProductName(products, sale.getProductId());
            if (productName != null) {
                String date = formatDateForChart(sale.getDate());
                result.get(productName).put(date,
                        result.get(productName).getOrDefault(date, 0) + sale.getQuantity());
            }
        }
        return result;
    }
    private String formatDateForChart(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            if (parts.length >= 3) {
                return parts[2] + "." + parts[1];
            }
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void showEmptyState() {
        binding.chartContainer.setVisibility(View.GONE);
        TextView emptyView = new TextView(requireContext());
        emptyView.setText("Нет данных для отображения");
        emptyView.setTextSize(18);
        emptyView.setGravity(Gravity.CENTER);
        binding.getRoot().addView(emptyView);
    }

    private String getProductName(List<Product> products, int productId) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product.getName();
            }
        }
        return null;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
