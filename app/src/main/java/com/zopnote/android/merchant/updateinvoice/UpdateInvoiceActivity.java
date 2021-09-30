package com.zopnote.android.merchant.updateinvoice;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceItem;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.PaymentModeEnum;
import com.zopnote.android.merchant.databinding.EditCustomerActBinding;
import com.zopnote.android.merchant.databinding.UpdateInvoiceActBinding;
import com.zopnote.android.merchant.invoice.InvoiceUtil;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NothingSelectedSpinnerAdapter;
import com.zopnote.android.merchant.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class UpdateInvoiceActivity extends AppCompatActivity {

    private static String LOG_TAG = UpdateInvoiceActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    private UpdateInvoiceActBinding binding;
    private UpdateInvoiceViewModel viewmodel;
    private ProgressDialog progressDialog;

    public String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.update_invoice_act);

        setupToolbar();

        setupArgs();

        viewmodel = obtainViewModel(this);
        viewmodel.customerId = customerId;
        viewmodel.init();



        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(UpdateInvoiceActivity.this);
                    progressDialog.setMessage(UpdateInvoiceActivity.this.getResources().getString(R.string.update_invoice_running_message));
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
                    Utils.showFailureToast(UpdateInvoiceActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(UpdateInvoiceActivity.this,
                            UpdateInvoiceActivity.this.getResources().getString(R.string.update_invoice_success_message),
                            Toast.LENGTH_LONG);
                    UpdateInvoiceActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantName =  merchant.getName();

            }
        });
        viewmodel.invoices.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(@Nullable List<Invoice> invoices) {
                viewmodel.latestInvoice = InvoiceUtil.getLatestInvoice(invoices);
                prepareInvoiceStatusSpinner();

            }
        });
        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    setDate();
                }
            }
        });
        binding.paymentDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new InvoiceDatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        binding.saveInvoiceChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewmodel.invoiceStatusAction.equalsIgnoreCase("PAID")) {
                    if (paymentDateSelected(binding.paymentDate)) {
                        viewmodel.updateInvoice();
                    }
                } else
                    viewmodel.updateInvoice();

            }
        });
    }

    private boolean paymentDateSelected(TextView paymentDate) {
        if (paymentDate.getText().toString().equalsIgnoreCase(getString(R.string.select_date_label))) {
            Toast.makeText(UpdateInvoiceActivity.this, R.string.payment_date_error_message, Toast.LENGTH_LONG).show();
            return false;
        }else
            return true;

    }

    private void prepareInvoiceStatusSpinner() {

        List<String> choices = new ArrayList<>();
       // for (InvoiceStatusEnum invoiceStatusOption: InvoiceStatusEnum.values()) {
       //     choices.add(invoiceStatusOption.name());
       // }
        choices.add("OPEN");
        choices.add("PAID");

        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.invoiceStatus.setAdapter(new NothingSelectedSpinnerAdapter(
                adapter,
                R.layout.spinner_row_nothing_selected,
                this));
        int position = adapter.getPosition(viewmodel.latestInvoice.getStatus().name());

        binding.invoiceStatus.setSelection(position+1);
        binding.invoiceStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = (String) parent.getItemAtPosition(position);
                if(selectedText != null){
                    viewmodel.invoiceStatusAction = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewmodel.InvStatusDateChangeCalender.getTime());
        binding.paymentDate.setText(date);
    }

    public static UpdateInvoiceViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        UpdateInvoiceViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(UpdateInvoiceViewModel.class);

        return viewModel;
    }


    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);

    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.UPDATE_INVOICE, "UpdateInvoice");

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

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }





}
