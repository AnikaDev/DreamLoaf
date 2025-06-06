package com.example.dreamloaf.production;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.data.Production;
import com.example.dreamloaf.databinding.FragmentAddProductionBinding;
import com.example.dreamloaf.viewmodels.ProductionViewModel;

import java.time.LocalDate;

public class AddProductionFragment extends Fragment {
    private FragmentAddProductionBinding binding;
    private ProductionViewModel productionViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProductionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productionViewModel = new ViewModelProvider(this).get(ProductionViewModel.class);

        binding.saveButton.setOnClickListener(v -> {
            if (binding.productSpinner.getSelectedItem() == null ||
                    binding.quantityEditText.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int productId = ((Product) binding.productSpinner.getSelectedItem()).getId();
                int quantity = Integer.parseInt(binding.quantityEditText.getText().toString());

                Production production = new Production();
                production.setProductId(productId);
                production.setQuantity(quantity);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    production.setDate(LocalDate.now().toString());
                }
                production.setUserId(1);

                productionViewModel.addProduction(production);
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
