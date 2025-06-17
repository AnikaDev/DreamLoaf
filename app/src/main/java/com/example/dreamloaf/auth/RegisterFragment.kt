package com.example.dreamloaf.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.dreamloaf.R
import com.example.dreamloaf.data.User
import com.example.dreamloaf.databinding.FragmentRegisterBinding
import com.example.dreamloaf.viewmodels.AuthViewModel

class RegisterFragment : Fragment() {
    private var binding: FragmentRegisterBinding? = null
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        setupLoginCheck()
        setupRegisterButton()
    }

    private fun setupLoginCheck() {
        binding?.etLogin?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val login = s?.toString()?.trim() ?: ""
                if (login.isNotEmpty()) {
                    checkLoginAvailability(login)
                }
            }
        })
    }

    private fun checkLoginAvailability(login: String) {
        authViewModel.checkLoginAvailable(login).observe(viewLifecycleOwner) { isAvailable ->
            binding?.etLogin?.error = if (!isAvailable) "Это имя уже занято" else null
        }
    }

    private fun setupRegisterButton() {
        binding?.btnRegister?.setOnClickListener { view ->
            val login = binding?.etLogin?.text?.toString()?.trim() ?: ""
            val password = binding?.etPassword?.text?.toString()?.trim() ?: ""
            val selectedRole = binding?.spRole?.selectedItem?.toString()

            if (selectedRole == null) {
                Toast.makeText(requireContext(), "Выберите роль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User().apply {
                this.login = login
                this.password = password
                this.role = selectedRole
            }

            authViewModel.registerUser(user).observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Регистрация успешна! Теперь войдите в систему",
                        Toast.LENGTH_SHORT
                    ).show()
                    Navigation.findNavController(view).navigate(
                        R.id.action_registerFragment_to_loginFragment
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка регистрации. Имя пользователя занято",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
} 