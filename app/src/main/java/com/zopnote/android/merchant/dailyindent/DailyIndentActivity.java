package com.zopnote.android.merchant.dailyindent;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;

import com.zopnote.android.merchant.databinding.DailyIndentActBinding;
import com.zopnote.android.merchant.invoice.editinvoice.EditInvoiceActivity;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.util.Calendar;


public class DailyIndentActivity extends AppCompatActivity {
    private static final String LOG_TAG = "IndentUpdateActivity";
    private DailyIndentActBinding binding;
    private DailyIndentViewModel viewmodel;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.daily_indent_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();


        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    getReports();
                }
            }
        });


        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();

                getReports();

                //to avoid calling api multiple times due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });


        viewmodel.updateQuantityApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {

                if (running) {
                    progressDialog = new ProgressDialog(DailyIndentActivity.this);
                    progressDialog.setMessage(DailyIndentActivity.this.getResources().getString(R.string.update_quantity_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.updateQuantityApiCallRunning.setValue(false);
                    }
                }


            }
        });

        viewmodel.updateQuantityApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(DailyIndentActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.updateQuantityApiCallError.setValue(false);
                }
            }
        });

        viewmodel.updateQuantityApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {

                if (success) {
                    Utils.showSuccessToast(DailyIndentActivity.this,
                            getApplicationContext().getResources().getString(R.string.update_invoice_success_message),
                            Toast.LENGTH_LONG);
                    getReports();
                    viewmodel.updateQuantityApiCallSuccess.setValue(false);
                }
            }
        });

        setupViewFragment();
    }


    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(this)) {

            viewmodel.getIndentReport();
        }else{
            viewmodel.networkError.postValue(true);
        }
    }

    public void updateIndentQuantity() {

        if (NetworkUtil.enforceNetworkConnection(DailyIndentActivity.this)) {
            viewmodel.updateQuantity();
        }
    }
    private void setupViewFragment() {
        DailyIndentFragment dailyIndentFragment =
                (DailyIndentFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (dailyIndentFragment == null) {
            // Create the fragment
            dailyIndentFragment = dailyIndentFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), dailyIndentFragment, R.id.contentFrame);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        setScreenName();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static DailyIndentViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        DailyIndentViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(DailyIndentViewModel.class);

        return viewModel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (viewmodel.isEdited) {
            String message = "You have not saved for the day "+ FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewmodel.calender.getTime());
            String positiveButtonText = DailyIndentActivity.this.getResources().getString(R.string.save_exit_label);
            String negativeButtonText = DailyIndentActivity.this.getResources().getString(R.string.exit_without_save_label);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(NetworkUtil.enforceNetworkConnection(DailyIndentActivity.this)){
                                viewmodel.updateQuantity();
                            }

                            viewmodel.isEdited = false;

                        }
                    })
                    .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DailyIndentActivity.super.onBackPressed();
                        }
                    })
                    .create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }


    private void setScreenName() {

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, "IndentUpdateActivity", "IndentUpdateActivity");
    }
}
