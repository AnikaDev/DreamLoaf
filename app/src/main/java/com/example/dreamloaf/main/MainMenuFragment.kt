package com.example.dreamloaf.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.dreamloaf.R
import com.example.dreamloaf.databinding.FragmentMainMenuBinding
import com.example.dreamloaf.viewmodels.AuthViewModel

class MainMenuFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentMainMenuBinding? = null
    private var authViewModel: AuthViewModel? = null
    private var currentUserRole: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserRole = arguments?.getString("user_role", "")

        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        setupNavigation()
    }

    private fun setupNavigation() {
        binding!!.btnProducts.setOnClickListener(View.OnClickListener { v: View? ->
            if ("Менеджер" == currentUserRole) {
                showAccessDenied()
                return@OnClickListener
            }
            findNavController(v!!).navigate(R.id.toProductList)
        })

        binding!!.btnProduction.setOnClickListener(View.OnClickListener { v: View? ->
            findNavController(
                v!!
            ).navigate(R.id.toCalendar)
        })

        binding!!.btnSales.setOnClickListener(View.OnClickListener { v: View? ->
            if ("Пекарь" == currentUserRole) {
                showAccessDenied()
                return@OnClickListener
            }
            findNavController(v!!).navigate(R.id.toSalesLog)
        })

        binding!!.btnStatistics.setOnClickListener(View.OnClickListener { v: View? ->
            if ("Пекарь" == currentUserRole) {
                showAccessDenied()
                return@OnClickListener
            }
            findNavController(v!!).navigate(R.id.toSalesStatistics)
        })
    }

    private fun showAccessDenied() {
        Toast.makeText(requireContext(), "Доступ ограничен", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}