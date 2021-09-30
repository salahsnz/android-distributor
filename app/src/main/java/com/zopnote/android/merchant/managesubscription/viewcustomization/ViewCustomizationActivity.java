package com.zopnote.android.merchant.managesubscription.viewcustomization;

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
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;

public class ViewCustomizationActivity extends AppCompatActivity {
    private ViewCustomizationViewModel viewmodel;
    private String subscriptionId;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_customization_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(subscriptionId, customerId);

        setupViewFragment();

    }

    private void setupArgs() {
        Subscription subscription = (Subscription) getIntent().getSerializableExtra(Extras.SUBSCRIPTION);
        subscriptionId = subscription.getId();
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        ViewCustomizationFragment viewCustomizationFragment =
                (ViewCustomizationFragment) getSupportFragmentManager().findFragmentById(R.id.contentLayout);
        if (viewCustomizationFragment == null) {
            // Create the fragment
            viewCustomizationFragment = ViewCustomizationFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), viewCustomizationFragment, R.id.contentLayout);
        }
    }

    public static ViewCustomizationViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        ViewCustomizationViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ViewCustomizationViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_CUSTOMIZATION, "ViewCustomizationActivity");

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
