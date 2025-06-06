package com.example.dreamloaf.products;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.dreamloaf.R;
import com.example.dreamloaf.adapter.ProductAdapter;
import com.example.dreamloaf.databinding.FragmentProductListBinding;
import com.example.dreamloaf.viewmodels.ProductViewModel;

public class ProductListFragment extends Fragment {
    private FragmentProductListBinding binding;
    private ProductViewModel viewModel;
    private ProductAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        setupAdapter();
        setupObservers();

        binding.addProductButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.toAddProduct);
        });
    }

    private void setupAdapter() {
        adapter = new ProductAdapter();
        binding.productsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.productsRecyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(product -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удаление")
                    .setMessage("Удалить " + product.getName() + "?")
                    .setPositiveButton("Да", (d, w) -> viewModel.delete(product.getId()))
                    .setNegativeButton("Нет", null)
                    .show();
        });
    }

    private void setupObservers() {
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            if (products.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.productsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.emptyState.setVisibility(View.GONE);
                binding.productsRecyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(products);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
