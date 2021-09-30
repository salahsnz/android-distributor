package com.zopnote.android.merchant.activatemerchant;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Utils;

public class ActivateMerchantActivity extends AppCompatActivity {
    private String title = null;
    private String url = null;
    private String content = null;
    private WebView webView = null;
    private String cta = null;
    private String ctaLink = null;
    private ActionBar ab;
    private ProgressBar progressBar;
    private Bundle stateBundle;
    private boolean loadRequired = true;
    private ActivateMerchantViewModel viewmodel;
    private ProgressDialog progressDialog;

    private static final boolean DEBUG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activate_merchant_act);

         Log.d("CSD","ACTIVATE MERCHANT ACTIVITY");

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupViewFragment();

        if (savedInstanceState != null) {
            stateBundle = savedInstanceState;
        } else {
            stateBundle = getIntent().getExtras();
        }

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ActivateMerchantActivity.this);
                    progressDialog.setMessage(ActivateMerchantActivity.this.getResources().getString(R.string.activating_merchant_running_message));
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
                    Utils.showFailureToast(ActivateMerchantActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ActivateMerchantActivity.this,
                            ActivateMerchantActivity.this.getResources().getString(R.string.merchant_activated_success_message),
                            Toast.LENGTH_LONG);
                    //Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    ActivateMerchantActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                System.out.println("Merchant id: " + merchant.getId());
            }
        });
    }

    private void setupViewFragment() {
        ActivateMerchantFragment ActivateMerchantFragment = (ActivateMerchantFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(ActivateMerchantFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT");
            ActivateMerchantFragment = ActivateMerchantFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), ActivateMerchantFragment, R.id.contentView);
        }
    }

    public static ActivateMerchantViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        Log.d("CSD","------");
        //Add ActivateMerchantViewModel in ViewModelFactory
        ActivateMerchantViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ActivateMerchantViewModel.class);

        return viewModel;
    }
}
