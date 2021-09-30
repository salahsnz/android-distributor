package com.zopnote.android.merchant.addarea;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Utils;

public class AddAreaActivity extends AppCompatActivity {

    private AddAreaViewModel viewmodel;
    private ProgressDialog progressDialog;

    private static final boolean DEBUG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.add_area_act);

        setupToolbar();
        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupViewFragment();

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(AddAreaActivity.this);

                    progressDialog.setMessage(AddAreaActivity.this.getResources().getString(R.string.adding_area_running_message));
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
                    Utils.showFailureToast(AddAreaActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(AddAreaActivity.this,
                            AddAreaActivity.this.getResources().getString(R.string.area_added_success_message),
                            Toast.LENGTH_LONG);
                    //Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    AddAreaActivity.this.finish();
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

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        AddAreaFragment AddAreaFragment = (com.zopnote.android.merchant.addarea.AddAreaFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);

        if(AddAreaFragment == null){
            Log.d("CSD","SETUP VIEW FRAGMENT");
            AddAreaFragment = AddAreaFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), AddAreaFragment, R.id.contentView);
        }
    }

    public static AddAreaViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        AddAreaViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(AddAreaViewModel.class);

        return viewModel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // remember to call our onBackPressed
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
