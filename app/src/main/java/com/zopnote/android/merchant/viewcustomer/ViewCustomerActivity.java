package com.zopnote.android.merchant.viewcustomer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.addsubscription.DateFragment;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.editcustomer.EditCustomerActivity;
import com.zopnote.android.merchant.editdraftinvoice.EditDraftInvoiceActivity;
import com.zopnote.android.merchant.managesubscription.ManageSubscriptionsActivity;
import com.zopnote.android.merchant.movecustomer.MoveCustomerActivity;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewinvoicehistory.ViewInvoiceHistoryActivity;

import java.util.List;

public class ViewCustomerActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static String LOG_TAG = ViewCustomerActivity.class.getSimpleName();
    private static boolean DEBUG = false;
    private final static String FRAGMENT_TAG_DATE_PICKER = "checkout_date_picker_fragment";
    private ViewCustomerViewModel viewmodel;
    private String customerId;
    private boolean hasValidMobileNumber = true;
    private boolean isOmDemand = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_customer_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId);

        setupViewFragment();

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {
                if (!hasValidMobileNumber(customer.getMobileNumber())) {
                    hasValidMobileNumber = false;
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                if (merchant.getProductList().contains("Ondemand"))
                    isOmDemand = true;
            }
        });
        viewmodel.refreshApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerActivity.this);
                    progressDialog.setMessage(ViewCustomerActivity.this.getResources().getString(R.string.refresh_customer_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.refreshApiCallRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.refreshApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.refreshApiCallError.setValue(false);
                }
            }
        });

        viewmodel.refreshApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerActivity.this,
                            ViewCustomerActivity.this.getResources().getString(R.string.refresh_customer_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.refreshApiCallSuccess.setValue(false);
                }
            }
        });
        findViewById(R.id.refreshCustomer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                refreshCustomer();
            }
        });



    }
    public void refreshCustomer() {

        if (NetworkUtil.enforceNetworkConnection(this)) {
            viewmodel.refreshCustomer();
        }
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewFragment() {
        ViewCustomerFragment viewCustomerFragment =
                (ViewCustomerFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (viewCustomerFragment == null) {
            // Create the fragment
            viewCustomerFragment = ViewCustomerFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), viewCustomerFragment, R.id.contentFrame);
        }
    }

    public static ViewCustomerViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        ViewCustomerViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(ViewCustomerViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_CUSTOMER, "AddOnDemandActivity");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_customer_menu, menu);

        if(isOmDemand|| BuildConfig.PRODUCT_FLAVOUR_MERCHANT == true) {
            MenuItem item = menu.findItem(R.id.generate_invoice_customer_menu_item);
            item.setVisible(false);
        }
        return true;
    }

    private boolean hasValidMobileNumber(String mobileNumber) {
        if (getDisplayMobileNumber(mobileNumber).matches("^[6789]\\d{9}$")) {
            return true;
        }
        return false;
    }

    private String getDisplayMobileNumber(String mobileNumber) {
        return mobileNumber.replaceAll("^\\+91", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.edit_customer_profile_menu_item:
                Intent editProfileIntent = new Intent(this, EditCustomerActivity.class);
                editProfileIntent.putExtra(Extras.CUSTOMER_ID, customerId);
                startActivity(editProfileIntent);
                return true;
            case R.id.edit_customer_route_sequence_menu_item:
                Intent editRouteSequenceIntent = new Intent(this,MoveCustomerActivity.class);
                editRouteSequenceIntent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                editRouteSequenceIntent.putExtra(Extras.ROUTE, viewmodel.customer.getValue().getRoute());
                startActivity(editRouteSequenceIntent);
                return true;
            case R.id.edit_notes_customer_menu_item:
                showEditNotesDialog();
                return true;
            case R.id.generate_invoice_customer_menu_item:
                showGenerateInvoiceWarningDialog();
                return true;
            case R.id.view_draft_invoice_customer_menu_item:
                viewDraftInvoice();
                return true;
            case R.id.check_out_menu_item:
                Fragment fragment = CheckoutDateFragment.newInstance();
                ActivityUtils.replaceFragmentInActivity(
                        getSupportFragmentManager(),
                        fragment,
                        R.id.contentFrame,
                        FRAGMENT_TAG_DATE_PICKER);
                        return true;
            case R.id.delete_customer_menu_item:
                deleteCustomerWarningDialog();
                return true;
            case R.id.release_current_invoice_menu_item:
                releaseCurrentInvoice();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewDraftInvoice() {
        Intent intent = new Intent(this, EditDraftInvoiceActivity.class);
        intent.putExtra(Extras.CUSTOMER_ID, customerId);
        startActivity(intent);
    }

    private void releaseCurrentInvoice() {
        viewmodel.releaseCurrentInvoice();
       // Toast.makeText(this, "Release Current Invoice",
         //       Toast.LENGTH_LONG).show();
    }

    private void checkPermissionAndCall() {
        if(hasValidMobileNumber) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                callCustomer();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }else{
            Toast.makeText(this, R.string.invalid_mobile_number_toast, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void callCustomer() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + viewmodel.customer.getValue().getMobileNumber()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callCustomer();
                } else {
                    Toast.makeText(this,R.string.permissions_call_phone_permission_settings_instruction, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }



    private void showEditNotesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.edit_notes_dialog, null);

        final EditText editText = view.findViewById(R.id.editNotes);
        editText.setHint(R.string.enter_notes);

        String notes = viewmodel.customer.getValue().getNotes();
        if(notes != null && notes.trim().length() >0){
            editText.setText(notes);
        }
        builder.setView(view);

        builder.setTitle(R.string.edit_notes_dialog_title)
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
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
                if (NetworkUtil.enforceNetworkConnection(ViewCustomerActivity.this)) {
                    viewmodel.saveNotes(editText.getText().toString().trim());
                    dialog.dismiss();
                }
            }
        });
    }

    private void showGenerateInvoiceWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.generate_invoice_warning_message)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
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
                if (NetworkUtil.enforceNetworkConnection(ViewCustomerActivity.this)) {
                    viewmodel.generateInvoice();
                    dialog.dismiss();
                }
            }
        });
    }

    private void deleteCustomerWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_customer_warning_message)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
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
                if (NetworkUtil.enforceNetworkConnection(ViewCustomerActivity.this)) {
                    viewmodel.deleteCustomer();
                    dialog.dismiss();
                }
            }
        });
    }

}
