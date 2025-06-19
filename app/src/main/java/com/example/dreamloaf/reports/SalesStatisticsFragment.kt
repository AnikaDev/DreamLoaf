package com.example.dreamloaf.reports

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.databinding.FragmentSalesStatisticsBinding
import com.example.dreamloaf.viewmodels.ProductViewModel
import com.example.dreamloaf.viewmodels.SaleViewModel

class SalesStatisticsFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentSalesStatisticsBinding? = null
    private var saleViewModel: SaleViewModel? = null
    private var productViewModel: ProductViewModel? = null
    private val colors = intArrayOf(
        Color.parseColor("#FF6200EE"),
        Color.parseColor("#FF03DAC5"),
        Color.parseColor("#FF018786"),
        Color.parseColor("#FFBB86FC"),
        Color.parseColor("#FF3700B3"),
        Color.parseColor("#FFFF5722"),
        Color.parseColor("#FFE91E63"),
        Color.parseColor("#FF8BC34A"),
        Color.parseColor("#FF795548"),
        Color.parseColor("#FF9C27B0"),
        Color.parseColor("#FF3F51B5"),
        Color.parseColor("#FF00BCD4"),
        Color.parseColor("#FFCDDC39"),
        Color.parseColor("#FFFF9800"),
        Color.parseColor("#FF607D8B")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSalesStatisticsBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saleViewModel =
            ViewModelProvider(requireActivity()).get<SaleViewModel>(SaleViewModel::class.java)
        productViewModel =
            ViewModelProvider(requireActivity()).get<ProductViewModel>(ProductViewModel::class.java)
        loadData()
    }


    private fun loadData() {
        productViewModel?.allProducts?.observe(viewLifecycleOwner) { products ->
            if (!products.isNullOrEmpty()) {
                saleViewModel?.allSales?.observe(viewLifecycleOwner) { sales ->
                    if (!sales.isNullOrEmpty()) {
                        val safeProducts = products.filterNotNull()
                        val safeSales = sales.filterNotNull()
                        val lineData = prepareLineChartData(safeSales, safeProducts)
                        val pieData = preparePieChartData(safeSales, safeProducts)
                        binding!!.lineChartView.setPieData(pieData)
                        binding!!.lineChartView.setLineData(lineData)
                        setupCombinedLegend(lineData.keys)
                    } else {
                        showEmptyState()
                    }
                }
            } else {
                showEmptyState()
            }
        }
    }

    private fun setupCombinedLegend(productNames: Iterable<String>) {
        val legendContainer = binding!!.legendContainer
        legendContainer.removeAllViews()
        val context: Context = requireContext()
        val legendTitle = TextView(context)
        legendTitle.text = "Легенда:"
        legendTitle.textSize = 16f
        legendTitle.setTextColor(Color.BLACK)
        legendTitle.setTypeface(null, Typeface.BOLD)
        legendContainer.addView(legendTitle)
        val productList: MutableList<String> = ArrayList()
        for (productName in productNames) {
            productList.add(productName)
        }
        for (i in productList.indices) {
            val productName = productList[i]
            val legendItem = LinearLayout(context)
            legendItem.orientation = LinearLayout.HORIZONTAL
            legendItem.gravity = Gravity.CENTER_VERTICAL
            legendItem.setPadding(0, dpToPx(8), 0, 0)
            val colorLine = View(context)
            colorLine.setBackgroundColor(colors[i % colors.size])
            val lineParams = LinearLayout.LayoutParams(dpToPx(40), dpToPx(3))
            lineParams.setMargins(0, 0, dpToPx(12), 0)
            colorLine.layoutParams = lineParams
            val productText = TextView(context)
            productText.text = productName
            productText.textSize = 14f
            productText.setTextColor(Color.BLACK)
            legendItem.addView(colorLine)
            legendItem.addView(productText)
            legendContainer.addView(legendItem)
        }
    }


    private fun preparePieChartData(
        sales: List<Sale>,
        products: List<Product>
    ): MutableMap<String, Int> {
        val result = mutableMapOf<String, Int>()
        for (sale in sales) {
            val productName = getProductName(products, sale.productId)
            if (productName != null) {
                result[productName] = result.getOrDefault(productName, 0) + sale.quantity
            }
        }
        return result
    }

    private fun prepareLineChartData(
        sales: List<Sale>,
        products: List<Product>
    ): MutableMap<String, MutableMap<String, Int>> {
        val result = mutableMapOf<String, MutableMap<String, Int>>()
        for (product in products) {
            result[product.name.toString()] = mutableMapOf()
        }
        for (sale in sales) {
            val productName = getProductName(products, sale.productId)
            if (productName != null) {
                val date = formatDateForChart(sale.date ?: "")
                val productMap = result[productName] ?: mutableMapOf()
                productMap[date.toString()] = productMap.getOrDefault(date, 0) + sale.quantity
                result[productName] = productMap
            }
        }
        return result
    }

    private fun formatDateForChart(dateStr: String): String? {
        try {
            val parts: Array<String?> =
                dateStr.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size >= 3) {
                return parts[2] + "." + parts[1]
            }
            return dateStr
        } catch (e: Exception) {
            return dateStr
        }
    }

    private fun showEmptyState() {
        binding!!.chartContainer.setVisibility(View.GONE)
        val emptyView = TextView(requireContext())
        emptyView.setText("Нет данных для отображения")
        emptyView.setTextSize(18f)
        emptyView.setGravity(Gravity.CENTER)
        binding!!.getRoot().addView(emptyView)
    }

    private fun getProductName(products: List<Product>, productId: Int): String? {
        for (product in products) {
            if (product.id.toInt() == productId) {
                return product.name
            }
        }
        return null
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * getResources().getDisplayMetrics().density).toInt()
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
