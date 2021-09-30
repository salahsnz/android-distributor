package com.zopnote.android.merchant.products.editproduct;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.EditProductActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;

public class EditProductActivity extends AppCompatActivity {
    private EditProductActBinding binding;
    private EditProductViewModel viewmodel;
    private Product product;
    private boolean addProduct;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.edit_product_act);

        setupArgs();

        if(addProduct) {
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }

        viewmodel = obtainViewModel(this);
        viewmodel.init(product, addProduct);

        setupToolbar();

        setupViewFragment();

        if(addProduct){
            setTitle(R.string.title_activity_add_product);
        }else{
            setTitle(R.string.title_activity_edit_product);
        }

        setupObservers();
    }

    private void setupArgs() {
        product = (Product) getIntent().getSerializableExtra(Extras.PRODUCT);
        addProduct = getIntent().getBooleanExtra(Extras.ADD_PRODUCT, false);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        EditProductFragment editProductFragment =
                (EditProductFragment) getSupportFragmentManager().findFragmentById(R.id.contentLayout);
        if (editProductFragment == null) {
            // Create the fragment
            editProductFragment = EditProductFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), editProductFragment, R.id.contentLayout);
        }
    }

    private void setupObservers() {

        //api observers
        viewmodel.updateProductApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditProductActivity.this);
                    if(addProduct){
                        progressDialog.setMessage(EditProductActivity.this.getResources().getString(R.string.add_product_running_message));
                    }else{
                        progressDialog.setMessage(EditProductActivity.this.getResources().getString(R.string.update_product_running_message));
                    }
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.updateProductApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.updateProductApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    String message;
                    if(addProduct){
                        message = EditProductActivity.this.getResources().getString(R.string.add_product_success_message);
                    }else{
                        message =  EditProductActivity.this.getResources().getString(R.string.update_product_success_message);
                    }
                    Utils.showSuccessToast(EditProductActivity.this,
                            message,
                            Toast.LENGTH_LONG);
                    viewmodel.updateProductApiCallSuccess.setValue(false);
                    EditProductActivity.this.finish();
                }
            }
        });

        viewmodel.updateProductApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditProductActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.updateProductApiCallError.setValue(false);
                }
            }
        });
    }

    public static EditProductViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        EditProductViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(EditProductViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.EDIT_PRODUCT, "EditProductActivity");

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
