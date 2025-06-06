package com.example.dreamloaf.sales;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.databinding.FragmentAddSaleBinding;
import com.example.dreamloaf.viewmodels.ProductViewModel;
import com.example.dreamloaf.viewmodels.SaleViewModel;

import java.time.LocalDate;

public class AddSaleFragment extends Fragment {
    private FragmentAddSaleBinding binding;
    private SaleViewModel saleViewModel;
    private ProductViewModel productViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddSaleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            ArrayAdapter<Product> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    products
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.productSpinner.setAdapter(adapter);
        });

        binding.saveButton.setOnClickListener(v -> {
            if (binding.productSpinner.getSelectedItem() == null ||
                    binding.quantityEditText.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Product selectedProduct = (Product) binding.productSpinner.getSelectedItem();
                int quantity = Integer.parseInt(binding.quantityEditText.getText().toString());
                String currentDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentDate = LocalDate.now().toString();
                }
                saleViewModel.addSale(selectedProduct.getId(), quantity, currentDate);
                Navigation.findNavController(v).navigateUp();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректное количество", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

