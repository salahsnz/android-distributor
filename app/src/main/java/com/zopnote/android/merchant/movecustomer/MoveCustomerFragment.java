package com.zopnote.android.merchant.movecustomer;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.databinding.MoveCustomerFragBinding;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MoveCustomerFragment extends Fragment {

    private MoveCustomerFragBinding binding;
    private MoveCustomerViewModel viewmodel;
    private MoveCustomerAdapter adapter;

    public MoveCustomerFragment() {
        // Requires empty public constructor
    }

    public static MoveCustomerFragment newInstance(String customerId, String route) {
        MoveCustomerFragment fragment = new MoveCustomerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras.CUSTOMER_ID, customerId);
        bundle.putString(Extras.ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = MoveCustomerFragBinding.inflate(inflater, container, false);

        binding.recyclerView.setHasFixedSize(true);

        adapter = new MoveCustomerAdapter(getActivity());

        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerDecoration);
        binding.recyclerView.setAdapter(adapter);

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

        viewmodel = MoveCustomerActivity.obtainViewModel(getActivity());

        adapter.setViewModel(viewmodel);

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {

                String moveHintTitle = getMoveCustomerHintTitle();
                String moveHintText = getMoveCustomerHintText();

                binding.moveCustomerHintTitle.setText(moveHintTitle);
                binding.moveCustomerHintText.setText(moveHintText);

            }
        });

        viewmodel.customers.observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable List<Customer> customers) {
                List<Customer> filteredCustomer = getFilteredCustomersList(customers);
                adapter.setItems(filteredCustomer);
                if (filteredCustomer.size() == 0) {
                    setStatusEmpty();
                } else {
                    setStatusReady();
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    private List<Customer> getFilteredCustomersList(List<Customer> customers) {
        List<Customer> arrayList = new ArrayList();
        for (Customer customer:customers) {
            if(! customer.getId().equals(viewmodel.customer.getValue().getId())){
                arrayList.add(customer);
            }
        }
        return arrayList;
    }

    private String getMoveCustomerHintTitle() {
        String doorNumber = viewmodel.customer.getValue().getDoorNumber();
        String addressLine1 = viewmodel.customer.getValue().getAddressLine1();
        StringBuilder builder = new StringBuilder();
        builder.append(doorNumber);
        if(addressLine1 != null && ! addressLine1.isEmpty()){
            String addrLine1 = Utils.getAddressLine1(getContext(), addressLine1).trim();
            if( ! addrLine1.isEmpty()){
                builder.append(" \u2022 ");
                builder.append(addrLine1);
            }
        }
        return String.format(getResources().getString(R.string.move_customer_hint_title), builder.toString());
    }

    private String getMoveCustomerHintText() {
        String doorNumber = viewmodel.customer.getValue().getDoorNumber();
        String addressLine1 = viewmodel.customer.getValue().getAddressLine1();
        StringBuilder builder = new StringBuilder();
        builder.append(doorNumber);
        if(addressLine1 != null && ! addressLine1.isEmpty()){
            String addrLine1 = Utils.getAddressLine1(getContext(), addressLine1).trim();
            if( ! addrLine1.isEmpty()){
                builder.append(" \u2022 ");
                builder.append(addrLine1);
            }
        }
        return String.format(getResources().getString(R.string.move_customer_hint_text), builder.toString());
    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
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
