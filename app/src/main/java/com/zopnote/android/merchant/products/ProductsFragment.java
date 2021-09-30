package com.zopnote.android.merchant.products;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.ProductsFragBinding;
import com.zopnote.android.merchant.util.Extras;

import java.util.Arrays;
import java.util.List;

import static com.zopnote.android.merchant.products.ProductsActivity.obtainViewModel;


public class ProductsFragment extends Fragment {
    private ProductsViewModel viewmodel;
    private ProductsFragBinding binding;
    private ProductsAdapter adapter;


    public ProductsFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment newInstance(String type) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putString(Extras.PRODUCT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ProductsFragBinding.inflate(inflater, container,false);
        binding.recylerView.setHasFixedSize(true);

        adapter = new ProductsAdapter(getActivity());
       /* String type = getArguments().getString(Extras.PRODUCT_TYPE);
        switch (type) {
            case "newspapers":
                adapter = new ProductsAdapter(getActivity());
            case "magazines":
                adapter = new ProductsAdapter(getActivity());
                break;
            default:
                // should not reach here
                throw new IllegalArgumentException(String.format("Unsupported tab type %s", type));
        }*/


        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recylerView.addItemDecoration(dividerDecoration);
        binding.recylerView.setAdapter(adapter);

        // hide all
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = obtainViewModel(getActivity());

        final String productType = getArguments().getString(Extras.PRODUCT_TYPE);

        LiveData<List<Product>> productsLiveData = viewmodel.getOfferedProduct(productType);

        if (productsLiveData != null) {
            productsLiveData.observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(@Nullable List<Product> products) {
                    adapter.setItems(products);
                    if (products.size() == 0) {
                        setStatusEmtpy();
                    } else {
                        setStatusReady();
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }



    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmtpy() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }
}
