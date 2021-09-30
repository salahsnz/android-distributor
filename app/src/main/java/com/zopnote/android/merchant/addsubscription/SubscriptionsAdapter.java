package com.zopnote.android.merchant.addsubscription;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.SubscriptionItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsAdapter extends BaseRecyclerAdapter<Product, SubscriptionsAdapter.SubscriptionViewHolder> implements Filterable {

    private AddSubscriptionViewModel viewmodel;
    private ItemClicked listener;
    private List<Product> productListFiltered;

    public SubscriptionsAdapter(Context context, ItemClicked listener) {
        super(context);

        this.listener = listener;
    }

    // TODO Use an interface
    public void setViewModel(AddSubscriptionViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Product> newItems) {
        Product oldItem = getItems().get(oldItemPosition);
        Product newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Product> newItems) {
        Product oldItem = getItems().get(oldItemPosition);
        Product newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(newItem.getId(), oldItem.getId())
                && ObjectsUtil.equals(newItem.getName(), oldItem.getName());
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SubscriptionItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.subscription_item,
                        parent, false);
        final SubscriptionViewHolder vh = new SubscriptionViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SubscriptionViewHolder holder, int position) {
        final Product product = getItems().get(position);

        holder.binding.name.setText(product.getName());

        if (viewmodel.productIdMap.containsKey(product.getId())) {
            holder.binding.selectedIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.binding.selectedIndicator.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // toggle selected indicator
                if (holder.binding.selectedIndicator.getVisibility() == View.VISIBLE) {
                    holder.binding.selectedIndicator.setVisibility(View.GONE);
                } else {
                    holder.binding.selectedIndicator.setVisibility(View.VISIBLE);
                }

                listener.onClick(product);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String alphabet = constraint.toString();
                if(alphabet.isEmpty() || alphabet.equalsIgnoreCase(getContext().getResources().getString(R.string.products_grid_item_all_label))){
                    productListFiltered = viewmodel.productList;
                }else {
                    productListFiltered = new ArrayList<>();
                    for (Product product: viewmodel.productList) {
                        if(product.getName().startsWith(alphabet)){
                            productListFiltered.add(product);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productListFiltered = (List<Product>) results.values;
                setItems(productListFiltered);
                notifyDataSetChanged();
            }
        };
    }

    static class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        final SubscriptionItemBinding binding;

        public SubscriptionViewHolder(SubscriptionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    interface ItemClicked {
        void onClick(Product product);
    }
}
