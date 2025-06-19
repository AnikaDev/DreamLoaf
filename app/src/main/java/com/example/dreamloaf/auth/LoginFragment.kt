package com.example.dreamloaf.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.dreamloaf.R
import com.example.dreamloaf.data.User
import com.example.dreamloaf.databinding.FragmentLoginBinding
import com.example.dreamloaf.viewmodels.AuthViewModel

class LoginFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentLoginBinding? = null
    private var authViewModel: AuthViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel =
            ViewModelProvider(requireActivity()).get<AuthViewModel>(AuthViewModel::class.java)

        setupLoginButton(view)
        setupRegisterButton()
    }

    private fun setupLoginButton(rootView: View) {
        binding!!.loginButton.setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent? ->
            when (event!!.getAction()) {
                MotionEvent.ACTION_DOWN -> {
                    val scaleDown =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
                    v!!.startAnimation(scaleDown)
                    v.setAlpha(0.9f)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v!!.clearAnimation()
                    v.setAlpha(1f)
                }
            }
            false
        })

        binding!!.loginButton.setOnClickListener(View.OnClickListener { v: View? ->
            val login = binding!!.emailEditText.getText().toString().trim { it <= ' ' }
            val password = binding!!.passwordEditText.getText().toString().trim { it <= ' ' }

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            binding!!.progressBar.setVisibility(View.VISIBLE)
            binding!!.loginButton.setEnabled(false)
            authViewModel!!.loginUser(login, password)
                .observe(getViewLifecycleOwner(), Observer { user: User? ->
                    binding!!.progressBar.setVisibility(View.GONE)
                    binding!!.loginButton.setEnabled(true)
                    if (user != null) {
                        val args = Bundle()
                        args.putString("user_role", user.role)
                        findNavController(rootView).navigate(R.id.toMainMenu, args)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Неверный логин или пароль",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        })
    }

    private fun setupRegisterButton() {
        binding!!.registerButton.setOnClickListener(View.OnClickListener { v: View? ->
            findNavController(
                v!!
            ).navigate(R.id.toRegisterFragment)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}