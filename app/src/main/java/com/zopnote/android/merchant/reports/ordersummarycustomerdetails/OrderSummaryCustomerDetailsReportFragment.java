package com.zopnote.android.merchant.reports.ordersummarycustomerdetails;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.OrdersummaryCustomerDetailsReportFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderSummaryCustomerDetailsReportFragment extends Fragment {

    private OrdersummaryCustomerDetailsReportFragBinding oscdrFragBinding;
    private OrderSummaryCustomerDetailsReportAdapter adapter;
    private OrderSummaryCustomerDetailsReportViewModel viewmodel;
    private ProgressDialog progressDialog;
    private Double readyForSettlement = 0.00;
    private OrderSummaryDatePickerFragment  datePickerFragment;
    private RadioButton radioButton;
    private RadioGroup rg1,rg2;

    public OrderSummaryCustomerDetailsReportFragment() {
        // Required empty public constructor
    }

    public static OrderSummaryCustomerDetailsReportFragment newInstance() {
        OrderSummaryCustomerDetailsReportFragment fragment = new OrderSummaryCustomerDetailsReportFragment();
        Log.d("CSD","ORDER SUMMARY CD REPORT FRAGMENT OSCD");
        //fragment.getActivity().getResources().getString(R.id.customerName);
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

        oscdrFragBinding = OrdersummaryCustomerDetailsReportFragBinding.inflate(inflater, container, false);
        adapter = new OrderSummaryCustomerDetailsReportAdapter(getActivity());
        adapter.setViewModel(OrderSummaryCustomerDetailsReportActivity.obtainViewModel(this.getActivity()));

        oscdrFragBinding.recyclerView.setAdapter(adapter);
        //osrFragBinding.orderSummaryreportitem

        // hide all
        oscdrFragBinding.contentView.setVisibility(View.GONE);
        oscdrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        oscdrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
        oscdrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);

        datePickerFragment = (OrderSummaryDatePickerFragment) getFragmentManager().findFragmentByTag("orderSummaryDatePicker");
        if(datePickerFragment == null){
            datePickerFragment = new OrderSummaryDatePickerFragment();
        }

        oscdrFragBinding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CSD","START CLICKED");
                datePickerFragment.setFlag(OrderSummaryDatePickerFragment.FLAG_START_DATE);
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
                try {
                    Date sd=ft.parse("2020-01-01");
                    datePickerFragment.setStartDate(sd);
                    datePickerFragment.setEndDate(new Date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "orderSummaryDatePicker");
            }
        });

        oscdrFragBinding.endDatePickerLayout.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  datePickerFragment.setFlag(OrderSummaryDatePickerFragment.FLAG_END_DATE);
                  Log.d("CSD","END CLICKED");
                  SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
                  try {
                      Date sd=ft.parse("2020-01-01");
                      datePickerFragment.setStartDate(sd);
                      datePickerFragment.setEndDate(new Date());
                  } catch (ParseException e) {
                      e.printStackTrace();
                  }
                  datePickerFragment.show(getActivity().getSupportFragmentManager(), "orderSummaryDatePicker");
              }
        });
        return oscdrFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rg1 = oscdrFragBinding.radioGroupPeriod;
        rg2 = oscdrFragBinding.radioGroupPeriod1;
        //  rg1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        // rg2.clearCheck();
        rg1.setOnCheckedChangeListener(listener1);
        rg2.setOnCheckedChangeListener(listener2);

        setStatusLoading();

        viewmodel = OrderSummaryCustomerDetailsReportActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();
                Log.d("CSD","OSCDRF ----78");

                setFields();
               // radioGroupPeriodChanged(); //OBSERVE
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
                oscdrFragBinding.radioBtnCustom.setChecked(true);
                if(dateChanged){
                    if(viewmodel.startDateCalender != null){
                        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.startDateCalender.getTime());
                        oscdrFragBinding.startDatePicker.setText(date);
                        if (viewmodel.endDateCalender != null) {
                            Log.d("CSD","START- START DATE CAL"+viewmodel.startDateCalender.getTime().toString());
                            Log.d("CSD","START- END DATE CAL"+viewmodel.endDateCalender.getTime().toString());
                            viewmodel.startDate=viewmodel.startDateCalender.getTimeInMillis();
                            viewmodel.endDate=viewmodel.endDateCalender.getTimeInMillis();
                            viewmodel.selectedPeriod="Custom";
                            getReports();
                        }
                    }
                }
            }
        });

        viewmodel.endDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                oscdrFragBinding.radioBtnCustom.setChecked(true);
                if(dateChanged){
                    if(viewmodel.endDateCalender != null){
                        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.endDateCalender.getTime());
                        oscdrFragBinding.endDatePicker.setText(date);

                        if (viewmodel.startDateCalender != null) {
                            Log.d("CSD", "END - START DATE CAL" + viewmodel.startDateCalender.getTime().toString());
                            Log.d("CSD", "END - END DATE CAL" + viewmodel.endDateCalender.getTime().toString());
                            viewmodel.startDate=viewmodel.startDateCalender.getTimeInMillis();
                            viewmodel.endDate=viewmodel.endDateCalender.getTimeInMillis();
                            viewmodel.selectedPeriod="Custom";
                            getReports();
                        }
                    }
                }
            }
        });

        oscdrFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
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
                    oscdrFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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

                   displayCustomerDetails();
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
                    progressDialog = new ProgressDialog(OrderSummaryCustomerDetailsReportFragment.this.getActivity());
                    progressDialog.setMessage(OrderSummaryCustomerDetailsReportFragment.this.getActivity().getResources().getString(R.string.request_advance));
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
                            OrderSummaryCustomerDetailsReportFragment.this.getActivity().getResources().getString(R.string.requested_advance),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccessReqAdv.setValue(false);

                }
            }
        });

        viewmodel.apiCallErrorReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(OrderSummaryCustomerDetailsReportFragment.this.getActivity(),
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
                    progressDialog = new ProgressDialog(OrderSummaryCustomerDetailsReportFragment.this.getActivity());
                    progressDialog.setMessage(OrderSummaryCustomerDetailsReportFragment.this.getActivity().getResources().getString(R.string.settle_now));
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
            viewmodel.getOrderSummaryCustomerDetailsReport();
        }else{
            viewmodel.networkError.postValue(true);
            oscdrFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

        private void setStatusNetworkError () {
            oscdrFragBinding.contentView.setVisibility(View.GONE);
            oscdrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
            oscdrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.recyclerView.setVisibility(View.VISIBLE);
            oscdrFragBinding.root.setVisibility(View.VISIBLE);
        }

        private void setStatusLoading () {
            oscdrFragBinding.contentView.setVisibility(View.VISIBLE);
            oscdrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
            oscdrFragBinding.recyclerView.setVisibility(View.VISIBLE);
            oscdrFragBinding.root.setVisibility(View.VISIBLE);
        }

        private void setStatusEmpty () {
            //oscdrFragBinding.contentView.setVisibility(View.GONE);
            oscdrFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
            oscdrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.recyclerView.setVisibility(View.GONE);
            oscdrFragBinding.root.setVisibility(View.GONE);
        }

        private void setStatusReady () {
            oscdrFragBinding.contentView.setVisibility(View.VISIBLE);
            oscdrFragBinding.emptyView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.loadingView.getRoot().setVisibility(View.GONE);
            oscdrFragBinding.recyclerView.setVisibility(View.VISIBLE);
            oscdrFragBinding.root.setVisibility(View.VISIBLE);
        }

        private void displayCustomerDetails()
        {
            try {
                Log.d("CSD", "VIEW MODEL ADDRESS LINE1" + viewmodel.addressLine1);
                Log.d("CSD", "VIEW MODEL ADDRESS LINE2" + viewmodel.addressLine2);
                oscdrFragBinding.customerName.setText(viewmodel.customerName);
                oscdrFragBinding.doorNo.setText(viewmodel.doorNo);
                oscdrFragBinding.customerStatus.setText(viewmodel.customerStatus);
                oscdrFragBinding.mobileNumber.setText(viewmodel.mobileNo);
                oscdrFragBinding.route.setText(viewmodel.route);
                oscdrFragBinding.email.setText(viewmodel.email);

                if (viewmodel.addressLine1 != null && viewmodel.addressLine1.trim().length() > 0) {
                    String addressLine1 = Utils.getAddressLine1(getContext(), viewmodel.addressLine1).trim();
                    if( ! addressLine1.isEmpty()){
                        oscdrFragBinding.addressLine1.setText(viewmodel.addressLine1);
                        oscdrFragBinding.addressLine1Layout.findViewById(R.id.addressLine1).setVisibility(View.VISIBLE);
                    }else{
                        oscdrFragBinding.addressLine1Layout.findViewById(R.id.addressLine1).setVisibility(View.GONE);
                    }
                }else {
                    oscdrFragBinding.addressLine1Layout.findViewById(R.id.addressLine1).setVisibility(View.GONE);
                }
                if (viewmodel.addressLine2 != null && viewmodel.addressLine2.trim().length() > 0) {
                    oscdrFragBinding.addressLine2.setText(viewmodel.addressLine2);
                    oscdrFragBinding.addressLine2.findViewById(R.id.addressLine2).setVisibility(View.VISIBLE);
                } else {
                    oscdrFragBinding.addressLine2.findViewById(R.id.addressLine2).setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                Log.d("CSD","ERROR:displayCustomerDetails: "+e.toString());
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

            oscdrFragBinding.totalOrders.setText(Integer.toString((viewmodel.totalOrders)));
            oscdrFragBinding.totalPaidOrders.setText(Integer.toString((viewmodel.totalPaidOrders)));
            oscdrFragBinding.totalUnPaidOrders.setText(Integer.toString((viewmodel.totalUnPaidOrders)));

            oscdrFragBinding.totalPaid.setText((totalPaidAmount));
            oscdrFragBinding.totalUnPaid.setText((totalUnPaid));
            oscdrFragBinding.totalBilled.setText(((totalBilled)));
        }
        catch (Exception e)
        {
            Log.d("CSD","ERROR:displayInvoicesTotal: "+e.toString());
        }
    }

    private void radioGroupPeriodChanged()
    {
        oscdrFragBinding.radioGroupPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedId = oscdrFragBinding.radioGroupPeriod.getCheckedRadioButtonId();
                radioButton = (RadioButton) oscdrFragBinding.radioGroupPeriod.findViewById(selectedId);

                oscdrFragBinding.startDatePicker.setText("Start date");
                oscdrFragBinding.endDatePicker.setText("End date");

                viewmodel.startDateCalender=null;
                viewmodel.endDateCalender=null;

                viewmodel.selectedPeriod = radioButton.getText().toString();
                updateInvoiceItems();
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
                radioButton = (RadioButton) oscdrFragBinding.radioGroupPeriod.findViewById(checkedId);

                oscdrFragBinding.startDatePicker.setText("Start date");
                oscdrFragBinding.endDatePicker.setText("End date");

                viewmodel.startDateCalender=null;
                viewmodel.endDateCalender=null;
                viewmodel.selectedPeriod = radioButton.getText().toString();


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


                Log.e("CSD", "do the work");
            }
        }
    };


    private void updateInvoiceItems() {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getOrderSummaryCustomerDetailsReport();
        }else{
            viewmodel.networkError.postValue(true);
            oscdrFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setFields()
    {

        Log.d("CSD",">>>>> " +viewmodel.main_radioSelected);
        if (viewmodel.main_radioSelected.equals("Custom"))
        {
            long sDate = viewmodel.startDate;
            long eDate = viewmodel.endDate;
            long millisecond = sDate;
            long millisecond1 = eDate;

            String sd = DateFormat.format("d/MM/yyyy", new Date(millisecond)).toString();
            String ed = DateFormat.format("d/MM/yyyy", new Date(millisecond1)).toString();

            oscdrFragBinding.startDatePicker.setText(sd);
            oscdrFragBinding.endDatePicker.setText(ed);

            Log.d("CSD","VIEW MODEL SELECTED INDEX"+viewmodel.main_radioSelectedIndex);
            //RadioGroup  selectedId = oscdrFragBinding.radioGroupPeriod1.findViewById(R.id.radioGroupPeriod1);
            oscdrFragBinding.radioBtnCustom.setChecked(true);
            //int radioButtonId= selectedId.getChildAt(Integer.parseInt(viewmodel.main_radioSelectedIndex)).getId();
            //selectedId.check(0);
        }
        else
        {
            //SET FIELDS FROM ORDER SUMMARY REPORT
            Log.d("CSD","VIEW MODEL SELECTED INDEX----"+viewmodel.main_radioSelectedIndex);
            RadioGroup  selectedId = oscdrFragBinding.radioGroupPeriod.findViewById(R.id.radioGroupPeriod);
            int radioButtonId= selectedId.getChildAt(Integer.parseInt(viewmodel.main_radioSelectedIndex)).getId();
            selectedId.check(radioButtonId);
        }
    }
}