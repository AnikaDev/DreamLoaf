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
    private boolean isEditMode = false;
    private Product currentProduct;

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

        if (getArguments() != null && getArguments().containsKey("product_id")) {
            int productId = getArguments().getInt("product_id");
            if (productId != -1) {
                isEditMode = true;
                productViewModel.getProductById(productId).observe(getViewLifecycleOwner(), product -> {
                    if (product != null) {
                        currentProduct = product;
                        populateForm(product);
                    }
                });
            }
        }

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
                Product product;
                if (isEditMode && currentProduct != null) {
                    product = currentProduct;
                } else {
                    product = new Product();
                }

                product.setName(name);
                product.setWeight(Double.parseDouble(weightStr));
                product.setPrice(Double.parseDouble(priceStr));
                product.setCostPrice(costPriceStr.isEmpty() ? 0 : Double.parseDouble(costPriceStr));

                if (isEditMode) {
                    productViewModel.update(product);
                    Toast.makeText(requireContext(), "Продукт обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    productViewModel.insert(product);
                    Toast.makeText(requireContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show();
                }

                Navigation.findNavController(v).navigateUp();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректный формат числа", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateForm(Product product) {
        binding.nameEditText.setText(product.getName());
        binding.weightEditText.setText(String.valueOf(product.getWeight()));
        binding.priceEditText.setText(String.valueOf(product.getPrice()));
        binding.costPriceEditText.setText(String.valueOf(product.getCostPrice()));
        binding.saveButton.setText("Обновить");

        if (getActivity() != null) {
            getActivity().setTitle("Редактировать продукт");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}