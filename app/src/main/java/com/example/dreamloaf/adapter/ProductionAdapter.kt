package com.example.dreamloaf.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamloaf.adapter.ProductionAdapter.ProductionViewHolder
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.data.Production
import com.example.dreamloaf.data.Sale
import com.example.dreamloaf.databinding.ItemProductionBinding
import com.example.dreamloaf.viewmodels.ProductionViewModel
import com.example.dreamloaf.viewmodels.SaleViewModel

class ProductionAdapter(
    private val productionViewModel: ProductionViewModel,
    private val saleViewModel: SaleViewModel,
    private val selectedDate: String?
) : androidx.recyclerview.widget.ListAdapter<Product, ProductionViewHolder?>(ProductionAdapter.Companion.DIFF_CALLBACK) {
    private val productions: MutableList<Production> = ArrayList<Production>()
    private val sales: MutableList<Sale> = ArrayList<Sale>()
    private var isEditMode = false
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun updateProductionsAndSales(
        productions: Collection<Production>?,
        sales: Collection<Sale>?
    ) {
        this.productions.clear()
        this.sales.clear()
        if (productions != null) {
            this.productions.addAll(productions)
        }
        if (sales != null) {
            this.sales.addAll(sales)
        }
        notifyDataSetChanged()
    }

    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        notifyDataSetChanged()
    }

    fun saveAllData() {
        if (recyclerView == null) return
        for (i in 0 until itemCount) {
            val product: Product = getItem(i)
            val holder = recyclerView?.findViewHolderForAdapterPosition(i) as? ProductionViewHolder
            if (holder != null) {
                productionViewModel.saveProductionData(
                    product.id.toInt(),
                    holder.producedQuantity,
                    selectedDate
                )
                saleViewModel.saveSale(
                    product.id.toInt(),
                    holder.soldQuantity,
                    selectedDate
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionViewHolder {
        val binding = ItemProductionBinding.inflate(
            LayoutInflater.from(parent.getContext()),
            parent,
            false
        )
        return ProductionViewHolder(binding, productions, sales, selectedDate, isEditMode)
    }

    override fun onBindViewHolder(holder: ProductionViewHolder, position: Int) {
        val product: Product = getItem(position)
        holder.bind(product)
    }

    class ProductionViewHolder(
        private val binding: ItemProductionBinding,
        private val productions: MutableList<Production>,
        private val sales: MutableList<Sale>,
        private val selectedDate: String?,
        private val isEditMode: Boolean
    ) : RecyclerView.ViewHolder(binding.getRoot()) {
        var producedQuantity: Int = 0
            private set
        var soldQuantity: Int = 0
            private set
        private var producedTextWatcher: TextWatcher? = null
        private var soldTextWatcher: TextWatcher? = null

        fun bind(product: Product) {
            if (producedTextWatcher != null) {
                binding.etProducedQuantity.removeTextChangedListener(producedTextWatcher)
            }
            if (soldTextWatcher != null) {
                binding.etSoldQuantity.removeTextChangedListener(soldTextWatcher)
            }

            binding.tvProductName.text = product.name
            binding.tvWeight.text = String.format("Вес: %.1f г", product.weight)
            binding.tvPrice.text = String.format("Цена: %.2f руб.", product.price)

            producedQuantity = getProductionQuantity(product.id.toInt())
            soldQuantity = getSalesQuantity(product.id.toInt())

            binding.etProducedQuantity.setText(producedQuantity.toString())
            binding.etSoldQuantity.setText(soldQuantity.toString())
            binding.etProducedQuantity.setEnabled(isEditMode)
            binding.etSoldQuantity.setEnabled(isEditMode)
            binding.btnDecrease.setEnabled(isEditMode)
            binding.btnIncrease.setEnabled(isEditMode)
            binding.btnDecreaseSold.setEnabled(isEditMode)
            binding.btnIncreaseSold.setEnabled(isEditMode)

            setupQuantityControls()
        }

        private fun getProductionQuantity(productId: Int): Int {
            for (p in productions) {
                if (p.productId == productId && p.date == selectedDate) {
                    return p.quantity
                }
            }
            return 0
        }

        private fun getSalesQuantity(productId: Int): Int {
            for (s in sales) {
                if (s.productId == productId && s.date == selectedDate) {
                    return s.quantity
                }
            }
            return 0
        }

        private fun setupQuantityControls() {
            binding.btnIncrease.setOnClickListener(View.OnClickListener { v: View? ->
                producedQuantity++
                binding.etProducedQuantity.setText(producedQuantity.toString())
            })

            binding.btnDecrease.setOnClickListener(View.OnClickListener { v: View? ->
                if (producedQuantity > 0) {
                    producedQuantity--
                    binding.etProducedQuantity.setText(producedQuantity.toString())
                }
            })

            binding.btnIncreaseSold.setOnClickListener(View.OnClickListener { v: View? ->
                soldQuantity++
                binding.etSoldQuantity.setText(soldQuantity.toString())
            })

            binding.btnDecreaseSold.setOnClickListener(View.OnClickListener { v: View? ->
                if (soldQuantity > 0) {
                    soldQuantity--
                    binding.etSoldQuantity.setText(soldQuantity.toString())
                }
            })

            producedTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    try {
                        producedQuantity = if (s.toString().isEmpty()) 0 else s.toString().toInt()
                    } catch (e: NumberFormatException) {
                        producedQuantity = 0
                    }
                }
            }

            soldTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    try {
                        soldQuantity = if (s.toString().isEmpty()) 0 else s.toString().toInt()
                    } catch (e: NumberFormatException) {
                        soldQuantity = 0
                    }
                }
            }

            binding.etProducedQuantity.addTextChangedListener(producedTextWatcher)
            binding.etSoldQuantity.addTextChangedListener(soldTextWatcher)
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Product> =
            object : DiffUtil.ItemCallback<Product>() {
                override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                    return oldItem == newItem
                }
            }
    }
}