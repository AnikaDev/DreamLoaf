package com.example.dreamloaf.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.dreamloaf.data.User;
import com.example.dreamloaf.databinding.FragmentRegisterBinding;
import com.example.dreamloaf.viewmodels.AuthViewModel;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupLoginCheck();
        setupRegisterButton();
    }

    private void setupLoginCheck() {
        binding.etLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String login = s.toString().trim();
                if (!login.isEmpty()) {
                    checkLoginAvailability(login);
                }
            }
        });
    }

    private void checkLoginAvailability(String login) {
        authViewModel.checkLoginAvailable(login).observe(getViewLifecycleOwner(), isAvailable -> {
            if (!isAvailable) {
                binding.etLogin.setError("Это имя уже занято");
            } else {
                binding.etLogin.setError(null);
            }
        });
    }

    private void setupRegisterButton() {
        binding.btnRegister.setOnClickListener(v -> {
            String login = binding.etLogin.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (binding.spRole.getSelectedItem() == null) {
                Toast.makeText(requireContext(), "Выберите роль", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = binding.spRole.getSelectedItem().toString();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(role);

            authViewModel.registerUser(user).observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    Toast.makeText(requireContext(),
                            "Регистрация успешна! Теперь войдите в систему",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(
                            R.id.action_registerFragment_to_loginFragment);
                } else {
                    Toast.makeText(requireContext(),
                            "Ошибка регистрации. Имя пользователя занято",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}