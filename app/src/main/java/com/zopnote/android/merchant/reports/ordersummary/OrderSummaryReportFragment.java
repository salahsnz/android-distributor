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
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.OrderSummarySelectionBinding;
import com.zopnote.android.merchant.databinding.OrdersummaryReportFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSummaryReportFragment extends Fragment {

    private OrdersummaryReportFragBinding osrFragBinding;
    private OrderSummarySelectionBinding ossFragBinding;
    private OrderSummaryReportAdapter adapter;
    private OrderSummaryReportViewModel viewmodel;
    private ProgressDialog progressDialog;
    private Double readyForSettlement = 0.00;
    private OrderSummaryReportDatePicker datePickerFragment;
    private boolean routeLoaded = false;
    private ArrayAdapter<String> routeSuggestionsArrayAdapter;
    private RadioButton radioButton;
    private RadioGroup rg1,rg2;

    public OrderSummaryReportFragment() {
        // Required empty public constructor
    }

    public static OrderSummaryReportFragment newInstance() {
        OrderSummaryReportFragment fragment = new OrderSummaryReportFragment();
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

        osrFragBinding = OrdersummaryReportFragBinding.inflate(inflater, container, false);
        ossFragBinding =OrderSummarySelectionBinding.inflate( inflater,container,false);

        adapter = new OrderSummaryReportAdapter(getActivity());
        adapter.setViewModel(OrderSummaryReportActivity.obtainViewModel(this.getActivity()));

        osrFragBinding.recyclerView.setAdapter(adapter);

        // hide all
        osrFragBinding.contentView.setVisibility(View.GONE);
        osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);

        datePickerFragment = (OrderSummaryReportDatePicker) getFragmentManager().findFragmentByTag("orderSummaryDatePicker");
        if(datePickerFragment == null){
            datePickerFragment = new OrderSummaryReportDatePicker();
        }

        osrFragBinding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CSD","START CLICKED");
                datePickerFragment.setFlag(OrderSummaryReportDatePicker.FLAG_START_DATE);
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");

                try {
                    Date sd=ft.parse("2020-01-01");
                    datePickerFragment.setStartDate(sd);
                    datePickerFragment.setEndDate(new Date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "orderSummaryReportDatePicker");
            }
        });

        osrFragBinding.endDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerFragment.setFlag(OrderSummaryReportDatePicker.FLAG_END_DATE);
                Log.d("CSD","END CLICKED");
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");

                try {
                    Date sd=ft.parse("2020-01-01");//Default start date  set to 1st Jan 2020
                    datePickerFragment.setStartDate(sd);
                    datePickerFragment.setEndDate(new Date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "orderSummaryReportDatePicker");
            }
        });
        return osrFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rg1 = osrFragBinding.radioGroupPeriod;
        rg2 = osrFragBinding.radioGroupPeriod1;
      //  rg1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
       // rg2.clearCheck();
        rg1.setOnCheckedChangeListener(listener1);
        rg2.setOnCheckedChangeListener(listener2);

        setStatusLoading();

        viewmodel = OrderSummaryReportActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();

                //radioGroupPeriodChanged(); //OBSERVE
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

        viewmodel.startDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    osrFragBinding.radioBtnCustom.setChecked(true);
                    if(viewmodel.startDateCalender != null){
                        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.startDateCalender.getTime());
                        osrFragBinding.startDatePicker.setText(date);
                        if (viewmodel.endDateCalender != null) {
                            Log.d("CSD","START- START DATE CAL"+viewmodel.startDateCalender.getTime().toString());
                            Log.d("CSD","START- END DATE CAL"+viewmodel.endDateCalender.getTime().toString());
                            viewmodel.startDate=viewmodel.startDateCalender.getTimeInMillis();
                            viewmodel.endDate=viewmodel.endDateCalender.getTimeInMillis();
                            viewmodel.selectedPeriod="Custom"; //NO NEED
                            getReports();
                        }
                    }
                }
            }
        });

        viewmodel.endDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    osrFragBinding.radioBtnCustom.setChecked(true);
                    if(viewmodel.endDateCalender != null){
                        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.endDateCalender.getTime());
                        osrFragBinding.endDatePicker.setText(date);

                        if (viewmodel.startDateCalender != null) {
                            Log.d("CSD", "END - START DATE CAL" + viewmodel.startDateCalender.getTime().toString());
                            Log.d("CSD", "END - END DATE CAL" + viewmodel.endDateCalender.getTime().toString());
                            viewmodel.startDate=viewmodel.startDateCalender.getTimeInMillis();
                            viewmodel.endDate=viewmodel.endDateCalender.getTimeInMillis();
                            viewmodel.selectedPeriod="Custom"; //NO NEED
                             getReports();
                        }
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


       /* osrFragBinding.radioGroupPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = osrFragBinding.radioGroupPeriod.getCheckedRadioButtonId();
                radioButton = (RadioButton) osrFragBinding.radioGroupPeriod.findViewById(selectedId);

                osrFragBinding.startDatePicker.setText("Start date");
                osrFragBinding.endDatePicker.setText("End date");

                viewmodel.startDateCalender=null;
                viewmodel.endDateCalender=null;
                viewmodel.selectedPeriod = radioButton.getText().toString();

                //RADIO SELECTED INDEX
                int radioButtonID = osrFragBinding.radioGroupPeriod.getCheckedRadioButtonId();
                View radioButton = osrFragBinding.radioGroupPeriod.findViewById(radioButtonID);
                int idx = osrFragBinding.radioGroupPeriod.indexOfChild(radioButton);
                viewmodel.selectedPeriodIndex=idx;

                updateInvoiceItems();
            }
        });*/

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

                    displayInvoicesTotal();

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
                    progressDialog = new ProgressDialog(OrderSummaryReportFragment.this.getActivity());
                    progressDialog.setMessage(OrderSummaryReportFragment.this.getActivity().getResources().getString(R.string.request_advance));
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
                            OrderSummaryReportFragment.this.getActivity().getResources().getString(R.string.requested_advance),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccessReqAdv.setValue(false);

                }
            }
        });

        viewmodel.apiCallErrorReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(OrderSummaryReportFragment.this.getActivity(),
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
                    progressDialog = new ProgressDialog(OrderSummaryReportFragment.this.getActivity());
                    progressDialog.setMessage(OrderSummaryReportFragment.this.getActivity().getResources().getString(R.string.settle_now));
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

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg2.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                rg2.clearCheck(); // clear the second RadioGroup!
                rg2.setOnCheckedChangeListener(listener2); //reset the listener

                //int selectedId = osrFragBinding.radioGroupPeriod.getCheckedRadioButtonId();
                radioButton = (RadioButton) osrFragBinding.radioGroupPeriod.findViewById(checkedId);

                osrFragBinding.startDatePicker.setText("Start date");
                osrFragBinding.endDatePicker.setText("End date");

                viewmodel.startDateCalender=null;
                viewmodel.endDateCalender=null;
                viewmodel.selectedPeriod = radioButton.getText().toString();

                //RADIO SELECTED INDEX
                //int radioButtonID = osrFragBinding.radioGroupPeriod.getCheckedRadioButtonId();
                View radioButton = osrFragBinding.radioGroupPeriod.findViewById(checkedId);
                int idx = osrFragBinding.radioGroupPeriod.indexOfChild(radioButton);

                if (viewmodel.selectedPeriod.equalsIgnoreCase("Custom"))
                    viewmodel.selectedPeriodIndex=4;

                viewmodel.selectedPeriodIndex=idx;

                Log.e("CSD", "do the work "+checkedId+ " "+idx);

                updateInvoiceItems();

            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg1.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg1.setOnCheckedChangeListener(listener1);

                viewmodel.selectedPeriod = "Custom";
                viewmodel.selectedPeriodIndex = 4;
            }
        }
    };

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
            osrFragBinding.recyclerView.setVisibility(View.VISIBLE);
        }

        private void setStatusLoading () {
            osrFragBinding.contentView.setVisibility(View.VISIBLE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
            osrFragBinding.recyclerView.setVisibility(View.VISIBLE);
        }

        private void setStatusEmpty () {
            //osrFragBinding.contentView.setVisibility(View.GONE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
            osrFragBinding.recyclerView.setVisibility(View.GONE);
        }

        private void setStatusReady () {
            osrFragBinding.contentView.setVisibility(View.VISIBLE);
            osrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            osrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            osrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
            osrFragBinding.recyclerView.setVisibility(View.VISIBLE);
        }

    private void prepareRoutesSpinner(List<String> merchantRoutes) {
        final ArrayList<String> routeSuggestions = new ArrayList<>();

        /*for (String route: merchantRoutes) {
            if( ! routeSuggestions.contains(route)){
                routeSuggestions.add(route);
            }
        }*/

       /* routeSuggestionsArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, routeSuggestions);
        routeSuggestionsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        osrFragBinding.route.setAdapter(new NothingSelectedSpinnerAdapter(
                routeSuggestionsArrayAdapter,
                R.layout.spinner_row_nothing_selected,
                getContext()));

        if(viewmodel.route != null){
            int index = routeSuggestionsArrayAdapter.getPosition(viewmodel.route);
            if(index >= 0){
                osrFragBinding.route.setSelection(index+1);
            }
        }

        osrFragBinding.radioGroupRoute.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int selectedId = osrFragBinding.radioGroupRoute.getCheckedRadioButtonId();
            radioButton = (RadioButton) osrFragBinding.radioGroupRoute.findViewById(selectedId);

            if (radioButton.getText().toString().equals("Select Route:"))
                osrFragBinding.route.setVisibility(View.VISIBLE);
            else
                osrFragBinding.route.setVisibility(View.INVISIBLE);
        }
});*/

      /*  osrFragBinding.route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                Log.d("CSD","SPINNER SELECTED "+selectedText);
                if(selectedText != null) {
                    viewmodel.route = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        //routeLoaded = true;
    }

    private void updateInvoiceItems() {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getOrderSummaryReport();
        }else{
            viewmodel.networkError.postValue(true);
            osrFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void displayInvoicesTotal()
    {
        try {

            String totalBilled = FormatUtil.getRupeePrefixedAmount(getContext(), (double) viewmodel.totalBilled,
                    FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

            String totalPaidAmount = FormatUtil.getRupeePrefixedAmount(getContext(), (double) viewmodel.totalPaidAmount,
                    FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

            String totalUnPaid = FormatUtil.getRupeePrefixedAmount(getContext(), (double) viewmodel.totalUnPaidAmount,
                    FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

            osrFragBinding.totalPaid.setText((totalPaidAmount));
            osrFragBinding.totalUnPaid.setText((totalUnPaid));
            osrFragBinding.totalBilled.setText(((totalBilled)));

            osrFragBinding.totalOrders.setText(Integer.toString((viewmodel.totalOrders)));
            osrFragBinding.totalPaidOrders.setText(Integer.toString((viewmodel.totalPaidOrders)));
            osrFragBinding.totalUnPaidOrders.setText(Integer.toString((viewmodel.totalUnPaidOrders)));
        }
        catch (Exception e)
        {
            Log.d("CSD","ERROR:displayInvoicesTotal: "+e.toString());
        }
    }
}