package com.zopnote.android.merchant.editdraftinvoice;

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
import com.zopnote.android.merchant.event.RefreshDraftInvoiceEvent;
import com.zopnote.android.merchant.event.RefreshDraftReportEvent;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditDraftInvoiceActivity extends AppCompatActivity {
    private String customerId;
    private String invoiceId;
    private EditDraftInvoiceViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_draft_invoice_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId, invoiceId);

        setupViewFragment();

        EventBus.getDefault().register(this);
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        if(getIntent().hasExtra(Extras.INVOICE_ID)){
            invoiceId = getIntent().getStringExtra(Extras.INVOICE_ID);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static EditDraftInvoiceViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        EditDraftInvoiceViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(EditDraftInvoiceViewModel.class);

        return viewModel;
    }

    private void setupViewFragment() {
        EditDraftInvoiceFragment editDraftInvoiceFragment = (EditDraftInvoiceFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);
        if(editDraftInvoiceFragment == null){
            editDraftInvoiceFragment = EditDraftInvoiceFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), editDraftInvoiceFragment, R.id.contentView);
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.EDIT_DRAFT_INVOICE, "EditDraftInvoiceActivity");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshDraftInvoiceEvent(RefreshDraftInvoiceEvent event) {
        if(event != null){
            viewmodel.getInvoice();
        }
    }

    public void updateDraftInvoiceReportIfApplicable() {
        if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftReportEvent.class)){
            EventBus.getDefault().post(new RefreshDraftReportEvent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
