package com.zopnote.android.merchant.viewinvoicehistory;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;

public class ViewInvoiceHistoryActivity extends AppCompatActivity {

    private static String LOG_TAG = ViewInvoiceHistoryActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    private String customerId;
    private ViewModelInvoiceHistory viewmodel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.invoice_history_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId);
        viewmodel.customerId=customerId;

        setupViewFragment();




    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static ViewModelInvoiceHistory obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        ViewModelInvoiceHistory viewModel =
                ViewModelProviders.of(activity, factory).get(ViewModelInvoiceHistory.class);

        return viewModel;
    }

    private void setupViewFragment() {
        ViewInvoiceHistoryFragment viewInvoiceHistoryFragment =
                (ViewInvoiceHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);
        if (viewInvoiceHistoryFragment == null) {
            // Create the fragment
            viewInvoiceHistoryFragment = viewInvoiceHistoryFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), viewInvoiceHistoryFragment, R.id.contentView);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_INVOICE_HISTORY, "ViewInvoiceHistory");    }


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