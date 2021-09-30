package com.zopnote.android.merchant.reports.ordersummary;

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
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.databinding.OrderSummaryReportActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;

public class OrderSummaryReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private OrderSummaryReportViewModel viewmodel;
    private OrderSummaryReportActBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_summary_report_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupViewFragment();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        OrderSummaryReportFragment orderSummaryReportFragment = (OrderSummaryReportFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(orderSummaryReportFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT");
            orderSummaryReportFragment = OrderSummaryReportFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), orderSummaryReportFragment, R.id.contentView);
        }
    }

    public static OrderSummaryReportViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        OrderSummaryReportViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(OrderSummaryReportViewModel.class);

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