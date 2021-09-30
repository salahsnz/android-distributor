package com.zopnote.android.merchant.managesubscription;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.addsubscription.AddSubscriptionActivity;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.data.model.StatusEnum;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.databinding.ManageSubscriptionsFragBinding;
import com.zopnote.android.merchant.managesubscription.addcustomization.AddCustomizationActivity;
import com.zopnote.android.merchant.managesubscription.editpause.EditPauseActivity;
import com.zopnote.android.merchant.managesubscription.editsubscription.EditSubscriptionActivity;
import com.zopnote.android.merchant.managesubscription.viewcustomization.ViewCustomizationActivity;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ManageSubscriptionsFragment extends Fragment {

    private ManageSubscriptionsFragBinding binding;
    private ManageSubscriptionsViewModel viewmodel;

    private ProgressDialog progressDialog;
    private AddPauseDatePickerFragment datePickerFragment;

    public ManageSubscriptionsFragment() {
        // Requires empty public constructor
    }

    public static ManageSubscriptionsFragment newInstance() {
        ManageSubscriptionsFragment fragment = new ManageSubscriptionsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ManageSubscriptionsFragBinding.inflate(inflater, container, false);

        binding.addSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageSubscriptionsFragment.this.getActivity(), AddSubscriptionActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = ManageSubscriptionsActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data
            }
        });

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {
                String name = getName(customer);
                String mobile = getDisplayMobileNumber(customer);
                boolean hasName = name.trim().length() > 0 ? true : false;
                boolean hasMobile = Utils.hasValidMobileNumber(mobile);
                if (hasName) {
                    binding.name.setText(name);
                    binding.name.setVisibility(View.VISIBLE);
                } else {
                    binding.name.setVisibility(View.GONE);
                }
                if (hasMobile) {
                    if (hasName) {
                        binding.nameMobileSeparator.setVisibility(View.VISIBLE);
                    } else {
                        binding.nameMobileSeparator.setVisibility(View.GONE);
                    }
                    binding.mobileNumber.setText(mobile);
                }else{
                    binding.nameMobileSeparator.setVisibility(View.GONE);
                    binding.mobileNumber.setVisibility(View.GONE);
                }

                String email = customer.getEmail();
                boolean hasEmail = email != null && (email.trim().length() > 0 ? true : false);
                if(hasEmail){
                    binding.email.setText(email);
                    binding.email.setVisibility(View.VISIBLE);
                }else{
                    binding.email.setVisibility(View.GONE);
                }

                binding.doorNumber.setText(customer.getDoorNumber());
                if (customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() >0 ) {
                    String addressLine1 = Utils.getAddressLine1(getContext(), customer.getAddressLine1()).trim();
                    if( ! addressLine1.isEmpty()){
                        binding.addressLine1.setText(addressLine1);
                        binding.addressLine1Layout.setVisibility(View.VISIBLE);
                    }else{
                        binding.addressLine1Layout.setVisibility(View.GONE);
                    }
                } else {
                    binding.addressLine1Layout.setVisibility(View.GONE);
                }
                binding.addressLine2.setText(customer.getAddressLine2());
            }
        });

        viewmodel.subscriptions.observe(this, new Observer<List<Subscription>>() {
            @Override
            public void onChanged(@Nullable List<Subscription> subscriptions) {
                binding.subscriptionsContainer.removeAllViews();
                showSubscriptions(subscriptions);
            }
        });

        setupApiCallObservers();
    }

    private void showSubscriptions(List<Subscription> subscriptions) {
        for (final Subscription subscription : subscriptions) {

            StatusEnum subscriptionStatus = subscription.getSubscriptionStatus();
            if( subscriptionStatus.name().equalsIgnoreCase(StatusEnum.ACTIVE.name())){

                final View subscriptionItemView = LayoutInflater.from(ManageSubscriptionsFragment.this.getActivity()).inflate(R.layout.manage_subscription_item, null);
                ((TextView) subscriptionItemView.findViewById(R.id.name)).setText(subscription.getProduct().getName());

                if(subscription.getStartDate() != null){
                    String startDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, subscription.getStartDate());
                    ((TextView)subscriptionItemView.findViewById(R.id.startDate)).setText(startDateText);
                }else{
                    subscriptionItemView.findViewById(R.id.startDateLayout).setVisibility(View.GONE);
                }

                if(subscription.getEndDate() != null){
                    String endDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, subscription.getEndDate());
                    ((TextView)subscriptionItemView.findViewById(R.id.endDate)).setText(endDateText);
                }else{
                    subscriptionItemView.findViewById(R.id.endDateLayout).setVisibility(View.GONE);
                }

                if(subscription.getTag() != null && subscription.getTag().trim().length() >0){
                    ((TextView)subscriptionItemView.findViewById(R.id.tag)).setText(subscription.getTag());
                }else{
                    subscriptionItemView.findViewById(R.id.tagLayout).setVisibility(View.GONE);
                }

                if(isSubscriptionCustomized(subscription)){
                    subscriptionItemView.findViewById(R.id.customizedTag).setVisibility(View.VISIBLE);
                }else{
                    subscriptionItemView.findViewById(R.id.customizedTag).setVisibility(View.GONE);
                }

                if(subscription.getPauses() != null && subscription.getPauses().size() >0 ){
                    addPauseView(subscriptionItemView, subscription);
                }else{
                    subscriptionItemView.findViewById(R.id.pausesLayout).setVisibility(View.GONE);
                }

                subscriptionItemView.findViewById(R.id.subscriptionActionsButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSubscriptionOptions(v, subscription);
                    }
                });
                subscriptionItemView.findViewById(R.id.customizedTag).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteCustomizedWarningDialog(subscription.getId());
                    }
                });
                subscriptionItemView.setTag(subscription.getId());
                binding.subscriptionsContainer.addView(subscriptionItemView);
            }
        }
    }

    private void addPauseView(View view, final Subscription subscription) {
        List<Pause> pauses = subscription.getPauses();

        SubscriptionUtil.sortPausesAscending(pauses);

        for(int i = 0; i < pauses.size(); i++){
            final Pause pause = pauses.get(i);
            StatusEnum pauseStatus = pause.getPauseStatus();

            if(pauseStatus.name().equalsIgnoreCase(StatusEnum.ACTIVE.name())){
                View pauseViewItem = LayoutInflater.from(ManageSubscriptionsFragment.this.getActivity()).inflate(R.layout.pause_item, null);

                String pauseDurationText = SubscriptionUtil.getPauseDurationText(pause, getContext());
                ((TextView) pauseViewItem.findViewById(R.id.stopDuration)).setText(pauseDurationText);
                pauseViewItem.findViewById(R.id.stopDuration).setVisibility(View.VISIBLE);

                pauseViewItem.setTag(pause.getId());
                pauseViewItem.findViewById(R.id.editPauseAction).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EditPauseActivity.class);
                        intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                        intent.putExtra(Extras.SUBSCRIPTION, subscription);
                        intent.putExtra(Extras.PAUSE, pause);
                        getContext().startActivity(intent);
                    }
                });
                ((LinearLayout)view.findViewById(R.id.pausesContainer)).addView(pauseViewItem);
            }
        }

        int pauseCount = ((LinearLayout)view.findViewById(R.id.pausesContainer)).getChildCount();
        if(pauseCount == 0){
            view.findViewById(R.id.pausesLayout).setVisibility(View.GONE);
        }
    }

    private void setupApiCallObservers() {
        viewmodel.pauseApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ManageSubscriptionsFragment.this.getActivity());
                    progressDialog.setMessage(ManageSubscriptionsFragment.this.getActivity().getResources().getString(R.string.add_pause_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.pauseApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.pauseApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ManageSubscriptionsFragment.this.getActivity(),
                            ManageSubscriptionsFragment.this.getActivity().getResources().getString(R.string.add_pause_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.pauseApiCallSuccess.setValue(false);

                    if(getActivity() != null){
                        ((ManageSubscriptionsActivity)getActivity()).updateDraftInvoiceReportIfNeeded();
                    }
                }
            }
        });

        viewmodel.pauseApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ManageSubscriptionsFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.pauseApiCallError.setValue(false);
                }
            }
        });
        viewmodel.deleteCustomizedApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ManageSubscriptionsFragment.this.getActivity());
                    progressDialog.setMessage(ManageSubscriptionsFragment.this.getActivity().getResources().getString(R.string.delete_customized_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.deleteCustomizedApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.deleteCustomizedApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ManageSubscriptionsFragment.this.getActivity(),
                            ManageSubscriptionsFragment.this.getActivity().getResources().getString(R.string.delete_customized_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.deleteCustomizedApiCallSuccess.setValue(false);

                    if(getActivity() != null){
                        ((ManageSubscriptionsActivity)getActivity()).updateDraftInvoiceReportIfNeeded();
                    }
                }
            }
        });

        viewmodel.deleteCustomizedApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ManageSubscriptionsFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.deleteCustomizedApiCallError.setValue(false);
                }
            }
        });

    }

    private String getName(Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        if (customer.getFirstName() != null && customer.getFirstName().trim().length() > 0) {
            stringBuilder.append(customer.getFirstName().trim());
        }
        if (customer.getLastName() != null && customer.getLastName().trim().length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(customer.getLastName().trim());
        }
        return stringBuilder.toString();
    }

    private String getDisplayMobileNumber(Customer customer) {
        return customer.getMobileNumber().replaceAll("^\\+91", "");
    }

    private void showSubscriptionOptions(View view, final Subscription subscription) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        popupMenu.getMenu().add(R.string.edit_subscription_menu_label).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), EditSubscriptionActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                intent.putExtra(Extras.SUBSCRIPTION, subscription);
                getContext().startActivity(intent);
                return true;
            }
        });

        popupMenu.getMenu().add(R.string.add_pause_menu_label).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAddPauseDialog(subscription);
                return true;
            }
        });
        popupMenu.getMenu().add(R.string.add_or_manage_customization).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showCustomization(subscription);
                return true;
            }
        });
        popupMenu.show();

    }

    private void showCustomization(Subscription subscription) {
        boolean isCustomized = isSubscriptionCustomized(subscription);
        if(isCustomized){
            //show customization
            Intent intent = new Intent(getContext(), ViewCustomizationActivity.class);
            intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
            intent.putExtra(Extras.SUBSCRIPTION, subscription);
            getContext().startActivity(intent);
        }else {
            //add customization
            Intent intent = new Intent(getContext(), AddCustomizationActivity.class);
            intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
            intent.putExtra(Extras.SUBSCRIPTION, subscription);
            getContext().startActivity(intent);
        }
    }

    private boolean isSubscriptionCustomized(Subscription subscription) {
        if(subscription.getPricingMode() != null){
            return true;
        }

        if(subscription.isAnnualSubscription()){
            return true;
        }

        return false;
    }
    private void showDeleteCustomizedWarningDialog(final String subscriptionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_customized_warning_message)
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
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    viewmodel.deleteCustomized(subscriptionId);
                    dialog.dismiss();
                }
            }
        });
    }

    private void showAddPauseDialog(final Subscription subscription){
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.pause_subscription_dialog, null);
        dialog.setView(view);

        ((TextView)view.findViewById(R.id.subscriptionTitle)).setText(String.format(getResources().getString(R.string.pause_subscription_title_label), subscription.getProduct().getName()));

        ((TextView)view.findViewById(R.id.startDatePicker)).setText(R.string.start_date_label);

        ((TextView)view.findViewById(R.id.endDatePicker)).setText(R.string.end_date_label);

        datePickerFragment = (AddPauseDatePickerFragment) getFragmentManager().findFragmentByTag("datePicker");
        if(datePickerFragment == null){
            datePickerFragment = new AddPauseDatePickerFragment();
        }

        view.findViewById(R.id.startDatePickerLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.setFlag(AddPauseDatePickerFragment.FLAG_START_DATE);
                datePickerFragment.setSubscriptionStartDate(subscription.getStartDate());
                datePickerFragment.setSubscriptionEndDate(subscription.getEndDate());
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        view.findViewById(R.id.endDatePickerLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.setFlag(AddPauseDatePickerFragment.FLAG_END_DATE);
                datePickerFragment.setSubscriptionStartDate(subscription.getStartDate());
                datePickerFragment.setSubscriptionEndDate(subscription.getEndDate());
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.pauseStartDateCalender = null;
                viewmodel.pauseEndDateCalender = null;
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.setButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidDateRange()){

                    if(SubscriptionUtil.isOverlappingPause(viewmodel.pauseStartDateCalender,
                            viewmodel.pauseEndDateCalender, subscription.getPauses(), null)){
                        Toast.makeText(getContext(), R.string.overlapping_pause_error_toast, Toast.LENGTH_LONG).show();
                    }else {
                        if(NetworkUtil.enforceNetworkConnection(getContext())){
                            savePauseDates(subscription);
                            dialog.dismiss();
                        }
                    }

                }else {
                    Toast.makeText(getContext(), R.string.date_range_error_toast, Toast.LENGTH_LONG).show();
                }
            }
        });

        viewmodel.startDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    if(viewmodel.pauseStartDateCalender != null){
                        setPauseStartDate((TextView) view.findViewById(R.id.startDatePicker), viewmodel.pauseStartDateCalender.getTime());
                    }
                }
            }
        });

        viewmodel.endDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    if(viewmodel.pauseEndDateCalender != null){
                        setPauseEndDate((TextView) view.findViewById(R.id.endDatePicker), viewmodel.pauseEndDateCalender.getTime());
                    }
                }
            }
        });
        dialog.show();
    }

    private boolean isValidDateRange() {
        boolean pauseStartDateValid = false;

        if(viewmodel.pauseStartDateCalender != null){
            pauseStartDateValid = true;
        }

        if(pauseStartDateValid){
            if(viewmodel.pauseEndDateCalender != null) {
                if (viewmodel.pauseEndDateCalender.getTime().compareTo(viewmodel.pauseStartDateCalender.getTime()) >= 0) {
                    return true;
                }
            }else {
                return true;
            }
        }
        return false;
    }

    private void setPauseStartDate(TextView startDatePicker, Date pauseStartDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pauseStartDate);
        viewmodel.pauseStartDateCalender =  calendar;

        String date = FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR.format(pauseStartDate.getTime());
        startDatePicker.setText(date);
    }


    private void setPauseEndDate(TextView endDatePicker, Date pauseEndDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pauseEndDate);
        viewmodel.pauseEndDateCalender =  calendar;

        String date = FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR.format(pauseEndDate.getTime());
        endDatePicker.setText(date);
    }

    private void savePauseDates(Subscription subscription) {
        if (viewmodel.pauseStartDateCalender != null) {
            if (NetworkUtil.enforceNetworkConnection(getContext())) {

                Date startDate = getPauseStartDate();
                Date endDate = getPauseEndDate();

                viewmodel.addPause(subscription.getId(), startDate, endDate);

                viewmodel.pauseStartDateCalender = null;
                viewmodel.pauseEndDateCalender = null;
            }
        }
    }

    private Date getPauseStartDate() {
        return viewmodel.pauseStartDateCalender.getTime();
    }

    private Date getPauseEndDate() {
        if( viewmodel.pauseEndDateCalender != null){
            return viewmodel.pauseEndDateCalender.getTime();
        }else{
            //end date can be null
            return null;
        }
    }
}
