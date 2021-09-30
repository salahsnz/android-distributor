package com.zopnote.android.merchant.reports.onboarding;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.databinding.OnboardFragBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardFragment extends Fragment {
    private OnboardFragBinding binding;
    private OnboardViewModel viewmodel;
    private OnboardReportAdapter adapter;

    public OnboardFragment() {
        // Required empty public constructor
    }

    public static OnboardFragment newInstance() {
        OnboardFragment fragment = new OnboardFragment();
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
        binding = OnboardFragBinding.inflate(getLayoutInflater(), container, false);

        adapter = new OnboardReportAdapter(getActivity());

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

        viewmodel = OnboardActivity.obtainViewModel(getActivity());

        viewmodel.customers.observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable List<Customer> customers) {
                List onboardingList = getOnboardingReport(customers);
                adapter.setItems(onboardingList);
                adapter.notifyDataSetChanged();

                if(onboardingList.size() == 0){
                    setStatusEmpty();
                }else{
                    setStatusReady();
                }
            }
        });
    }

    private List getOnboardingReport(List<Customer> customers) {
        Map<String, OnboardInfo> stringOnboardMap = new HashMap<>();

        OnboardInfo onboardInfoSummary = new OnboardInfo();
        onboardInfoSummary.setRoute("All");

        for (Customer customer:customers) {
            String route = customer.getRoute();
            boolean hasName = hasName(customer); //unused
            boolean hasMobileNumber = hasMobileNumber(customer);

            OnboardInfo onboardInfo;
            if(stringOnboardMap.containsKey(route)){
                onboardInfo = stringOnboardMap.get(route);
            }else{
                onboardInfo = new OnboardInfo();
                onboardInfo.setRoute(route);
            }
            onboardInfo.setTotalCustomers(onboardInfo.getTotalCustomers() + 1 );
            onboardInfoSummary.setTotalCustomers(onboardInfoSummary.getTotalCustomers() + 1 );

            if(hasMobileNumber){
                onboardInfo.setNameOrMobileAvailableCount(onboardInfo.getNameOrMobileAvailableCount() + 1);
                onboardInfoSummary.setNameOrMobileAvailableCount(onboardInfoSummary.getNameOrMobileAvailableCount() + 1);
            }else{
                onboardInfo.setNameOrMobileNumberNotAddedCount(onboardInfo.getNameOrMobileNumberNotAddedCount() + 1);
                onboardInfoSummary.setNameOrMobileNumberNotAddedCount(onboardInfoSummary.getNameOrMobileNumberNotAddedCount() + 1);
            }
            if (customer.getActive()){
                onboardInfo.setActiveCount(onboardInfo.getActiveCount() + 1);
                onboardInfoSummary.setActiveCount(onboardInfoSummary.getActiveCount() + 1);
            }else {
                onboardInfo.setInactiveCount(onboardInfo.getInactiveCount() + 1);
                onboardInfoSummary.setInactiveCount(onboardInfoSummary.getInactiveCount() + 1);
            }
            stringOnboardMap.put(route, onboardInfo);
        }

        List onboardingList = new ArrayList(stringOnboardMap.values());

        onboardingList.add(onboardInfoSummary);

        return onboardingList;
    }

    private boolean hasMobileNumber(Customer customer) {
        String mobileNumber = getDisplayMobileNumber(customer);
        if( !mobileNumber.isEmpty()){
            if(mobileNumber.matches("^[6789]\\d{9}$")) {
                return true;
            }
        }
        return false;
    }

    private String getDisplayMobileNumber(Customer customer) {
        return customer.getMobileNumber().replaceAll("^\\+91", "");
    }

    private boolean hasName(Customer customer) {
        if((customer.getFirstName() != null && ! customer.getFirstName().isEmpty()
                ||  (customer.getLastName() != null && ! customer.getLastName().isEmpty()))){
            return true;
        }
        return false;
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
