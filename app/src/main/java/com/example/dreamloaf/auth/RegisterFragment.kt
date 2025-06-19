package com.example.dreamloaf.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.dreamloaf.R
import com.example.dreamloaf.data.User
import com.example.dreamloaf.databinding.FragmentRegisterBinding
import com.example.dreamloaf.viewmodels.AuthViewModel

class RegisterFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentRegisterBinding? = null
    private var authViewModel: AuthViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel =
            ViewModelProvider(requireActivity()).get<AuthViewModel>(AuthViewModel::class.java)

        setupLoginCheck()
        setupRegisterButton()
    }

    private fun setupLoginCheck() {
        binding!!.etLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val login = s.toString().trim { it <= ' ' }
                if (!login.isEmpty()) {
                    checkLoginAvailability(login)
                }
            }
        })
    }

    private fun checkLoginAvailability(login: String?) {
        authViewModel!!.checkLoginAvailable(login)
            .observe(getViewLifecycleOwner(), Observer { isAvailable: Boolean? ->
                if (isAvailable == false) {
                    binding!!.etLogin.setError("Это имя уже занято")
                } else {
                    binding!!.etLogin.setError(null)
                }
            })
    }

    private fun setupRegisterButton() {
        binding!!.btnRegister.setOnClickListener { v: View? ->
            val login = binding!!.etLogin.getText().toString().trim { it <= ' ' }
            val password = binding!!.etPassword.getText().toString().trim { it <= ' ' }

            if (binding!!.spRole.getSelectedItem() == null) {
                Toast.makeText(requireContext(), "Выберите роль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = binding!!.spRole.getSelectedItem().toString()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User()
            user.login = login
            user.password = password
            user.role = role
            authViewModel!!.registerUser(user)
                .observe(getViewLifecycleOwner(), Observer { success: Boolean? ->
                    if (success == true) {
                        Toast.makeText(
                            requireContext(),
                            "Регистрация успешна! Теперь войдите в систему",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController(v!!).navigate(
                            R.id.action_registerFragment_to_loginFragment
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка регистрации. Имя пользователя занято",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}