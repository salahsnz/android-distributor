package com.zopnote.android.merchant.managesubscription.editpause;

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
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.databinding.EditPauseActBinding;
import com.zopnote.android.merchant.event.RefreshDraftInvoiceEvent;
import com.zopnote.android.merchant.event.RefreshDraftReportEvent;
import com.zopnote.android.merchant.managesubscription.SubscriptionUtil;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class EditPauseActivity extends AppCompatActivity {
    private EditPauseViewModel viewmodel;
    private String customerId;
    private Subscription subscription;
    private Pause pause;
    private EditPauseActBinding binding;
    private EditPauseDatePickerFragment datePickerFragment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.edit_pause_act);

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
        pause = (Pause) getIntent().getSerializableExtra(Extras.PAUSE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static EditPauseViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        EditPauseViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(EditPauseViewModel.class);

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
        String startDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, pause.getPauseStartDate());
        binding.startDatePicker.setText(startDateText);

        //init startDate calender
        viewmodel.pauseStartDateCalender = FormatUtil.getLocalCalender();
        viewmodel.pauseStartDateCalender.setTime(pause.getPauseStartDate());

        if (pause.getPauseEndDate() != null) {
            String endDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, pause.getPauseEndDate());
            binding.endDatePicker.setText(endDateText);

            //init endDate calender
            viewmodel.pauseEndDateCalender = FormatUtil.getLocalCalender();
            viewmodel.pauseEndDateCalender.setTime(pause.getPauseEndDate());

            binding.removeEndDate.setVisibility(View.VISIBLE);
        } else {
            binding.endDatePicker.setText(R.string.select_date_label);
            binding.removeEndDate.setVisibility(View.GONE);
        }

        datePickerFragment = (EditPauseDatePickerFragment) getSupportFragmentManager().findFragmentByTag("datePicker");

        if(datePickerFragment == null){
            datePickerFragment = new EditPauseDatePickerFragment();
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
                datePickerFragment.setFlag(EditPauseDatePickerFragment.FLAG_END_DATE);
                datePickerFragment.setSubscriptionStartDate(subscription.getStartDate());
                datePickerFragment.setSubscriptionEndDate(subscription.getEndDate());
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        binding.removeEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.pauseEndDateCalender = null;
                binding.endDatePicker.setText(R.string.select_date_label);
                binding.removeEndDate.setVisibility(View.GONE);

                Toast.makeText(EditPauseActivity.this, R.string.end_date_removed_toast_message, Toast.LENGTH_SHORT).show();

            }
        });

        binding.deletePauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePauseConfirmationDialog();
            }
        });

        binding.saveSubscriptionChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidDateRange()) {

                    if(SubscriptionUtil.isOverlappingPause(viewmodel.pauseStartDateCalender,
                            viewmodel.pauseEndDateCalender, subscription.getPauses(),
                            pause.getId())){
                        Toast.makeText(EditPauseActivity.this, R.string.overlapping_pause_edit_error_toast, Toast.LENGTH_LONG).show();
                    }else {
                        if (NetworkUtil.enforceNetworkConnection(EditPauseActivity.this)) {
                            savePauseDate(subscription, pause);
                        }
                    }
                } else {
                    Toast.makeText(EditPauseActivity.this, R.string.date_range_error_toast, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewmodel.startDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    setPauseStartDate();
                }
            }
        });

        viewmodel.endDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    if (viewmodel.pauseEndDateCalender != null) {
                        setPauseEndDate();

                        binding.removeEndDate.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void showDateChangeWarningDialog() {
        String message = getResources().getString(R.string.subscription_start_date_change_warning_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePickerFragment.setFlag(EditPauseDatePickerFragment.FLAG_START_DATE);
                        datePickerFragment.setSubscriptionStartDate(subscription.getStartDate());
                        datePickerFragment.setSubscriptionEndDate(subscription.getEndDate());
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

        if (customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() > 0) {
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

        if (viewmodel.pauseEndDateCalender != null) {
            if (viewmodel.pauseEndDateCalender.getTime().compareTo(viewmodel.pauseStartDateCalender.getTime()) >= 0) {
                return true;
            } else
                return false;
        } else {
            return true;
        }
    }

    private void setPauseStartDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.pauseStartDateCalender.getTime());
        binding.startDatePicker.setText(date);
    }

    private void setPauseEndDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.pauseEndDateCalender.getTime());
        binding.endDatePicker.setText(date);
    }

    private void savePauseDate(Subscription subscription, Pause pause) {
        if (NetworkUtil.enforceNetworkConnection(EditPauseActivity.this)) {

            Date pauseStartDate = getPauseStartDate();
            Date pauseEndDate = getPauseEndDate();

            viewmodel.updatePause(subscription.getId(), pauseStartDate, pauseEndDate, pause.getId());
        }
    }

    private Date getPauseStartDate() {
        if(viewmodel.startDateChanged.getValue()){
            return viewmodel.pauseStartDateCalender.getTime();
        }else {
            return pause.getPauseStartDate();
        }
    }

    private Date getPauseEndDate() {
        if (viewmodel.pauseEndDateCalender != null) {
            if(viewmodel.endDateChanged.getValue()){
                return viewmodel.pauseEndDateCalender.getTime();
            }else {
                return pause.getPauseEndDate();
            }
        }else {
            //end date can be null
            return null;
        }
    }

    private void showDeletePauseConfirmationDialog() {
        String message = getResources().getString(R.string.confirm_pause_termination_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.button_delete_pause, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkUtil.enforceNetworkConnection(EditPauseActivity.this)) {
                            viewmodel.deletePause(subscription.getId(), pause.getId());
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

        viewmodel.updatePauseApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditPauseActivity.this);
                    progressDialog.setMessage(EditPauseActivity.this.getResources().getString(R.string.update_pause_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.updatePauseApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.updatePauseApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(EditPauseActivity.this,
                            EditPauseActivity.this.getResources().getString(R.string.update_pause_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.updatePauseApiCallSuccess.setValue(false);

                    updateInvoiceIfApplicable();

                    EditPauseActivity.this.finish();
                }
            }
        });

        viewmodel.updatePauseApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditPauseActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.updatePauseApiCallError.setValue(false);
                }
            }
        });

        viewmodel.deletePauseApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(EditPauseActivity.this);
                    progressDialog.setMessage(EditPauseActivity.this.getResources().getString(R.string.delete_pause_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.deletePauseApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.deletePauseApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(EditPauseActivity.this,
                            EditPauseActivity.this.getResources().getString(R.string.delete_pause_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.deletePauseApiCallSuccess.setValue(false);

                    updateInvoiceIfApplicable();

                    EditPauseActivity.this.finish();
                }
            }
        });

        viewmodel.deletePauseApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(EditPauseActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.deletePauseApiCallError.setValue(false);
                }
            }
        });
    }

    private void updateInvoiceIfApplicable() {
        if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftInvoiceEvent.class)){
            EventBus.getDefault().post(new RefreshDraftInvoiceEvent());
        }

        if(EventBus.getDefault().hasSubscriberForEvent(RefreshDraftReportEvent.class)){
            EventBus.getDefault().post(new RefreshDraftReportEvent());
        }
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
