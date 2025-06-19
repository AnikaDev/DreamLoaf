package com.example.dreamloaf.products

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dreamloaf.R
import com.example.dreamloaf.adapter.ProductAdapter
import com.example.dreamloaf.adapter.ProductAdapter.OnDeleteClickListener
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.databinding.FragmentProductListBinding
import com.example.dreamloaf.viewmodels.ProductViewModel

class ProductListFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentProductListBinding? = null
    private var viewModel: ProductViewModel? = null
    private var adapter: ProductAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get<ProductViewModel>(ProductViewModel::class.java)

        setupAdapter()
        setupObservers()

        binding!!.addProductButton.setOnClickListener(View.OnClickListener { v: View? ->
            findNavController(
                v!!
            ).navigate(R.id.toAddProduct)
        })
    }

    private fun setupAdapter() {
        adapter = ProductAdapter()
        binding!!.productsRecyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        binding!!.productsRecyclerView.setAdapter(adapter)

        adapter!!.setOnDeleteClickListener(object : OnDeleteClickListener {
            override fun onDeleteClick(product: Product?) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Удаление")
                    .setMessage("Удалить ${product?.name}?")
                    .setPositiveButton("Да") { d, w ->
                        product?.let { viewModel!!.delete(it.id.toInt()) }
                    }
                    .setNegativeButton("Нет", null)
                    .show()
            }
        })

        adapter!!.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(product: Product?) {
                val args = Bundle()
                args.putInt("product_id", product?.id?.toInt() ?: -1)
                findNavController(requireView()).navigate(R.id.toAddProduct, args)
            }
        })
    }

    private fun setupObservers() {
        viewModel?.allProducts?.observe(viewLifecycleOwner) { products ->
            if (products.isNullOrEmpty()) {
                binding!!.emptyState.visibility = View.VISIBLE
                binding!!.productsRecyclerView.visibility = View.GONE
            } else {
                binding!!.emptyState.visibility = View.GONE
                binding!!.productsRecyclerView.visibility = View.VISIBLE
                adapter!!.submitList(products.filterNotNull())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}