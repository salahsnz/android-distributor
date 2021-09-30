package com.zopnote.android.merchant.merchantsetup;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.util.ActivityUtils;

public class MerchantBankInfoActivity extends AppCompatActivity {
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
    private MerchantBankInfoViewModel viewmodel;
    private ProgressDialog progressDialog;

    private static final boolean DEBUG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.merchant_bank_info_act);

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupViewFragment();

        if (savedInstanceState != null) {
            stateBundle = savedInstanceState;
        } else {
            stateBundle = getIntent().getExtras();
        }

        /*viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(MerchantBankInfoActivity.this);
                    progressDialog.setMessage(MerchantBankInfoActivity.this.getResources().getString(R.string.adding_route_running_message));
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
                    Utils.showFailureToast(MerchantBankInfoActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(MerchantBankInfoActivity.this,
                            MerchantBankInfoActivity.this.getResources().getString(R.string.route_added_success_message),
                            Toast.LENGTH_LONG);
                    //Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    MerchantBankInfoActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                System.out.println("Merchant id: " + merchant.getId());
            }
        });*/
    }

    private void setupViewFragment() {
        MerchantBankInfoFragment MerchantBankInfoFragment = (MerchantBankInfoFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(MerchantBankInfoFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT");
            MerchantBankInfoFragment = MerchantBankInfoFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), MerchantBankInfoFragment, R.id.contentView);
        }
    }

    public static MerchantBankInfoViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        Log.d("CSD","------");
        //Add AddRouteViewModel in ViewModelFactory
        MerchantBankInfoViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(MerchantBankInfoViewModel.class);

        return viewModel;
    }
}
