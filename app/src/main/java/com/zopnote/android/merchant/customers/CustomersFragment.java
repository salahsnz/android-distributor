package com.zopnote.android.merchant.customers;

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

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.databinding.CustomersFragBinding;
import com.zopnote.android.merchant.util.Extras;

import java.util.List;

/**
 * Created by nmohideen on 26/12/17.
 */

public class CustomersFragment extends Fragment {

    private CustomersFragBinding binding;
    private CustomersViewModel viewmodel;
    private CustomersAdapter adapter;

    public CustomersFragment() {
        // Requires empty public constructor
    }

    public static CustomersFragment newInstance(String route) {
        CustomersFragment fragment = new CustomersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras.ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = CustomersFragBinding.inflate(inflater, container, false);

        binding.recylerView.setHasFixedSize(true);

        adapter = new CustomersAdapter(getActivity());

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

        viewmodel = CustomersActivity.obtainViewModel(getActivity());
        adapter.setViewModel(viewmodel);
        String route = getArguments().getString(Extras.ROUTE);
        LiveData<List<Customer>> customersLiveData = viewmodel.getCustomers(route);

        if (customersLiveData != null) {
            customersLiveData.observe(this, new Observer<List<Customer>>() {
                @Override
                public void onChanged(@Nullable List<Customer> customers) {
                    adapter.setItems(customers);
                    if (customers.size() == 0) {
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
