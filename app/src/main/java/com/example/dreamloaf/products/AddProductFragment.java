package com.example.dreamloaf.products;

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
import com.example.dreamloaf.databinding.FragmentAddProductBinding;
import com.example.dreamloaf.viewmodels.ProductViewModel;

public class AddProductFragment extends Fragment {
    private FragmentAddProductBinding binding;
    private ProductViewModel productViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        binding.saveButton.setOnClickListener(v -> {
            String name = binding.nameEditText.getText().toString().trim();
            String weightStr = binding.weightEditText.getText().toString().trim();
            String priceStr = binding.priceEditText.getText().toString().trim();
            String costPriceStr = binding.costPriceEditText.getText().toString().trim();

            if (name.isEmpty() || weightStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Product product = new Product();
                product.setName(name);
                product.setWeight(Double.parseDouble(weightStr));
                product.setPrice(Double.parseDouble(priceStr));
                double costPrice = costPriceStr.isEmpty() ? 0 : Double.parseDouble(costPriceStr);
                product.setCostPrice(costPrice);

                productViewModel.insert(product);
                Navigation.findNavController(v).navigateUp();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректный формат числа", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}