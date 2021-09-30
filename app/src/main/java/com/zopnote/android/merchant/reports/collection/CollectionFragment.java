package com.zopnote.android.merchant.reports.collection;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Collection;
import com.zopnote.android.merchant.databinding.CollectionFragBinding;
import com.zopnote.android.merchant.util.Extras;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CollectionFragment extends Fragment {
    private CollectionViewModel viewmodel;
    private CollectionFragBinding collectionFragBinding;
    private CollectionAdapter adapter;
    private String LOG_TAG = "CollectionFragment";

    private String route;

    public CollectionFragment() {
        // Required empty public constructor
    }

    public static CollectionFragment newInstance(String route) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putString(Extras.ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        collectionFragBinding = CollectionFragBinding.inflate(inflater, container, false);
        collectionFragBinding.recyclerView.setHasFixedSize(true);


        adapter = new CollectionAdapter(getActivity());
        adapter.setViewModel(CollectionActivity.obtainViewModel(this.getActivity()));

        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        collectionFragBinding.recyclerView.addItemDecoration(dividerDecoration);
        collectionFragBinding.recyclerView.setAdapter(adapter);

        // hide all
        collectionFragBinding.contentView.setVisibility(View.GONE);
        collectionFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);

        return collectionFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = CollectionActivity.obtainViewModel(getActivity());
        route = getArguments().getString(Extras.ROUTE);

        viewmodel.filterTypeChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean changed) {
                if (changed) {
                    if (viewmodel.apiCallError.getValue() || viewmodel.networkError.getValue()) {
                        //do nothing
                    } else {
                        viewmodel.networkError.setValue(false);
                        setData();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        collectionFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CollectionActivity) getActivity()).getReports();
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
                    collectionFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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
                if (error) {
                    collectionFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
                    setStatusNetworkError();
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewmodel.apiCallError.getValue() || viewmodel.networkError.getValue()) {
            //do nothing
        } else {
            viewmodel.networkError.setValue(false);
            setData();
        }
    }

    private void setData() {
        List collectionForRoute = getCollectionForRoute(route);
        adapter.setItems(collectionForRoute);

        if (collectionForRoute.size() == 0) {
            setStatusEmpty();
        } else {
            setStatusReady();
        }
    }

    private List<Collection> getCollectionForRoute(String route) {
        List list = new ArrayList();
        Date dayFrom = getDayFrom();
        Date dayTo = getDayTo();

        if (!viewmodel.collectionReport.isEmpty()) {

            for (Collection collection : viewmodel.collectionReport) {

                if (collection.getRoute().equalsIgnoreCase(route)) {
                    if (PaymentFilterOption.BILLED.getShortName().equalsIgnoreCase(viewmodel.filterType.getShortName())) {

                        //all
                        if (viewmodel.dayFrom != 0 && viewmodel.dayTo != 0) {
                            if (collection.getInvoiceDate().after(dayFrom) && collection.getInvoiceDate().before(dayTo)){
                                  list.add(collection);
                            }
                        } else {
                            list.add(collection);
                        }

                    } else if (collection.getStatus().equalsIgnoreCase(viewmodel.filterType.getShortName())) {

                        if (!PaymentFilterOption.PENDING.getShortName().equalsIgnoreCase(viewmodel.filterType.getShortName())) {
                            if (viewmodel.dayFrom != 0 && viewmodel.dayTo != 0) {
                                if (collection.getInvoicePaidDate().after(dayFrom) && collection.getInvoicePaidDate().before(dayTo) ){
                                    list.add(collection);
                                }
                            } else {
                                list.add(collection);
                            }
                        }else {
                            if (viewmodel.dayFrom != 0 && viewmodel.dayTo != 0) {
                                if (collection.getInvoiceDate().after(dayFrom) && collection.getInvoiceDate().before(dayTo) ){
                                    list.add(collection);
                                }
                            } else {
                                list.add(collection);
                            }
                        }

                    }
                }
            }
        }
        return list;
    }

    private Date getDayFrom() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, viewmodel.month);
        calendar.set(Calendar.YEAR, viewmodel.year);
        calendar.set(Calendar.DAY_OF_MONTH, viewmodel.dayFrom );
        calendar.add(Calendar.HOUR, -12);
        return calendar.getTime();
    }

    private Date getDayTo() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, viewmodel.month);
        calendar.set(Calendar.YEAR, viewmodel.year);
        calendar.set(Calendar.DAY_OF_MONTH, viewmodel.dayTo);
        calendar.add(Calendar.HOUR, +12);

        return calendar.getTime();
    }

    private void setStatusNetworkError() {
        collectionFragBinding.contentView.setVisibility(View.GONE);
        collectionFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        collectionFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        collectionFragBinding.contentView.setVisibility(View.GONE);
        collectionFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        collectionFragBinding.contentView.setVisibility(View.GONE);
        collectionFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        collectionFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        collectionFragBinding.contentView.setVisibility(View.VISIBLE);
        collectionFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        collectionFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }
}
