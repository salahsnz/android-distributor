package com.zopnote.android.merchant.managesubscription;

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
import com.zopnote.android.merchant.editdraftinvoice.EditDraftInvoiceActivity;
import com.zopnote.android.merchant.event.RefreshDraftInvoiceEvent;
import com.zopnote.android.merchant.event.RefreshDraftReportEvent;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;

import org.greenrobot.eventbus.EventBus;

public class ManageSubscriptionsActivity extends AppCompatActivity {
    private ManageSubscriptionsViewModel viewmodel;
    private String customerId;
    private String callingActivityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_subscriptions_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId);

        setupViewFragment();

    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        callingActivityClass = getIntent(). getStringExtra(Extras.CALLING_ACTIVITY_CLASS);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        ManageSubscriptionsFragment manageSubscriptionsFragment =
                (ManageSubscriptionsFragment) getSupportFragmentManager().findFragmentById(R.id.contentLayout);
        if (manageSubscriptionsFragment == null) {
            // Create the fragment
            manageSubscriptionsFragment = ManageSubscriptionsFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), manageSubscriptionsFragment, R.id.contentLayout);
        }
    }

    public static ManageSubscriptionsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        ManageSubscriptionsViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ManageSubscriptionsViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_CUSTOMER, "ManageSubscriptionsActivity");

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

    public void updateDraftInvoiceReportIfNeeded() {
        if(callingActivityClass != null){
            if(callingActivityClass.equalsIgnoreCase(EditDraftInvoiceActivity.class.getSimpleName()));{

                if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftInvoiceEvent.class)){
                    EventBus.getDefault().post(new RefreshDraftInvoiceEvent());
                }

                if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftReportEvent.class)){
                    EventBus.getDefault().post(new RefreshDraftReportEvent());
                }
            }
        }
    }
}
