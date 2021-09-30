package com.zopnote.android.merchant.registermerchant;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.RegisterActBinding;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;
import android.widget.TextView;


public class RegisterMerchantActivity extends AppCompatActivity {


    private RegisterActBinding binding;
    private RegisterViewModel viewmodel;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.register_act);
        setupView();
        setupToolbar();

        viewmodel = obtainViewModel(this);

        binding.finishReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        TextView textView = (TextView) findViewById(R.id.customerInfo);
        textView.setText("Customers can sign in with registered mobile number and pay online at https://www.zopnote.com/consumer");
        //textView.setText(Html.fromHtml("Customers can sign in with registered mobile number <b>and pay online </b>at https://www.zopnote.com/consumer"));
        Linkify.addLinks(textView, Linkify.WEB_URLS);
        Linkify.addLinks(binding.registerNote, Linkify.PHONE_NUMBERS);
        setupClickListeners();

        setupModelObservers();


    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(false);
    }
    private void setupView() {
        String  merchantStatus = getIntent().getStringExtra(Extras.MERCHANT_STATUS);
        if (merchantStatus.equalsIgnoreCase("1111"))
            setVisibilityRegistered();
    }
    private void setupClickListeners() {
        binding.registerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields()) {
                    if (Utils.enforceConnection(getApplicationContext())) {
                        viewmodel.callRegisterApi(binding.merchantName.getText().toString().trim(),
                                binding.merchantBusinessName.getText().toString().trim());
                    }
                }
            }
        });
    }
    private void setupModelObservers() {

        viewmodel.isRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(RegisterMerchantActivity.this);
                    progressDialog.setMessage(RegisterMerchantActivity.this.getResources().getString(R.string.registering_merchant_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.isRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.isError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                if (isError) {
                    Utils.showFailureToast(RegisterMerchantActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.isError.setValue(false);
                }
            }
        });

            viewmodel.isAuthCallSuccess.observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean success) {
                    if (success) {
                        setVisibilityRegistered();
                    }
                }
            });

    }

    private void setVisibilityRegistered() {
        binding.llRegisterFields.setVisibility(View.GONE);
        binding.llRegistered.setVisibility(View.VISIBLE);

    }


    public static RegisterViewModel obtainViewModel(FragmentActivity activity) {
        // uid is not yet available; cannot use our ViewModelFactory since it depends on uid

        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(activity.getApplication());

        RegisterViewModel viewModel = factory.create(RegisterViewModel.class);

        return viewModel;
    }



    private boolean validateAllFields() {
        if(validate(binding.merchantName) && validate(binding.merchantBusinessName)){
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {

        if(editText.equals(binding.merchantName)){
            if (binding.merchantName.getText().toString().trim().length() > 2) {
                return true;
            } else {
                binding.merchantName.setError(getResources().getString(R.string.name_error_message));
                binding.merchantName.requestFocus();
                return false;
            }
        }

        if(editText.equals(binding.merchantBusinessName)){
            if (binding.merchantBusinessName.getText().toString().trim().length() > 0) {
                return true;
            } else {
                binding.merchantBusinessName.setError(getResources().getString(R.string.business_name_error_message));
                binding.merchantBusinessName.requestFocus();
                return false;
            }
        }

        return false;
    }
}
