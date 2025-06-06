package com.example.dreamloaf.production;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.dreamloaf.adapter.ProductionAdapter;
import com.example.dreamloaf.databinding.FragmentProductionLogBinding;
import com.example.dreamloaf.viewmodels.ProductViewModel;
import com.example.dreamloaf.viewmodels.ProductionViewModel;
import com.example.dreamloaf.viewmodels.SaleViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductionLogFragment extends Fragment {

    private FragmentProductionLogBinding binding;
    private ProductViewModel productViewModel;
    private ProductionViewModel productionViewModel;
    private SaleViewModel saleViewModel;
    private ProductionAdapter adapter;
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductionLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedDate = getArguments() != null ?
                getArguments().getString("selected_date", getCurrentDate()) :
                getCurrentDate();

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        productionViewModel = new ViewModelProvider(this).get(ProductionViewModel.class);
        saleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);
        adapter = new ProductionAdapter(productionViewModel, saleViewModel, selectedDate);
        adapter.setEditMode(true);
        binding.productionRecyclerView.setAdapter(adapter);
        binding.productionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.tvDateTitle.setText("Производство на " + selectedDate);

        binding.btnSaveAll.setOnClickListener(v -> {
            adapter.saveAllData();
            Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
        });

        loadDataForDate(selectedDate);
    }

    private String getCurrentDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            return java.text.DateFormat.getDateInstance().format(new java.util.Date());
        }
    }

    private void loadDataForDate(String date) {
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.productionRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.productionRecyclerView.setVisibility(View.VISIBLE);

                productionViewModel.getProductionByDate(date).observe(getViewLifecycleOwner(), productions -> {
                    saleViewModel.getSalesByDate(date).observe(getViewLifecycleOwner(), sales -> {
                        adapter.updateProductionsAndSales(productions, sales);
                        adapter.submitList(products);
                    });
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}