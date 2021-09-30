package com.zopnote.android.merchant.indent;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.DailySubscription;
import com.zopnote.android.merchant.databinding.IndentFragBinding;
import com.zopnote.android.merchant.reports.subscription.HeaderItemDecoration;
import com.zopnote.android.merchant.reports.subscription.RouteHeader;
import com.zopnote.android.merchant.util.Extras;

import java.util.ArrayList;
import java.util.List;

public class IndentFragment extends Fragment {
    private IndentViewModel viewmodel;
    private IndentFragBinding indentFragBinding;
    private IndentAdapter adapter;
    private String LOG_TAG = "IndentFragment";

    private String route;

    public IndentFragment() {
        // Required empty public constructor
    }

    public static IndentFragment newInstance(String route) {
        IndentFragment fragment = new IndentFragment();
        Bundle args = new Bundle();
        args.putString(Extras.ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        indentFragBinding = IndentFragBinding.inflate(inflater, container,false);
        indentFragBinding.recyclerView.setHasFixedSize(true);



        adapter = new IndentAdapter(getActivity());
        adapter.setViewModel(IndentActivity.obtainViewModel(this.getActivity()));

        RecyclerView.ItemDecoration headerItemDecoration = new HeaderItemDecoration(indentFragBinding.recyclerView, adapter);
        indentFragBinding.recyclerView.addItemDecoration(headerItemDecoration);
        indentFragBinding.recyclerView.setAdapter(adapter);

        // hide all
        indentFragBinding.contentView.setVisibility(View.GONE);
        indentFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        indentFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        indentFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);

        return indentFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = IndentActivity.obtainViewModel(getActivity());
        route = getArguments().getString(Extras.ROUTE);

        viewmodel.indentTypeChanged.observe(this, new Observer<Boolean>() {
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

        indentFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IndentActivity)getActivity()).getReports();
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
                    indentFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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
                    indentFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
                    setStatusNetworkError();
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if( viewmodel.apiCallError.getValue() || viewmodel.networkError.getValue()){
            //do nothing
        }else{
            viewmodel.networkError.setValue(false);
            setData();
        }
    }

    private void setData() {
        List indentForRoute = getIndentForRoute(route);
        adapter.setItems(indentForRoute);

        if(indentForRoute.size() == 0){
            setStatusEmpty();
        }else{
            setStatusReady();
        }
    }

    private List<DailySubscription> getIndentForRoute(String route) {
        List indentList = new ArrayList();

        if( ! viewmodel.indentReport.isEmpty()){

            List summaryAtTopList = new ArrayList();
            List itemsList = new ArrayList();

            String[] previousAddressLine2Header = new String[1];

            for (DailySubscription dailySubscription: viewmodel.indentReport) {

                if(dailySubscription.getRoute().equalsIgnoreCase(route)){
                    filterListForIndentType(dailySubscription, previousAddressLine2Header, itemsList, summaryAtTopList);
                }
            }
            indentList.addAll(summaryAtTopList);
            indentList.addAll(itemsList);
        }
        return indentList;
    }

    private void filterListForIndentType(DailySubscription dailySubscription, String[] previousAddressLine2Header, List itemsList, List summaryAtTopList) {
        if(viewmodel.indentType.equalsIgnoreCase("changes")) {
            //indent type "changes" -> paused
            if(dailySubscription.getPauseCount() > 0){
                if(dailySubscription.getAddressLine2().equalsIgnoreCase("All")){
                    addToList(dailySubscription, previousAddressLine2Header, summaryAtTopList);
                }else{
                    addToList(dailySubscription, previousAddressLine2Header, itemsList);
                }
            }
        }else{
            //indent type "all"
            if(dailySubscription.getAddressLine2().equalsIgnoreCase("All")){
                addToList(dailySubscription, previousAddressLine2Header, summaryAtTopList);
            }else{
                addToList(dailySubscription, previousAddressLine2Header, itemsList);
            }
        }
    }

    private void addToList(DailySubscription dailySubscription, String[] previousAddressLine2Header, List itemsList) {

        String currentAddressLine2 = dailySubscription.getAddressLine2();
        if( ! currentAddressLine2.equalsIgnoreCase(previousAddressLine2Header[0])){
            addHeader(currentAddressLine2, itemsList);
            previousAddressLine2Header[0] = currentAddressLine2;
        }

        itemsList.add(dailySubscription);
    }

    private void addHeader(String currentAddressLine2, List summaryList) {
        RouteHeader routeHeader = new RouteHeader();
        routeHeader.setName(currentAddressLine2);
        summaryList.add(routeHeader);
    }

    private void setStatusNetworkError() {
        indentFragBinding.contentView.setVisibility(View.GONE);
        indentFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        indentFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        indentFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        indentFragBinding.contentView.setVisibility(View.GONE);
        indentFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        indentFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        indentFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        indentFragBinding.contentView.setVisibility(View.GONE);
        indentFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        indentFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        indentFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        indentFragBinding.contentView.setVisibility(View.VISIBLE);
        indentFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        indentFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        indentFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }
}
