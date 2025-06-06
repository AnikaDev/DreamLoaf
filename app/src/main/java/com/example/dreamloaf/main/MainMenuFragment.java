package com.example.dreamloaf.main;

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
import com.example.dreamloaf.R;
import com.example.dreamloaf.databinding.FragmentMainMenuBinding;
import com.example.dreamloaf.viewmodels.AuthViewModel;

public class MainMenuFragment extends Fragment {
    private FragmentMainMenuBinding binding;
    private AuthViewModel authViewModel;
    private String currentUserRole;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            currentUserRole = getArguments().getString("user_role", "");
        }

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        setupNavigation();
    }

    private void setupNavigation() {
        binding.btnProducts.setOnClickListener(v -> {
            if ("Менеджер".equals(currentUserRole)) {
                showAccessDenied();
                return;
            }
            Navigation.findNavController(v).navigate(R.id.toProductList);
        });

        binding.btnProduction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.toCalendar);
        });

        binding.btnSales.setOnClickListener(v -> {
            if ("Пекарь".equals(currentUserRole)) {
                showAccessDenied();
                return;
            }
            Navigation.findNavController(v).navigate(R.id.toSalesLog);
        });

        binding.btnStatistics.setOnClickListener(v -> {
            if ("Пекарь".equals(currentUserRole)) {
                showAccessDenied();
                return;
            }
            Navigation.findNavController(v).navigate(R.id.toSalesStatistics);
        });
    }

    private void showAccessDenied() {
        Toast.makeText(requireContext(), "Доступ ограничен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}