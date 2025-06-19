package com.example.dreamloaf.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamloaf.R
import com.example.dreamloaf.adapter.ProductAdapter.ProductViewHolder
import com.example.dreamloaf.data.Product

class ProductAdapter : androidx.recyclerview.widget.ListAdapter<Product, ProductViewHolder?>(
    DIFF_CALLBACK
) {
    interface OnDeleteClickListener {
        fun onDeleteClick(product: Product?)
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product?)
    }

    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener?) {
        this.onDeleteClickListener = listener
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product: Product = getItem(position)
        holder.bind(product)
    }

    public inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView
        private val tvWeight: TextView
        private val tvPrice: TextView
        private val btnDelete: View

        init {
            tvName = itemView.findViewById<TextView>(R.id.tvName)
            tvWeight = itemView.findViewById<TextView>(R.id.tvWeight)
            tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
            btnDelete = itemView.findViewById<View>(R.id.btnDelete)

            itemView.setOnClickListener(View.OnClickListener { v: View? ->
                val position: Int = getAdapterPosition()
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    val product: Product? = getItem(position)

                    if (v!!.getTag() != null && v.getTag() as Long + 500 > System.currentTimeMillis()) {
                        onItemClickListener!!.onItemClick(product)
                    }
                    v.setTag(System.currentTimeMillis())
                }
            })
        }

        fun bind(product: Product) {
            tvName.text = product.name
            tvWeight.text = String.format("%.1f г", product.weight)
            tvPrice.text = String.format("%.2f руб.", product.price)
            btnDelete.setOnClickListener { v: View? ->
                onDeleteClickListener?.onDeleteClick(product)
            }
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