package com.zopnote.android.merchant.invoice;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.home.HomeFragment;
import com.zopnote.android.merchant.invoice.editinvoice.EditInvoiceActivity;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.io.File;

public class InvoiceActivity extends AppCompatActivity {

    private static String LOG_TAG = InvoiceActivity.class.getSimpleName();
    private static boolean DEBUG = false;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private String customerId;
    private String invoiceId;
    private InvoiceViewModel viewmodel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.invoice_act);

        setupArgs();
        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId);
        viewmodel.customerId = customerId;

        findActionFromWhichActivity();

        setupViewFragment();

        setupApiCallObservers();
    }

    private void findActionFromWhichActivity() {
        if (!invoiceId.equalsIgnoreCase("!")){
            viewmodel.isActionFromInvoiceHistory = true;
            viewmodel.invoiceId = invoiceId;
        }
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        invoiceId = getIntent().getStringExtra(Extras.INVOICE_ID);

        Log.d("CSD",customerId+"---"+invoiceId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        InvoiceFragment invoiceFragment =
                (InvoiceFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (invoiceFragment == null) {
            // Create the fragment
            invoiceFragment = invoiceFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), invoiceFragment, R.id.contentFrame);
        }
    }

    public static InvoiceViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        InvoiceViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(InvoiceViewModel.class);

        return viewModel;
    }

    private void setupApiCallObservers() {
        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(InvoiceActivity.this);
                    progressDialog.setMessage(InvoiceActivity.this.getResources().getString(R.string.delete_invoice_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.apiCallRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(InvoiceActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(InvoiceActivity.this,
                            InvoiceActivity.this.getResources().getString(R.string.delete_invoice_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccess.setValue(false);

                    if(InvoiceActivity.this != null){
                        InvoiceActivity.this.finish();
                    }
                }
            }
        });

        viewmodel.invoicePdfUri.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String uri) {
               // binding.progressBar.setVisibility(View.GONE);
               // binding.saveAsPdfSubscription.setVisibility(View.VISIBLE);
                Utils.showSuccessToast(InvoiceActivity.this,getString(R.string.save_as_pdf_success_invoice) , Toast.LENGTH_LONG);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.distributor.myfileprovider", new File(uri));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_invoice_report)));








            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_INVOICE, "InvoiceActivity");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invoice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.edit_invoice_menu_item:
                if(pendingInvoice()){
                    showEditInvoiceDialog();
                }else{
                    Toast.makeText(this, R.string.paid_invoice_edit_error_toast, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete_invoice_menu_item:
                showDeleteInvoiceDialog();
                return true;
            case R.id.save_as_pdf_invoice_menu_item:
                if (isPermissionEnabledStorage()) {
                   openCreatePDF();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean pendingInvoice() {
        if(viewmodel.latestInvoice != null){
            if(InvoiceStatusEnum.OPEN == viewmodel.latestInvoice.getStatus()){
                return true;
            }
        }
        return false;
    }



    private void showEditInvoiceDialog() {
        Intent intent = new Intent(InvoiceActivity.this, EditInvoiceActivity.class);
        intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
        intent.putExtra(Extras.INVOICE_ID, viewmodel.latestInvoice.getId());
        startActivity(intent);
    }

    private void showDeleteInvoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.invoice_delete_warning_message)
                .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.enforceNetworkConnection(InvoiceActivity.this)) {
                    viewmodel.deleteInvoice();
                    dialog.dismiss();
                }
            }
        });
    }


    private boolean isPermissionEnabledStorage() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCreatePDF();

                } else {
                    Toast.makeText(this,R.string.permissions_write_external_Storage_permission_settings_instruction, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    private void openCreatePDF() {

       /* if (viewmodel.merchant.getValue().getId().equals("97de2a6b-05f0-4c61-96f8-23850d3c3d45"))
            InvoicePDFNagesh.build(this,"",viewmodel);
        else*/
            addAddressDialog();



    }


    private void addAddressDialog(){

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.enter_address_dialog, null);

        final EditText itemMobileEditText = view.findViewById(R.id.businessAddress);
        itemMobileEditText.setHint("Enter address");

        builder.setView(view);

        builder.setMessage("Add Address")
                .setPositiveButton(R.string.save_as_pdf, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final android.support.v7.app.AlertDialog builderSingle = builder.create();
        builderSingle.show();
        //Override the handler
        builderSingle.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoicePDF.build(InvoiceActivity.this,itemMobileEditText.getText().toString().trim(),viewmodel);

                builderSingle.dismiss();
            }
        });
    }

}
