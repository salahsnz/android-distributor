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

public class ShopSetupActivity extends AppCompatActivity {
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
    private ShopSetupViewModel viewmodel;
    private ProgressDialog progressDialog;

    private static final boolean DEBUG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.shop_setup_act);

         Log.d("CSD","SHOP ACTIVITY");

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
                    progressDialog = new ProgressDialog(ShopSetupActivity.this);
                    progressDialog.setMessage(ShopSetupActivity.this.getResources().getString(R.string.adding_route_running_message));
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
                    Utils.showFailureToast(ShopSetupActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ShopSetupActivity.this,
                            ShopSetupActivity.this.getResources().getString(R.string.route_added_success_message),
                            Toast.LENGTH_LONG);
                    //Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    ShopSetupActivity.this.finish();
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
        ShopSetupFragment ShopSetupFragment = (ShopSetupFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(ShopSetupFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT");
            ShopSetupFragment = ShopSetupFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), ShopSetupFragment, R.id.contentView);
        }
    }

    public static ShopSetupViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        //Add AddRouteViewModel in ViewModelFactory
        ShopSetupViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ShopSetupViewModel.class);

        return viewModel;
    }
}
