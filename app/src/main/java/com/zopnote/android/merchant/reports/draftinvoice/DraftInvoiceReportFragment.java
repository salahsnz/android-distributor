package com.zopnote.android.merchant.reports.draftinvoice;

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
import com.zopnote.android.merchant.data.model.DraftInvoiceReportItem;
import com.zopnote.android.merchant.databinding.DraftInvoiceReportFragBinding;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class DraftInvoiceReportFragment extends Fragment {

    private DraftInvoiceReportFragBinding binding;
    private DraftInvoiceReportViewModel viewmodel;
    private DraftInvoiceReportAdapter adapter;
    private String route;

    public DraftInvoiceReportFragment() {
        // Requires empty public constructor
    }

    public static DraftInvoiceReportFragment newInstance(String route) {
        DraftInvoiceReportFragment fragment = new DraftInvoiceReportFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras.ROUTE, route);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DraftInvoiceReportFragBinding.inflate(inflater, container, false);

        binding.recyclerView.setHasFixedSize(true);

        route = getArguments().getString(Extras.ROUTE);

        adapter = new DraftInvoiceReportAdapter(getActivity());

        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerDecoration);
        binding.recyclerView.setAdapter(adapter);

        // hide all
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = DraftInvoiceReportActivity.obtainViewModel(getActivity());

        viewmodel.invoiceTypeChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean changed) {
                if(changed){
                    if( viewmodel.apiCallError.getValue() || viewmodel.networkError.getValue()){
                        //do nothing
                    }else{
                        viewmodel.networkError.setValue(false);
                        setData();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DraftInvoiceReportActivity)getActivity()).getReports();
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    binding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    setData();
                }
            }
        });

        viewmodel.networkError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if(error){
                    binding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
                    setStatusNetworkError();
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkUtil.isNetworkAvailable(getContext())){
            if(viewmodel.apiCallError.getValue() != null  && viewmodel.apiCallError.getValue()){
                //api error, do nothing
            }else{
                viewmodel.networkError.setValue(false);
                setData();
            }
        }else{
            viewmodel.networkError.setValue(true);
        }
    }

    private void setData() {
        List draftInvoicesList = getDraftInvoicesForRoute(route);
        adapter.setItems(draftInvoicesList);

        if(draftInvoicesList.size() == 0){
            setStatusEmpty();
        }else{
            setStatusReady();
        }
    }

    private List<DraftInvoiceReportItem> getDraftInvoicesForRoute(String route) {
        List draftInvoices = new ArrayList();
        if( ! viewmodel.draftInvoiceReport.isEmpty()){
            for (DraftInvoiceReportItem draftInvoiceReportItem: viewmodel.draftInvoiceReport) {
                if(draftInvoiceReportItem.getRoute().equalsIgnoreCase(route)){
                    addToList(draftInvoiceReportItem, draftInvoices);
                }
            }
        }
        return draftInvoices;
    }

    private void addToList(DraftInvoiceReportItem draftInvoiceReportItem, List draftInvoices) {

        if(viewmodel.invoiceType.equalsIgnoreCase("changes")) {
            if(draftInvoiceReportItem.getNotes() != null && ! draftInvoiceReportItem.getNotes().isEmpty()){
                draftInvoices.add(draftInvoiceReportItem);
            }
        }else{
            draftInvoices.add(draftInvoiceReportItem);
        }
    }

    private void setStatusNetworkError() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }
}
