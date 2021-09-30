package com.zopnote.android.merchant.viewinvoicehistory;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;

import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.InvoicehistoryFragBinding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewInvoiceHistoryFragment extends Fragment {

    private InvoicehistoryFragBinding binding;
    private ViewModelInvoiceHistory viewmodel;
    private ViewInvoiceHistoryAdapter adapter;

    public ViewInvoiceHistoryFragment() {
        // Required empty public constructor
    }

    public static ViewInvoiceHistoryFragment newInstance() {
        ViewInvoiceHistoryFragment fragment = new ViewInvoiceHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = InvoicehistoryFragBinding.inflate(getLayoutInflater(), container, false);

        adapter = new ViewInvoiceHistoryAdapter(getActivity());
        adapter.setViewModel(ViewInvoiceHistoryActivity.obtainViewModel(this.getActivity()));
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

        viewmodel = ViewInvoiceHistoryActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantName =  merchant.getName();

            }
        });


        viewmodel.invoices.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(@Nullable List<Invoice> invoices) {
                List invoiceList =invoices;

                Collections.sort(invoiceList, new Comparator<Invoice>() {

                    @Override
                    public int compare(Invoice lhs, Invoice rhs) {
                        return rhs.getInvoiceDate().compareTo( lhs.getInvoiceDate());

                    }
                });

                adapter.setItems(invoiceList);
                adapter.notifyDataSetChanged();

                if(invoiceList.size() == 0){
                    setStatusEmpty();
                }else{
                    setStatusReady();
                }
            }
        });
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
