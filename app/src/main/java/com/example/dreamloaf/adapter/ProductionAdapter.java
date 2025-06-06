package com.example.dreamloaf.adapter;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dreamloaf.data.Product;
import com.example.dreamloaf.data.Production;
import com.example.dreamloaf.data.Sale;
import com.example.dreamloaf.databinding.ItemProductionBinding;
import com.example.dreamloaf.viewmodels.ProductionViewModel;
import com.example.dreamloaf.viewmodels.SaleViewModel;
import java.util.ArrayList;
import java.util.List;

public class ProductionAdapter extends ListAdapter<Product, ProductionAdapter.ProductionViewHolder> {

    private final ProductionViewModel productionViewModel;
    private final SaleViewModel saleViewModel;
    private final String selectedDate;
    private List<Production> productions = new ArrayList<>();
    private List<Sale> sales = new ArrayList<>();
    private boolean isEditMode = false;
    private RecyclerView recyclerView;

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };

    public ProductionAdapter(ProductionViewModel productionViewModel,
                             SaleViewModel saleViewModel,
                             String selectedDate) {
        super(DIFF_CALLBACK);
        this.productionViewModel = productionViewModel;
        this.saleViewModel = saleViewModel;
        this.selectedDate = selectedDate;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void updateProductionsAndSales(List<Production> productions, List<Sale> sales) {
        this.productions.clear();
        this.sales.clear();

        if (productions != null) {
            this.productions.addAll(productions);
        }

        if (sales != null) {
            this.sales.addAll(sales);
        }

        notifyDataSetChanged();
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    public void saveAllData() {
        if (recyclerView == null) return;

        for (int i = 0; i < getItemCount(); i++) {
            Product product = getItem(i);
            ProductionViewHolder holder = (ProductionViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                productionViewModel.saveProductionData(
                        product.getId(),
                        holder.getProducedQuantity(),
                        selectedDate
                );

                saleViewModel.saveSale(
                        product.getId(),
                        holder.getSoldQuantity(),
                        selectedDate);
            }
        }
    }

    @NonNull
    @Override
    public ProductionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductionBinding binding = ItemProductionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ProductionViewHolder(binding, productions, sales, selectedDate, isEditMode);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductionViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product);
    }

    public static class ProductionViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductionBinding binding;
        private int producedQuantity;
        private int soldQuantity;
        private final List<Production> productions;
        private final List<Sale> sales;
        private final String selectedDate;
        private final boolean isEditMode;
        private TextWatcher producedTextWatcher;
        private TextWatcher soldTextWatcher;

        public ProductionViewHolder(ItemProductionBinding binding,
                                    List<Production> productions,
                                    List<Sale> sales,
                                    String selectedDate,
                                    boolean isEditMode) {
            super(binding.getRoot());
            this.binding = binding;
            this.productions = productions;
            this.sales = sales;
            this.selectedDate = selectedDate;
            this.isEditMode = isEditMode;
        }

        public int getProducedQuantity() {
            return producedQuantity;
        }

        public int getSoldQuantity() {
            return soldQuantity;
        }

        public void bind(Product product) {
            if (producedTextWatcher != null) {
                binding.etProducedQuantity.removeTextChangedListener(producedTextWatcher);
            }
            if (soldTextWatcher != null) {
                binding.etSoldQuantity.removeTextChangedListener(soldTextWatcher);
            }

            binding.tvProductName.setText(product.getName());
            binding.tvWeight.setText(String.format("Вес: %.1f г", product.getWeight()));
            binding.tvPrice.setText(String.format("Цена: %.2f руб.", product.getPrice()));

            producedQuantity = getProductionQuantity(product.getId());
            soldQuantity = getSalesQuantity(product.getId());

            binding.etProducedQuantity.setText(String.valueOf(producedQuantity));
            binding.etSoldQuantity.setText(String.valueOf(soldQuantity));
            binding.etProducedQuantity.setEnabled(isEditMode);
            binding.etSoldQuantity.setEnabled(isEditMode);
            binding.btnDecrease.setEnabled(isEditMode);
            binding.btnIncrease.setEnabled(isEditMode);
            binding.btnDecreaseSold.setEnabled(isEditMode);
            binding.btnIncreaseSold.setEnabled(isEditMode);

            setupQuantityControls();
        }

        private int getProductionQuantity(int productId) {
            for (Production p : productions) {
                if (p.getProductId() == productId && p.getDate().equals(selectedDate)) {
                    return p.getQuantity();
                }
            }
            return 0;
        }

        private int getSalesQuantity(int productId) {
            for (Sale s : sales) {
                if (s.getProductId() == productId && s.getDate().equals(selectedDate)) {
                    return s.getQuantity();
                }
            }
            return 0;
        }

        private void setupQuantityControls() {
            binding.btnIncrease.setOnClickListener(v -> {
                producedQuantity++;
                binding.etProducedQuantity.setText(String.valueOf(producedQuantity));
            });

            binding.btnDecrease.setOnClickListener(v -> {
                if (producedQuantity > 0) {
                    producedQuantity--;
                    binding.etProducedQuantity.setText(String.valueOf(producedQuantity));
                }
            });

            binding.btnIncreaseSold.setOnClickListener(v -> {
                soldQuantity++;
                binding.etSoldQuantity.setText(String.valueOf(soldQuantity));
            });

            binding.btnDecreaseSold.setOnClickListener(v -> {
                if (soldQuantity > 0) {
                    soldQuantity--;
                    binding.etSoldQuantity.setText(String.valueOf(soldQuantity));
                }
            });

            producedTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        producedQuantity = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        producedQuantity = 0;
                    }
                }
            };

            soldTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        soldQuantity = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        soldQuantity = 0;
                    }
                }
            };

            binding.etProducedQuantity.addTextChangedListener(producedTextWatcher);
            binding.etSoldQuantity.addTextChangedListener(soldTextWatcher);
        }
    }
}