package com.example.dreamloaf.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.dreamloaf.data.Product
import com.example.dreamloaf.databinding.FragmentAddProductBinding
import com.example.dreamloaf.viewmodels.ProductViewModel

class AddProductFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentAddProductBinding? = null
    private var productViewModel: ProductViewModel? = null
    private var isEditMode = false
    private var currentProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productViewModel =
            ViewModelProvider(requireActivity()).get<ProductViewModel>(ProductViewModel::class.java)

        var productId: Int = -1
        if (getArguments() != null && getArguments()!!.containsKey("product_id")) {
            productId = getArguments()!!.getInt("product_id")
        }
        if (productId != -1) {
            isEditMode = true
            productViewModel!!.getProductById(productId)
                ?.observe(getViewLifecycleOwner(), Observer { product: Product? ->
                    if (product != null) {
                        currentProduct = product
                        populateForm(product)
                    }
                })
        }

        binding!!.saveButton.setOnClickListener(View.OnClickListener { v: View? ->
            val name = binding!!.nameEditText.getText().toString().trim { it <= ' ' }
            val weightStr = binding!!.weightEditText.getText().toString().trim { it <= ' ' }
            val priceStr = binding!!.priceEditText.getText().toString().trim { it <= ' ' }
            val costPriceStr = binding!!.costPriceEditText.getText().toString().trim { it <= ' ' }

            if (name.isEmpty() || weightStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            try {
                val product: Product?
                if (isEditMode && currentProduct != null) {
                    product = currentProduct
                } else {
                    product = Product()
                }

                product!!.name = name
                product.weight = weightStr.toDouble()
                product.price = priceStr.toDouble()
                product.costPrice = if (costPriceStr.isEmpty()) 0.0 else costPriceStr.toDouble()

                if (isEditMode) {
                    productViewModel!!.update(product)
                    Toast.makeText(requireContext(), "Продукт обновлен", Toast.LENGTH_SHORT).show()
                } else {
                    productViewModel!!.insert(product)
                    Toast.makeText(requireContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show()
                }

                findNavController(v!!).navigateUp()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Некорректный формат числа", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun populateForm(product: Product) {
        binding!!.nameEditText.setText(product.name)
        binding!!.weightEditText.setText(product.weight.toString())
        binding!!.priceEditText.setText(product.price.toString())
        binding!!.costPriceEditText.setText(product.costPrice.toString())
        binding!!.saveButton.setText("Обновить")

        val activity = getActivity()
        if (activity != null) {
            activity!!.setTitle("Редактировать продукт")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}