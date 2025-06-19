package com.example.dreamloaf.production

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dreamloaf.adapter.ProductionAdapter
import com.example.dreamloaf.databinding.FragmentProductionLogBinding
import com.example.dreamloaf.viewmodels.ProductViewModel
import com.example.dreamloaf.viewmodels.ProductionViewModel
import com.example.dreamloaf.viewmodels.SaleViewModel
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class ProductionLogFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentProductionLogBinding? = null
    private lateinit var productViewModel: ProductViewModel
    private lateinit var productionViewModel: ProductionViewModel
    private lateinit var saleViewModel: SaleViewModel
    private var adapter: ProductionAdapter? = null
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductionLogBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedDate = arguments?.getString("selected_date", this.currentDate) ?: this.currentDate
        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
        productionViewModel = ViewModelProvider(this).get(ProductionViewModel::class.java)
        saleViewModel = ViewModelProvider(this).get(SaleViewModel::class.java)
        adapter = ProductionAdapter(productionViewModel, saleViewModel, selectedDate)
        adapter!!.setEditMode(true)
        binding!!.productionRecyclerView.adapter = adapter
        binding!!.productionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.tvDateTitle.text = "Производство на $selectedDate"
        binding!!.btnSaveAll.setOnClickListener {
            adapter!!.saveAllData()
            Toast.makeText(requireContext(), "Данные сохранены", Toast.LENGTH_SHORT).show()
        }
        loadDataForDate(selectedDate)
    }

    private val currentDate: String?
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return LocalDate.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
            } else {
                return DateFormat.getDateInstance().format(Date())
            }
        }

    private fun loadDataForDate(date: String?) {
        productViewModel.allProducts?.observe(viewLifecycleOwner) { products ->
            if (products.isNullOrEmpty()) {
                binding!!.emptyState.visibility = View.VISIBLE
                binding!!.productionRecyclerView.visibility = View.GONE
            } else {
                binding!!.emptyState.visibility = View.GONE
                binding!!.productionRecyclerView.visibility = View.VISIBLE
                productionViewModel.getProductionByDate(date)?.observe(viewLifecycleOwner) { productions ->
                    saleViewModel.getSalesByDate(date)?.observe(viewLifecycleOwner) { sales ->
                        val safeProductions = productions?.filterNotNull() ?: emptyList()
                        val safeSales = sales?.filterNotNull() ?: emptyList()
                        val safeProducts = products.filterNotNull()
                        adapter!!.updateProductionsAndSales(safeProductions, safeSales)
                        adapter!!.submitList(safeProducts)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}