package com.zopnote.android.merchant.reports.ordersummary;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.OrderSummarySelectionBinding;
import com.zopnote.android.merchant.databinding.OrdersummaryReportFragBinding;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.NothingSelectedSpinnerAdapter;
import com.zopnote.android.merchant.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class OrderSummarySelectionReportFragment extends Fragment {

    private OrdersummaryReportFragBinding osrFragBinding;
    private OrderSummarySelectionBinding ossFragBinding;
    private OrderSummaryReportAdapter adapter;
    private OrderSummaryReportViewModel viewmodel;
    private ProgressDialog progressDialog;
    private Double readyForSettlement = 0.00;
    private boolean routeLoaded = false;
    private ArrayAdapter<String> routeSuggestionsArrayAdapter;

    public OrderSummarySelectionReportFragment() {
        // Required empty public constructor
    }

    public static OrderSummarySelectionReportFragment newInstance() {
        OrderSummarySelectionReportFragment fragment = new OrderSummarySelectionReportFragment();
        Log.d("CSD","ORDER SUMMARY REPORT FRAGMENT");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //osrFragBinding = OrdersummaryReportFragBinding.inflate(getLayoutInflater(), container, false);

        osrFragBinding = OrdersummaryReportFragBinding.inflate(inflater, container, false);
        ossFragBinding =OrderSummarySelectionBinding.inflate( inflater,container,false);


        adapter = new OrderSummaryReportAdapter(getActivity());
        adapter.setViewModel(OrderSummaryReportActivity.obtainViewModel(this.getActivity()));

        osrFragBinding.recyclerView.setAdapter(adapter);
        //osrFragBinding.orderSummaryreportitem

        // hide all
        osrFragBinding.contentView.setVisibility(View.GONE);
        osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);

        return osrFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = OrderSummaryReportActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();
                Log.d("CSD","OSRF ----90");

                if( ! routeLoaded){
                   // prepareRoutesSpinner(merchant.getRoutes());
                }

                getReports();

               // to avoid calling api without merchant Id, also avoid multiple calls due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged) {
                    if (viewmodel.merchantId != null) {
                        getReports();
                    }
                }
            }
        });

        osrFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReports();
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.apiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    osrFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {

                if (success) {
                    adapter.setItems(viewmodel.customers);
                    adapter.notifyDataSetChanged();

                    if(viewmodel.customers.size() == 0){
                        setStatusEmpty();
                    }else{
                        setStatusReady();
                    }
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });


        viewmodel.apiCallRunningReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(OrderSummarySelectionReportFragment.this.getActivity());
                    progressDialog.setMessage(OrderSummarySelectionReportFragment.this.getActivity().getResources().getString(R.string.request_advance));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.apiCallRunningReqAdv.setValue(false);
                    }
                }
            }
        });

        viewmodel.apiCallSuccessReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            OrderSummarySelectionReportFragment.this.getActivity().getResources().getString(R.string.requested_advance),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccessReqAdv.setValue(false);

                }
            }
        });

        viewmodel.apiCallErrorReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(OrderSummarySelectionReportFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallErrorReqAdv.setValue(false);
                }
            }
        });



        viewmodel.apiCallRunningSettleNow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(OrderSummarySelectionReportFragment.this.getActivity());
                    progressDialog.setMessage(OrderSummarySelectionReportFragment.this.getActivity().getResources().getString(R.string.settle_now));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.apiCallRunningSettleNow.setValue(false);
                    }
                }
            }
        });


    }
    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getOrderSummaryReport();
            //viewmodel.getMerchantRoutes();
        }else{
            viewmodel.networkError.postValue(true);
            osrFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

        private void setStatusNetworkError () {
            osrFragBinding.contentView.setVisibility(View.GONE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        }

        private void setStatusLoading () {
            osrFragBinding.contentView.setVisibility(View.VISIBLE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
        }

        private void setStatusEmpty () {
            osrFragBinding.contentView.setVisibility(View.GONE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        }

        private void setStatusReady () {
            osrFragBinding.contentView.setVisibility(View.VISIBLE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        }

    private void prepareRoutesSpinner(List<String> merchantRoutes) {
        final ArrayList<String> routeSuggestions = new ArrayList<>();

        for (String route: merchantRoutes) {
            if( ! routeSuggestions.contains(route)){
                routeSuggestions.add(route);
            }
        }

        routeSuggestionsArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, routeSuggestions);
        routeSuggestionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ossFragBinding.route.setAdapter(new NothingSelectedSpinnerAdapter(
                routeSuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                getContext()));

        if(viewmodel.route != null){
            int index = routeSuggestionsArrayAdapter.getPosition(viewmodel.route);
            if(index >= 0){
                ossFragBinding.route.setSelection(index+1);
            }
        }

        ossFragBinding.route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(selectedText != null) {
                    viewmodel.route = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        routeLoaded = true;
    }
}