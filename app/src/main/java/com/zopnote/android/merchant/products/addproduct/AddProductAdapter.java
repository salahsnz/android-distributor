package com.zopnote.android.merchant.products.addproduct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.GenericProduct;
import com.zopnote.android.merchant.data.model.PricingModeEnum;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.AddProductItemBinding;
import com.zopnote.android.merchant.products.editproduct.EditProductActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductAdapter extends BaseRecyclerAdapter<GenericProduct, AddProductAdapter.ProductViewHolder> {

    private ImageLoader imageLoader;
    private Context mContext;

    public AddProductAdapter(Context context) {
        super(context);
        mContext = context;
        imageLoader = VolleyManager.getInstance(context).getImageLoader();
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<GenericProduct> newItems) {
        GenericProduct oldItem = getItems().get(oldItemPosition);
        GenericProduct newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<GenericProduct> newItems) {
        GenericProduct oldItem = getItems().get(oldItemPosition);
        GenericProduct newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(newItem.getId(), oldItem.getId());
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AddProductItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.add_product_item,
                        parent, false);
        final ProductViewHolder vh = new ProductViewHolder(binding);
        vh.itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = vh.getAdapterPosition();
                GenericProduct genericProduct =  getItems().get(adapterPosition);
                Product product = getProduct(genericProduct);

                Intent intent = new Intent(getContext(), EditProductActivity.class);
                intent.putExtra(Extras.PRODUCT, product);
                intent.putExtra(Extras.ADD_PRODUCT, true);
                getContext().startActivity(intent);

                ((Activity)mContext).finish();

            }
        });
        return vh;
    }

    private Product getProduct(GenericProduct genericProduct) {
        Product product = new Product();
        product.setId(genericProduct.getId());
        product.setName(genericProduct.getName());
        product.setLanguage(genericProduct.getLanguage());
        product.setFrequency(genericProduct.getFrequency());
        product.setType(genericProduct.getType());
        product.setShortCode(genericProduct.getShortCode());
        product.setLogoUrl(genericProduct.getLogoUrl());
        return product;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final GenericProduct product = getItems().get(position);

        holder.itemBinding.title.setText(product.getName()+" ("+product.getShortCode()+")");
        holder.itemBinding.language.setText(product.getLanguage());

        String frequency = product.getFrequency();
        String capitalizedFrequency = frequency.substring(0,1).toUpperCase() + frequency.substring(1).toLowerCase();
        holder.itemBinding.frequency.setText(capitalizedFrequency);

        holder.itemBinding.logo.setDefaultImageResId(R.drawable.ic_image_black_24dp);
        holder.itemBinding.logo.setErrorImageResId(R.drawable.ic_broken_image_black_24dp);
        holder.itemBinding.logo.setImageUrl(product.getLogoUrl(), imageLoader);

    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        final AddProductItemBinding itemBinding;

        public ProductViewHolder(AddProductItemBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
