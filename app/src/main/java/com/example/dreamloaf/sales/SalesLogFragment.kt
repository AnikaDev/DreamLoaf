package com.example.dreamloaf.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dreamloaf.R
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.databinding.FragmentSalesLogBinding
import com.example.dreamloaf.viewmodels.ProductViewModel
import com.example.dreamloaf.viewmodels.SaleViewModel
import java.text.NumberFormat
import java.util.Locale

class SalesLogFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentSalesLogBinding? = null
    private var saleViewModel: SaleViewModel? = null
    private var productViewModel: ProductViewModel? = null
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSalesLogBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saleViewModel = ViewModelProvider(this).get<SaleViewModel>(SaleViewModel::class.java)
        productViewModel =
            ViewModelProvider(this).get<ProductViewModel>(ProductViewModel::class.java)

        loadSalesData()
    }

    private fun loadSalesData() {
        saleViewModel?.allSales?.observe(viewLifecycleOwner) { sales ->
            if (sales.isNullOrEmpty()) {
                showEmptyState()
                return@observe
            }
            productViewModel?.allProducts?.observe(viewLifecycleOwner) { products ->
                if (products.isNullOrEmpty()) {
                    showEmptyState()
                    return@observe
                }
                calculateAndDisplayResults(sales.filterNotNull(), products.filterNotNull())
                binding!!.emptyState.visibility = View.GONE
            }
        }
    }

    private fun calculateAndDisplayResults(
        sales: List<Sale>,
        products: List<Product>
    ) {
        var totalRevenue = 0.0
        var totalCost = 0.0
        val productDataMap = mutableMapOf<Int, ProductSalesData>()
        for (sale in sales) {
            val product = findProductById(products, sale.productId)
            if (product != null) {
                val productRevenue = sale.quantity * product.price
                val productCost = sale.quantity * product.costPrice
                totalRevenue += productRevenue
                totalCost += productCost
                val data = productDataMap.getOrDefault(product.id.toInt(), ProductSalesData(product.name))
                data.addSale(sale.quantity, productRevenue, productCost)
                productDataMap[product.id.toInt()] = data
            }
        }
        binding!!.tvTotalRevenue.text = currencyFormat.format(totalRevenue)
        binding!!.tvTotalCost.text = currencyFormat.format(totalCost)
        binding!!.tvTotalProfit.text = currencyFormat.format(totalRevenue - totalCost)
        binding!!.layoutProductsBreakdown.removeAllViews()
        for (data in productDataMap.values) {
            addProductBreakdownRow(data)
        }
    }

    private fun addProductBreakdownRow(data: ProductSalesData) {
        val itemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_product_breakdown, binding!!.layoutProductsBreakdown, false)

        val tvProductName = itemView.findViewById<TextView>(R.id.tvProductName)
        val tvQuantity = itemView.findViewById<TextView>(R.id.tvQuantity)
        val tvRevenue = itemView.findViewById<TextView>(R.id.tvRevenue)
        val tvCost = itemView.findViewById<TextView>(R.id.tvCost)
        val tvProfit = itemView.findViewById<TextView>(R.id.tvProfit)

        tvProductName.setText(data.productName)
        tvQuantity.setText(data.totalQuantity.toString())
        tvRevenue.setText(currencyFormat.format(data.totalRevenue))
        tvCost.setText(currencyFormat.format(data.totalCost))
        tvProfit.setText(currencyFormat.format(data.totalRevenue - data.totalCost))

        binding!!.layoutProductsBreakdown.addView(itemView)
    }

    private class ProductSalesData(var productName: String?) {
        var totalQuantity: Int = 0
        var totalRevenue: Double = 0.0
        var totalCost: Double = 0.0

        fun addSale(quantity: Int, revenue: Double, cost: Double) {
            this.totalQuantity += quantity
            this.totalRevenue += revenue
            this.totalCost += cost
        }
    }

    private fun findProductById(products: List<Product>, productId: Int): Product? {
        for (product in products) {
            if (product.id.toInt() == productId) {
                return product
            }
        }
        return null
    }

    private fun showEmptyState() {
        binding!!.emptyState.visibility = View.VISIBLE
        binding!!.salesRecyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}