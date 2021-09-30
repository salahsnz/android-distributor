package com.zopnote.android.merchant.managesubscription.editsubscription;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.databinding.EditSubscriptionActBinding;
import com.zopnote.android.merchant.event.RefreshDraftInvoiceEvent;
import com.zopnote.android.merchant.event.RefreshDraftReportEvent;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class EditSubscriptionActivity extends AppCompatActivity {
    private EditSubscriptionViewModel viewmodel;
    private String customerId;
    private Subscription subscription;
    private EditSubscriptionActBinding binding;
    private EditSubscriptionDatePickerFragment datePickerFragment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.edit_subscription_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(customerId);

        setupView();

        setupApiCallObservers();
    }

    private void setupArgs() {
        customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
        subscription = (Subscription) getIntent().getSerializableExtra(Extras.SUBSCRIPTION);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static EditSubscriptionViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        EditSubscriptionViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(EditSubscriptionViewModel.class);

        return viewModel;
    }

    private void setupView() {

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data
            }
        });

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {
                binding.customerAddress.setText(getCustomerAddress(customer));
            }
        });

        binding.subscriptionName.setText(subscription.getProduct().getName());

        viewmodel.tag = subscription.getTag();

        viewmodel.quantity = String.valueOf(subscription.getQuantity());
        binding.quantity.setText(viewmodel.quantity);

        String startDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, subscription.getStartDate());
        binding.startDatePicker.setText(startDateText);

        //init startDate calender
        viewmodel.startDateCalender = FormatUtil.getLocalCalender();
        viewmodel.startDateCalender.setTime(subscription.getStartDate());

        if (subscription.getEndDate() != null) {
            String endDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, subscription.getEndDate());
            binding.endDatePicker.setText(endDateText);

            //init endDate calender
            viewmodel.endDateCalender = FormatUtil.getLocalCalender();
            viewmodel.endDateCalender.setTime(subscription.getEndDate());

            binding.removeEndDate.setVisibility(View.VISIBLE);
        } else {
            binding.endDatePicker.setText(R.string.select_date_label);
            binding.removeEndDate.setVisibility(View.GONE);
        }

        datePickerFragment = (EditSubscriptionDatePickerFragment) getSupportFragmentManager().findFragmentByTag("datePicker");

        if(datePickerFragment == null){
            datePickerFragment = new EditSubscriptionDatePickerFragment();
        }

        binding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateChangeWarningDialog();
            }
        });

        binding.endDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.setFlag(EditSubscriptionDatePickerFragment.FLAG_END_DATE);
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        binding.removeEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.endDateCalender = null;
                binding.endDatePicker.setText(R.string.select_date_label);
                binding.removeEndDate.setVisibility(View.GONE);

                Toast.makeText(EditSubscriptionActivity.this, R.string.end_date_removed_toast_message, Toast.LENGTH_SHORT).show();

            }
        });

        binding.deleteSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteSubscriptionConfirmationDialog();
            }
        });

        binding.saveSubscriptionChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.tag = binding.tag.getText().toString();
                viewmodel.quantity = binding.quantity.getText().toString();
                if (isValidDateRange()) {
                    if (NetworkUtil.enforceNetworkConnection(EditSubscriptionActivity.this)) {
                        saveSubscriptionDate(subscription);
                    }
                } else {
                    Toast.makeText(EditSubscriptionActivity.this, R.string.date_range_error_toast, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewmodel.startDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    setStartDate();
                }
            }
        });

        viewmodel.endDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    if (viewmodel.endDateCalender != null) {
                        setEndDate();

                        binding.removeEndDate.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        setFocus();

    }

    private void setFocus() {
        binding.startDatePicker.setFocusable(true);
        binding.startDatePicker.setFocusableInTouchMode(true);
        binding.startDatePicker.requestFocus();
    }

    private void showDateChangeWarningDialog() {
        String message = getResources().getString(R.string.subscription_start_date_change_warning_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePickerFragment.setFlag(EditSubscriptionDatePickerFragment.FLAG_START_DATE);
                        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private String getCustomerAddress(Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        if (customer.getDoorNumber() != null) {
            stringBuilder.append(customer.getDoorNumber());
        }

        if (customer.getAddressLine1() != null) {
            String addressLine1 = Utils.getAddressLine1(this, customer.getAddressLine1()).trim();
            if( ! addressLine1.isEmpty()){
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(addressLine1);
            }
        }

        if (customer.getAddressLine2() != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(customer.getAddressLine2());
        }
        return stringBuilder.toString();
    }

    private boolean isValidDateRange() {

        if (viewmodel.endDateCalender != null) {
            if (viewmodel.endDateCalender.getTime().compareTo(viewmodel.startDateCalender.getTime()) >= 0) {
                return true;
            } else
                return false;
        } else {
            return true;
        }
    }

    private void setStartDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.startDateCalender.getTime());
        binding.startDatePicker.setText(date);
    }

    private void setEndDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.endDateCalender.getTime());
        binding.endDatePicker.setText(date);
    }

    private void saveSubscriptionDate(Subscription subscription) {
        if (NetworkUtil.enforceNetworkConnection(EditSubscriptionActivity.this)) {
            Date startDate = getStartDate();
            Date endDate = getEndDate();

            viewmodel.updateSubscription(subscription.getId(), startDate, endDate);
        }
    }

    private Date getStartDate() {
        if(viewmodel.startDateChanged.getValue()){
            return viewmodel.startDateCalender.getTime();
        }else{
            return subscription.getStartDate();
        }
    }

    private Date getEndDate() {
        if (viewmodel.endDateCalender != null) {
            if(viewmodel.endDateChanged.getValue()){
                return viewmodel.endDateCalender.getTime();
            }else {
                return subscription.getEndDate();
            }
        }else {
            //end date can be null
            return null;
        }
    }


    private void showDeleteSubscriptionConfirmationDialog() {
        String message = String.format(getResources().getString(R.string.confirm_subscription_delete_message),
                subscription.getProduct().getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.button_delete_subscription, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkUtil.enforceNetworkConnection(EditSubscriptionActivity.this)) {
                            viewmodel.deleteSubscription(subscription.getId());
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void setupApiCallObservers() {

        viewmodel.subscriptionUpdateApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditSubscriptionActivity.this);
                    progressDialog.setMessage(EditSubscriptionActivity.this.getResources().getString(R.string.update_subscription_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.subscriptionUpdateApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.subscriptionUpdateApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(EditSubscriptionActivity.this,
                            EditSubscriptionActivity.this.getResources().getString(R.string.update_subscription_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.subscriptionUpdateApiCallSuccess.setValue(false);

                    updateInvoiceIfApplicable();

                    EditSubscriptionActivity.this.finish();
                }
            }
        });

        viewmodel.subscriptionUpdateApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditSubscriptionActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.subscriptionUpdateApiCallError.setValue(false);
                }
            }
        });

        viewmodel.subscriptionDeleteApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditSubscriptionActivity.this);
                    progressDialog.setMessage(EditSubscriptionActivity.this.getResources().getString(R.string.delete_subscription_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.subscriptionDeleteApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.subscriptionDeleteApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(EditSubscriptionActivity.this,
                            EditSubscriptionActivity.this.getResources().getString(R.string.delete_subscription_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.subscriptionDeleteApiCallSuccess.setValue(false);

                    updateInvoiceIfApplicable();

                    EditSubscriptionActivity.this.finish();
                }
            }
        });

        viewmodel.subscriptionDeleteApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditSubscriptionActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.subscriptionDeleteApiCallError.setValue(false);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        viewmodel.tag = binding.tag.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(viewmodel.tag != null && viewmodel.tag.trim().length() > 0){
            binding.tag.setText(viewmodel.tag);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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

    private void updateInvoiceIfApplicable() {
        if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftInvoiceEvent.class)){
            EventBus.getDefault().post(new RefreshDraftInvoiceEvent());
        }

        if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftReportEvent.class)){
            EventBus.getDefault().post(new RefreshDraftReportEvent());
        }
    }
}
