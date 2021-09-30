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
import com.zopnote.android.merchant.util.Extras;

public class ProductSetupActivity extends AppCompatActivity {
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
    private ProductSetupViewModel viewmodel;
    private ProgressDialog progressDialog;

    private static final boolean DEBUG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.product_setup_act);

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupViewFragment();

        viewmodel.productType = getIntent().getStringExtra(Extras.SERVICE_TYPE);

        Log.d("CSD","Product ACTIVITY>>>> "+ viewmodel.productType);

        if (savedInstanceState != null) {
            stateBundle = savedInstanceState;
        } else {
            stateBundle = getIntent().getExtras();
        }
    }

    private void setupViewFragment() {
        ProductSetupFragment ProductSetupFragment = (com.zopnote.android.merchant.merchantsetup.ProductSetupFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(ProductSetupFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT");
            ProductSetupFragment = ProductSetupFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), ProductSetupFragment, R.id.contentView);
        }
    }

    public static ProductSetupViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        Log.d("CSD","------");
        //Add AddRouteViewModel in ViewModelFactory
        ProductSetupViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ProductSetupViewModel.class);

        return viewModel;
    }
}
