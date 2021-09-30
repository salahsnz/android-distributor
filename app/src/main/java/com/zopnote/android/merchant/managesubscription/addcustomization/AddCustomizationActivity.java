package com.zopnote.android.merchant.managesubscription.addcustomization;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;

public class AddCustomizationActivity extends AppCompatActivity {
    private Subscription subscription;
    private AddCustomizationViewModel viewmodel;
    private ProgressDialog progressDialog;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customization_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(subscription.getId(), customerId);

        setupViewFragment();

        setupApiCallObservers();
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        subscription = (Subscription) getIntent().getSerializableExtra(Extras.SUBSCRIPTION);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        AddCustomizationFragment addCustomizationFragment =
                (AddCustomizationFragment) getSupportFragmentManager().findFragmentById(R.id.contentLayout);
        if (addCustomizationFragment == null) {
            // Create the fragment
            addCustomizationFragment = AddCustomizationFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), addCustomizationFragment, R.id.contentLayout);
        }
    }

    public static AddCustomizationViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        AddCustomizationViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(AddCustomizationViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ADD_CUSTOMIZATION, "AddCustomizationActivity");

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

    private void setupApiCallObservers() {

        viewmodel.customSubscriptionApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(AddCustomizationActivity.this);
                    progressDialog.setMessage(AddCustomizationActivity.this.getResources().getString(R.string.update_subscription_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.customSubscriptionApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.customSubscriptionApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(AddCustomizationActivity.this,
                            AddCustomizationActivity.this.getResources().getString(R.string.update_subscription_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.customSubscriptionApiCallSuccess.setValue(false);
                    AddCustomizationActivity.this.finish();
                }
            }
        });

        viewmodel.customSubscriptionApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(AddCustomizationActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.customSubscriptionApiCallError.setValue(false);
                }
            }
        });
    }

}
