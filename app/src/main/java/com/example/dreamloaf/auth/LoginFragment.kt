package com.example.dreamloaf.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.dreamloaf.R
import com.example.dreamloaf.databinding.FragmentLoginBinding
import com.example.dreamloaf.viewmodels.AuthViewModel

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        setupLoginButton(view)
        setupRegisterButton()
    }

    private fun setupLoginButton(rootView: View) {
        binding?.loginButton?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
                    v.startAnimation(scaleDown)
                    v.alpha = 0.9f
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.clearAnimation()
                    v.alpha = 1f
                }
            }
            false
        }

        binding?.loginButton?.setOnClickListener {
            val login = binding?.emailEditText?.text?.toString()?.trim() ?: ""
            val password = binding?.passwordEditText?.text?.toString()?.trim() ?: ""

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding?.progressBar?.visibility = View.VISIBLE
            binding?.loginButton?.isEnabled = false

            authViewModel.loginUser(login, password).observe(viewLifecycleOwner) { user ->
                binding?.progressBar?.visibility = View.GONE
                binding?.loginButton?.isEnabled = true

                if (user != null) {
                    val args = Bundle().apply {
                        putString("user_role", user.role)
                    }
                    Navigation.findNavController(rootView).navigate(R.id.toMainMenu, args)
                } else {
                    Toast.makeText(requireContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRegisterButton() {
        binding?.registerButton?.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.toRegisterFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
} 