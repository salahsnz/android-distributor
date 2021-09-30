package com.zopnote.android.merchant.movecustomer;

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
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;

public class MoveCustomerActivity extends AppCompatActivity {
    private String customerId;
    private String route;
    private MoveCustomerViewModel viewmodel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_customer_act);

        setupToolbar();

        getArgs();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId, route);

        setupViewFragment();

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                //required to get value
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(MoveCustomerActivity.this);
                    progressDialog.setMessage(getResources().getString(R.string.add_customer_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(MoveCustomerActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(MoveCustomerActivity.this,
                            getResources().getString(R.string.move_customer_success_message),
                            Toast.LENGTH_LONG);
                    finish();
                }
            }
        });

    }

    private void getArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        route = getIntent().getStringExtra(Extras.ROUTE);
    }

    public static MoveCustomerViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        MoveCustomerViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(MoveCustomerViewModel.class);

        return viewModel;
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        MoveCustomerFragment moveCustomerFragment =
                (MoveCustomerFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (moveCustomerFragment == null) {
            // Create the fragment
            moveCustomerFragment = MoveCustomerFragment.newInstance(customerId, route);
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), moveCustomerFragment, R.id.contentFrame);
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
}
