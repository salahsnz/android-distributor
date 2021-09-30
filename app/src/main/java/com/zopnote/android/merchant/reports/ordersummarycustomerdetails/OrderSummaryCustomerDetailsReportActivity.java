package com.zopnote.android.merchant.reports.ordersummarycustomerdetails;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsReportActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;

public class OrderSummaryCustomerDetailsReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private OrderSummaryCustomerDetailsReportViewModel viewmodel;
    private OrderSummaryCustomerDetailsReportActBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_summary_customer_details_report_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        setupArgs();
        setupViewFragment();
    }
    private void setupArgs() {
        viewmodel.invoiceNo = getIntent().getStringExtra(Extras.INVOICE_NO);
        viewmodel.customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        viewmodel.merchantId = getIntent().getStringExtra(Extras.MERCHANT_ID);
        viewmodel.customerName = getIntent().getStringExtra(Extras.CUSTOMER_NAME);

        viewmodel.customerStatus = getIntent().getStringExtra(Extras.CUSTOMER_STATUS);
        viewmodel.doorNo = getIntent().getStringExtra(Extras.DOOR_NUMBER);

        viewmodel.addressLine1 = getIntent().getStringExtra(Extras.ADDRESS_LINE1);
        viewmodel.addressLine2 = getIntent().getStringExtra(Extras.ADDRESS_LINE2);

        viewmodel.mobileNo = getIntent().getStringExtra(Extras.MOBILE_NO);
        viewmodel.route = getIntent().getStringExtra(Extras.ROUTE);
        viewmodel.email = getIntent().getStringExtra(Extras.EMAIL);

        viewmodel.main_radioSelected = getIntent().getStringExtra(Extras.RADIO_SELECTED);
        viewmodel.main_radioSelectedIndex = getIntent().getStringExtra(Extras.RADIO_SELECTED_INDEX);
        viewmodel.main_startDateSelected = getIntent().getStringExtra(Extras.START_DATE);
        viewmodel.main_endDateSelected = getIntent().getStringExtra(Extras.END_DATE);

      //  viewmodel.selectedPeriod=viewmodel.main_radioSelected;
        viewmodel.startDate= Long.parseLong(viewmodel.main_startDateSelected);
        viewmodel.endDate= Long.parseLong(viewmodel.main_endDateSelected);

        Log.d("CSD","RADIO SELECTEDINDEX="+viewmodel.main_radioSelectedIndex);
        Log.d("CSD","START DATE SELECTED="+viewmodel.main_startDateSelected);
        Log.d("CSD","END DATE SELECTED="+viewmodel.main_endDateSelected);
        Log.d("CSD","RADIO SELECTED="+viewmodel.main_radioSelected);
    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        OrderSummaryCustomerDetailsReportFragment orderSummaryCustomerDetailsReportFragment = (OrderSummaryCustomerDetailsReportFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(orderSummaryCustomerDetailsReportFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT OSCD");

            orderSummaryCustomerDetailsReportFragment = OrderSummaryCustomerDetailsReportFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), orderSummaryCustomerDetailsReportFragment, R.id.contentView);
        }
    }

    public static OrderSummaryCustomerDetailsReportViewModel obtainViewModel(FragmentActivity activity) {

        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        OrderSummaryCustomerDetailsReportViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(OrderSummaryCustomerDetailsReportViewModel.class);
        Log.d("CSD","OSCD");
        return viewModel;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_ORDERSUMMARY, "OrderSummaryReportActivity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}