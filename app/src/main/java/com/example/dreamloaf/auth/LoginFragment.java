package com.example.dreamloaf.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.dreamloaf.R;
import com.example.dreamloaf.databinding.FragmentLoginBinding;
import com.example.dreamloaf.viewmodels.AuthViewModel;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupLoginButton(view);
        setupRegisterButton();
    }

    private void setupLoginButton(View rootView) {
        binding.loginButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Animation scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale);
                    v.startAnimation(scaleDown);
                    v.setAlpha(0.9f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.clearAnimation();
                    v.setAlpha(1f);
                    break;
            }
            return false;
        });

        binding.loginButton.setOnClickListener(v -> {
            String login = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.loginButton.setEnabled(false);

            authViewModel.loginUser(login, password).observe(getViewLifecycleOwner(), user -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.loginButton.setEnabled(true);

                if (user != null) {
                    Bundle args = new Bundle();
                    args.putString("user_role", user.getRole());
                    Navigation.findNavController(rootView).navigate(R.id.toMainMenu, args);
                } else {
                    Toast.makeText(requireContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupRegisterButton() {
        binding.registerButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.toRegisterFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}