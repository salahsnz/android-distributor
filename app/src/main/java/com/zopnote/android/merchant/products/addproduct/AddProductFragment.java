package com.zopnote.android.merchant.products.addproduct;


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

import com.zopnote.android.merchant.data.model.GenericProduct;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.AddProductFragBinding;
import com.zopnote.android.merchant.util.Extras;

import java.util.ArrayList;
import java.util.List;

import static com.zopnote.android.merchant.products.addproduct.AddProductActivity.obtainViewModel;

public class AddProductFragment extends Fragment {
    private AddProductViewModel viewmodel;
    private AddProductFragBinding binding;
    private AddProductAdapter adapter;
    private LiveData<List<Product>> productsLiveData;

    public AddProductFragment() {
        // Required empty public constructor
    }

    public static AddProductFragment newInstance(String type) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putString(Extras.PRODUCT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = AddProductFragBinding.inflate(inflater, container,false);
        binding.recylerView.setHasFixedSize(true);
        adapter = new AddProductAdapter(getActivity());
       /* String type = getArguments().getString(Extras.PRODUCT_TYPE);
        switch (type) {
            case "newspapers":
                adapter = new AddProductAdapter(getActivity());
            case "magazines":
                adapter = new AddProductAdapter(getActivity());
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

        final String type = getArguments().getString(Extras.PRODUCT_TYPE);
        LiveData<List<GenericProduct>> allProductsLiveData = viewmodel.getAllProduct(type);
        productsLiveData = viewmodel.getOfferedProduct(type);
       /* switch (type) {
            case "newspapers":
                allProductsLiveData = viewmodel.allNewspapers;
                productsLiveData = viewmodel.newspapers;
                break;
            case "magazines":
                allProductsLiveData = viewmodel.allMagazines;
                productsLiveData = viewmodel.magazines;
                break;
        }*/

        if (allProductsLiveData != null) {
            allProductsLiveData.observe(this, new Observer<List<GenericProduct>>() {
                @Override
                public void onChanged(@Nullable final List<GenericProduct> allProducts) {

                    productsLiveData.observe(getActivity(), new Observer<List<Product>>() {
                        @Override
                        public void onChanged(@Nullable List<Product> products) {
                            List<GenericProduct> uniqueList = new ArrayList<>();
                            for (GenericProduct p: allProducts) {
                                if(! products.contains(p)){
                                    uniqueList.add(p);
                                }
                            }

                            adapter.setItems(uniqueList);
                            if (uniqueList.size() == 0) {
                                setStatusEmtpy();
                            } else {
                                setStatusReady();
                            }
                        }
                    });

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
