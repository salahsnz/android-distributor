package com.zopnote.android.merchant.viewcustomer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.addcustomer.ReviewFragment;
import com.zopnote.android.merchant.addondemanditem.AddOnDemandActivity;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.data.model.PaymentModeEnum;
import com.zopnote.android.merchant.data.model.StatusEnum;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.databinding.ViewCustomerFragBinding;
import com.zopnote.android.merchant.invoice.InvoiceActivity;
import com.zopnote.android.merchant.invoice.editinvoice.EditInvoiceActivity;
import com.zopnote.android.merchant.managesubscription.ManageSubscriptionsActivity;
import com.zopnote.android.merchant.managesubscription.ManageSubscriptionsFragment;
import com.zopnote.android.merchant.managesubscription.SubscriptionUtil;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewinvoicehistory.ViewInvoiceHistoryActivity;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by nmohideen on 26/12/17.
 */

public class ViewCustomerFragment extends Fragment {
    private ViewCustomerFragBinding binding;
    private ViewCustomerViewModel viewmodel;

    private ProgressDialog progressDialog;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private Double invAmount;
    private String editText_invAmount;
    private View viewAdvancePayment;

    public ViewCustomerFragment() {
        // Requires empty public constructor
    }

    public static ViewCustomerFragment newInstance() {
        ViewCustomerFragment fragment = new ViewCustomerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ViewCustomerFragBinding.inflate(inflater, container, false);

        binding.viewBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewCustomerFragment.this.getActivity(), InvoiceActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                intent.putExtra(Extras.INVOICE_ID, "!");
                startActivity(intent);
            }
        });

        binding.manageSubscriptionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCustomerFragment.this.getActivity(), ManageSubscriptionsActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                startActivity(intent);
            }
        });
        binding.addItemButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewCustomerFragment.this.getActivity(), AddOnDemandActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                startActivity(intent);
            }
        });

        if (BuildConfig.PRODUCT_FLAVOUR_MERCHANT == false)
            binding.subscriberView.setVisibility(View.VISIBLE);
        else
            binding.subscriberView.setVisibility(View.GONE);

        binding.addNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewmodel.addCustomerMobileNumber();
            }
        });
        binding.subscriberView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewmodel.subscriberView();
            }
        });

        binding.cashPaymentReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCashPaymentReverseConfirmDialog();
            }
        });

        binding.cashPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCashPaymentConfirmDialog();
            }
        });
        binding.editInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditInvoiceActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                intent.putExtra(Extras.INVOICE_ID, viewmodel.latestInvoiceId);
                startActivity(intent);
            }
        });

        binding.billHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewInvoiceHistoryActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customer.getValue().getId());
                startActivity(intent);
            }
        });
        binding.callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndCall();
            }
        });
        binding.sendRemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = getShareIntent();
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        // hide
        binding.invoiceLayout.setVisibility(View.GONE);

        return binding.getRoot();
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TITLE, R.string.invite_share_title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getInviteShareText());
        return shareIntent;
    }

    private String getInviteShareText() {
        String mobileNumber = viewmodel.customer.getValue().getMobileNumber();
        String displayMobileNumber = getDisplayMobileNumber(mobileNumber);
        String welcomeText = String.format(getResources().getString(R.string.welcome_text), displayMobileNumber);
        if (viewmodel.merchant.getValue().getProductList().contains("Ondemand"))
            welcomeText = "Your " + viewmodel.merchant.getValue().getName() + " " + welcomeText;
        else
            welcomeText = "Your " + viewmodel.merchant.getValue().getName() + ", newspaper " + welcomeText;

        return welcomeText;
    }

    private void checkPermissionAndCall() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            callCustomer();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }

    }

    @SuppressLint("MissingPermission")
    private void callCustomer() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + viewmodel.customer.getValue().getMobileNumber()));
        startActivity(intent);
    }

    private void showCashPaymentConfirmDialog() {
        final String[] listItems = {"CASH", "CHEQUE", "GPAY", "PAYTM", "PHONEPE", "UPI", "OTHER"};
        final int[] pickedItem = new int[1];

        AlertDialog.Builder firstDialog = new AlertDialog.Builder(getActivity());
        final View viewAdvancePayment = LayoutInflater.from(ViewCustomerFragment.this.getActivity()).inflate(R.layout.advance_payment, null);
        String a = new DecimalFormat("###0.00").format(invAmount);
        ((EditText) viewAdvancePayment.findViewById(R.id.editTXTadvpayment)).setText(a);
        firstDialog.setView(viewAdvancePayment);

        int checkedItem = 0; //this will checked the item when user open the dialog
        firstDialog.setTitle("Select the Payment Type ");
        //Please refer
        //https://stackoverflow.com/questions/22559706/alertdialog-why-cant-i-show-message-and-list-together

        firstDialog.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pickedItem[0] = which;
                //Toast.makeText(getActivity(), "Position: " + which + " Value: " + listItems[which], Toast.LENGTH_LONG).show();
            }
        });

        firstDialog.setPositiveButton(R.string.payment_option_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        firstDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = firstDialog.create();
        dialog.show();

        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText_invAmount = (((EditText) viewAdvancePayment.findViewById(R.id.editTXTadvpayment)).getText().toString());
                editText_invAmount = editText_invAmount.trim();
                viewmodel.partAdvanceAmount = editText_invAmount;

                Log.d("CSD", editText_invAmount);
                if (((viewmodel.partAdvanceAmount) != null) && ((viewmodel.partAdvanceAmount).length() > 0) && ((viewmodel.partAdvanceAmount) != "0")) {
                    showSecondDialog(listItems[pickedItem[0]]);
                    dialog.dismiss();
                } else
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            "Please enter valid amount",
                            Toast.LENGTH_LONG);
            }
        });
    }

    private void showSecondDialog(final String paymentOption) {
        final Double totalDue = viewmodel.customer.getValue().getTotalDue();
        Log.d("CSD", "EDIT  TXT inv AMOUNT " + editText_invAmount);
        String message = String.format(getResources().getString(R.string.accept_cash_payment_confirm_messgae), FormatUtil.AMOUNT_FORMAT.format(Double.valueOf(editText_invAmount.replace(",", "")))); //totalDue
        AlertDialog.Builder secondDialog = new AlertDialog.Builder(getActivity());
        secondDialog.setTitle(R.string.confirm_cash_payment_received)
                .setMessage(message)
                .setPositiveButton(R.string.cash_payment_received, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = secondDialog.create();

        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    viewmodel.cashPaymentAction = paymentOption;

                    if (invAmount.equals(Double.valueOf(viewmodel.partAdvanceAmount))) {
                        // System.out.println("Cash Receive __ FULL");
                        viewmodel.acceptPayment();
                    }else {
                        // System.out.println("Cash Receive __ PART");
                        viewmodel.acceptPartPayment();

                    }

                    dialog.dismiss();
                }
            }
        });
    }

    private void showCashPaymentReverseConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_cash_payment_reverse)
                .setMessage(R.string.confirm_cash_payment_reverse_msg)
                .setPositiveButton(R.string.button_yes_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                    viewmodel.cashPaymentAction = "REVERSE";
                    viewmodel.acceptPayment();
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = ViewCustomerActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data
                //List<String> productList = merchant.getProductList();
                if (merchant.getProductList().contains("Ondemand"))
                    binding.addItemButton.setVisibility(View.VISIBLE);
                else
                    binding.addItemButton.setVisibility(View.GONE);

            }
        });

        viewmodel.invoices.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(@Nullable List<Invoice> invoices) {
                if (invoices.size() > 0) {
                    Invoice invoice = getLatestInvoice(invoices);
                    binding.invoiceAmount.setText(
                            getActivity().getResources().getString(
                                    R.string.amount_with_rupee_prefix,
                                    FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoice.getInvoiceAmount()))
                    );
                    invAmount = invoice.getInvoiceAmount();


                    if (invoice.getInvoicePaidDate() != null) {
                        binding.invoicePaidDate.setText(FormatUtil.DATE_FORMAT_DMY.format(invoice.getInvoicePaidDate()));
                    } else {
                        binding.invoicePaidDateLinear.setVisibility(View.GONE);
                    }

                    int amountColor;
                    String paymentModeText;
                    /*Log.d("CSD","Status : "+invoice.getStatus());
                    Log.d("CSD","getPaymentMode : "+invoice.getPaymentMode());*/
                    if (invoice.getStatus().equals(InvoiceStatusEnum.PAID)) {
                        amountColor = getContext().getResources().getColor(R.color.text_primary);

                        PaymentModeEnum paymentMode = invoice.getPaymentMode();

                        if (paymentMode != null) {
                            paymentModeText = String.format(getContext().getString(R.string.payment_mode_label), paymentMode.name().toLowerCase());
                        } else {
                            paymentModeText = String.format(getContext().getString(R.string.payment_mode_label), "");  //TODO: for legacy entries, remove if not needed
                        }

                       /* if (!paymentModeText.equalsIgnoreCase("Paid Online")) {
                            binding.cashPaymentReverseButton.setVisibility(View.VISIBLE);
                        }*/

                        binding.cashPaymentButton.setVisibility(View.GONE);
                        binding.editInvoice.setVisibility(View.GONE);
                    } else {
                        amountColor = getContext().getResources().getColor(R.color.warning_red);
                        paymentModeText = getContext().getString(R.string.pending_label);

                        binding.cashPaymentButton.setVisibility(View.VISIBLE);
                    }
                    binding.invoiceAmount.setTextColor(amountColor);
                    binding.paymentMode.setText(paymentModeText);

                    binding.invoiceLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.invoiceLayout.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {
                String name = getName(customer);
                final String mobile = getDisplayMobileNumber(customer);
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
                    binding.mobileNumberLayout.setVisibility(View.VISIBLE);

                    binding.mobileNumberLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            copyMobileNumber(mobile);
                        }
                    });
                } else {
                    binding.nameMobileSeparator.setVisibility(View.GONE);
                    binding.mobileNumberLayout.setVisibility(View.GONE);
                }

                String email = customer.getEmail();
                boolean hasEmail = email != null && (email.trim().length() > 0 ? true : false);
                if (hasEmail) {
                    binding.email.setText(email);
                    binding.email.setVisibility(View.VISIBLE);
                } else {
                    binding.email.setVisibility(View.GONE);
                }

                binding.doorNumber.setText(customer.getDoorNumber());
                if (customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() > 0) {
                    String addressLine1 = Utils.getAddressLine1(getContext(), customer.getAddressLine1()).trim();
                    if (!addressLine1.isEmpty()) {
                        binding.addressLine1.setText(addressLine1);
                        binding.addressLine1Layout.setVisibility(View.VISIBLE);
                    } else {
                        binding.addressLine1Layout.setVisibility(View.GONE);
                    }
                } else {
                    binding.addressLine1Layout.setVisibility(View.GONE);
                }
                binding.addressLine2.setText(customer.getAddressLine2());
                binding.route.setText(customer.getRoute());

                if (customer.getNotes() != null && customer.getNotes().trim().length() > 0) {
                    binding.notes.setText(customer.getNotes());
                    binding.notesLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.notesLayout.setVisibility(View.GONE);
                }

                if (customer.getCreated() != null) {
                    String created = FormatUtil.DATE_FORMAT_DMMMY_HH_MM.format(customer.getCreated());
                    binding.created.setText(created);
                    binding.createdLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.createdLayout.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.subscriptions.observe(this, new Observer<List<Subscription>>() {
            @Override
            public void onChanged(@Nullable List<Subscription> subscriptions) {
                binding.subscriptionsContainer.removeAllViews();

                if (subscriptions.isEmpty()) {
                    binding.subscriptionsContainer.setVisibility(View.GONE);
                } else {
                    showSubscriptions(subscriptions);
                }
            }
        });

        setupApiCallObservers();
    }

    private Invoice getLatestInvoice(List<Invoice> invoices) {
        Collections.sort(invoices, new Comparator<Invoice>() {
            @Override
            public int compare(Invoice o1, Invoice o2) {
                return o2.getInvoiceDate().compareTo(o1.getInvoiceDate());
            }
        });
        Invoice latestInvoice = invoices.get(0);
        viewmodel.latestInvoiceId = latestInvoice.getId();
        return latestInvoice;
    }

    private void copyMobileNumber(String mobile) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", mobile);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), R.string.mobile_number_copied_toast_message, Toast.LENGTH_SHORT).show();
    }

    private void showSubscriptions(List<Subscription> subscriptions) {
        for (final Subscription subscription : subscriptions) {

            StatusEnum subscriptionStatus = subscription.getSubscriptionStatus();
            if (subscriptionStatus.name().equalsIgnoreCase(StatusEnum.ACTIVE.name())) {

                View view = LayoutInflater.from(ViewCustomerFragment.this.getActivity()).inflate(R.layout.customer_subscription_item, null);
                ((TextView) view.findViewById(R.id.name)).setText(subscription.getProduct().getName());

                String subscriptionDurationText = getSubscriptionDurationText(subscription);
                if (subscriptionDurationText.isEmpty()) {
                    view.findViewById(R.id.subscriptionDuration).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.subscriptionDuration)).setText(subscriptionDurationText);
                    view.findViewById(R.id.subscriptionDuration).setVisibility(View.VISIBLE);
                }

                if (subscription.getPauses() != null && subscription.getPauses().size() > 0) {
                    String stopDurationText = getCombinedStopDurationText(subscription);
                    if (stopDurationText.isEmpty()) {
                        view.findViewById(R.id.stopDuration).setVisibility(View.GONE);
                    } else {
                        ((TextView) view.findViewById(R.id.stopDuration)).setText(stopDurationText);
                        view.findViewById(R.id.stopDuration).setVisibility(View.VISIBLE);
                    }
                } else {
                    view.findViewById(R.id.stopDuration).setVisibility(View.GONE);
                }

                if (subscription.getTag() != null && subscription.getTag().trim().length() > 0) {
                    ((TextView) view.findViewById(R.id.tag)).setText(subscription.getTag());
                } else {
                    view.findViewById(R.id.tag).setVisibility(View.GONE);
                }

                if (isSubscriptionCustomized(subscription)) {
                    view.findViewById(R.id.customizedTag).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.customizedTag).setVisibility(View.GONE);
                }

                view.setTag(subscription.getId());

                view.findViewById(R.id.addPause).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                binding.subscriptionsContainer.addView(view);
            }
        }
    }

    private boolean isSubscriptionCustomized(Subscription subscription) {
        if (subscription.getPricingMode() != null) {
            return true;
        }

        if (subscription.isAnnualSubscription()) {
            return true;
        }

        return false;
    }

    private void setupApiCallObservers() {
        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.accept_cash_payment_running_message));
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
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
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
                    if (viewmodel.cashPaymentAction.equalsIgnoreCase("REVERSE"))
                        binding.cashPaymentReverseButton.setVisibility(View.GONE);

                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.accept_cash_payment_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.partPaymentApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.accept_cash_payment_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.partPaymentApiCallSuccess.setValue(false);
                   // viewmodel.generateInvoice();
                }
            }
        });

        viewmodel.partPaymentApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.partPaymentApiCallError.setValue(false);
                }
            }
        });

        viewmodel.partPaymentApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.part_payment_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.partPaymentApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.editNotesApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.edit_notes_update_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.editNotesApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.editNotesApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.edit_notes_update_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.editNotesApiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.editNotesApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.editNotesApiCallError.setValue(false);
                }
            }
        });

        viewmodel.generateInvoiceApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.generate_invoice_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.generateInvoiceApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.generateInvoiceApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.generate_invoice_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.generateInvoiceApiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.generateInvoiceApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.generateInvoiceApiCallError.setValue(false);
                }
            }
        });


        viewmodel.checkoutApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.checkout_customer_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.checkoutApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.checkoutApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.checkout_customer_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.checkoutApiCallSuccess.setValue(false);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        viewmodel.checkoutApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.checkoutApiCallError.setValue(false);
                }
            }
        });


        viewmodel.releaseCurrentInvoiceApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.releasing_invoice_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.releaseCurrentInvoiceApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.releaseCurrentInvoiceApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.release_current_invoice_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.releaseCurrentInvoiceApiCallSuccess.setValue(false);
                   /* if(getActivity() != null){
                        getActivity().finish();
                    }*/
                }
            }
        });

        viewmodel.releaseCurrentInvoiceApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.releaseCurrentInvoiceApiCallError.setValue(false);
                }
            }
        });

        viewmodel.deleteCustomerApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.delete_customer_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.deleteCustomerApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.deleteCustomerApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(ViewCustomerFragment.this.getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.delete_customer_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.deleteCustomerApiCallSuccess.setValue(false);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        viewmodel.deleteCustomerApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.deleteCustomerApiCallError.setValue(false);
                }
            }
        });

        viewmodel.updateMobileApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(ViewCustomerFragment.this.getActivity());
                    progressDialog.setMessage(ViewCustomerFragment.this.getActivity().getResources().getString(R.string.update_customer_mobile_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.updateMobileApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.updateMobileApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            ViewCustomerFragment.this.getActivity().getResources().getString(R.string.customer_mobile_added),
                            Toast.LENGTH_LONG);
                    viewmodel.updateMobileApiCallSuccess.setValue(false);

                }
            }
        });

        viewmodel.updateMobileApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(ViewCustomerFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.updateMobileApiCallError.setValue(false);
                }
            }
        });

    }

    private String getCombinedStopDurationText(Subscription subscription) {
        StringBuilder stringBuilder = new StringBuilder();
        String prefix = "";
        List<Pause> pauses = subscription.getPauses();

        SubscriptionUtil.sortPausesAscending((pauses));

        for (int i = 0; i < pauses.size(); i++) {
            Pause pause = pauses.get(i);
            StatusEnum pauseStatus = pause.getPauseStatus();

            if (pauseStatus.name().equalsIgnoreCase(StatusEnum.ACTIVE.name())) {
                String stopDeliveryDurationText = SubscriptionUtil.getPauseDurationText(pause, getContext());
                stringBuilder.append(prefix);
                prefix = "\n";
                stringBuilder.append(stopDeliveryDurationText);
            }
        }
        return stringBuilder.toString();
    }

    private String getSubscriptionDurationText(Subscription subscription) {
        String subscriptionDurationText = "";
        String startDateText = getStartDateText(subscription.getStartDate());
        String endDateText = getEndDateText(subscription.getEndDate());

        if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
            subscriptionDurationText = String.format(getResources().getString(R.string.subscription_starts_ends_label), startDateText, endDateText);
        } else if (!startDateText.isEmpty() && endDateText.isEmpty()) {
            subscriptionDurationText = String.format(getResources().getString(R.string.subscription_starts_label), startDateText);
        } else if (startDateText.isEmpty() && !endDateText.isEmpty()) {
            subscriptionDurationText = String.format(getResources().getString(R.string.subscription_ends_label), endDateText);
        }

        return subscriptionDurationText;
    }

    private String getStartDateText(Date startDate) {
        String startDateText;

        boolean startsCurrentMonth;
        boolean startsCurrentYear;
        boolean startsSoon;

        Calendar subscriptionStartDateCalendar = FormatUtil.getLocalCalender();
        subscriptionStartDateCalendar.setTime(startDate);

        if (subscriptionStartDateCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
            startsCurrentMonth = true;
        } else {
            startsCurrentMonth = false;
        }

        if (subscriptionStartDateCalendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            startsCurrentYear = true;
        } else {
            startsCurrentYear = false;
        }

        boolean upcomingMonth = subscriptionStartDateCalendar.get(Calendar.MONTH) >= Calendar.getInstance().get(Calendar.MONTH);
        boolean upcomingYear = subscriptionStartDateCalendar.get(Calendar.YEAR) >= Calendar.getInstance().get(Calendar.YEAR);
        if (upcomingMonth && upcomingYear) {
            startsSoon = true;
        } else {
            startsSoon = false;
        }

        if (startsCurrentMonth && startsCurrentYear) {
            //show day, month
            startDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DM, startDate);
        } else if (startsSoon) {
            //show day, month and year
            startDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, startDate);
        } else {
            startDateText = "";
        }
        return startDateText;
    }

    private String getEndDateText(Date endDate) {
        String endDateText = "";

        if (endDate != null) {
            boolean endsCurrentMonth;
            boolean endsCurrentYear;

            Calendar subscriptionEndDateCalender = FormatUtil.getLocalCalender();
            subscriptionEndDateCalender.setTime(endDate);

            if (subscriptionEndDateCalender.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                endsCurrentMonth = true;
            } else {
                endsCurrentMonth = false;
            }

            if (subscriptionEndDateCalender.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                endsCurrentYear = true;
            } else {
                endsCurrentYear = false;
            }

            if (endsCurrentMonth && endsCurrentYear) {
                //show day, month
                endDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DM, endDate);
            } else if (endsCurrentMonth && !endsCurrentYear) {
                //show day, month and year
                endDateText = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, endDate);
            } else {
                endDateText = "";
            }
        }
        return endDateText;
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

    private String getDisplayMobileNumber(String mobileNumber) {
        return mobileNumber.replaceAll("^\\+91", "");
    }
}

