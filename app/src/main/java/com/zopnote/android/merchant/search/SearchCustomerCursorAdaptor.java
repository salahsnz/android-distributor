package com.zopnote.android.merchant.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.addondemanditem.AddOnDemandActivity;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.customers.CustomersViewModel;
import com.zopnote.android.merchant.data.database.CustomerDbHelper;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.databinding.CustomerItemBinding;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerActivity;

public class SearchCustomerCursorAdaptor extends CursorAdapter {
    private String queryText;

    public SearchCustomerCursorAdaptor(Context context, Cursor cursor, String query) {
        super(context, cursor, false);
        mContext = context;
        queryText = query;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        CustomerItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.customer_item,
                        parent, false);
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final CustomerItemBinding binding = DataBindingUtil.getBinding(view);

        final Customer customer = CustomerDbHelper.fromCursor(cursor);

        String name = getName(customer);
        String mobile = getDisplayMobileNumber(customer);
        boolean hasName = name.trim().length() > 0 ? true : false;
        boolean hasMobile = Utils.hasValidMobileNumber(mobile);

        if (hasName) {
            if (!queryText.isEmpty()) {
                int startPos = name.toLowerCase().indexOf(queryText.toLowerCase());
                int endPos = startPos + queryText.length();

                if (startPos != -1) {
                    binding.name.setText(getSpannable(name, startPos, endPos));
                } else {
                    binding.name.setText(name);
                }
            } else {
                binding.name.setText(name);
            }
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


            if (!queryText.isEmpty() && queryText.length()>5) {
                int startPos = mobile.toLowerCase().indexOf(queryText.toLowerCase());
                int endPos = startPos + queryText.length();

                if (startPos != -1) {
                    binding.mobileNumber.setText(getSpannable(mobile, startPos, endPos));
                } else {
                    binding.mobileNumber.setText(name);
                }
            } else {
                binding.mobileNumber.setText(mobile);
            }


            binding.nameMobileSeparator.setVisibility(View.VISIBLE);
            binding.mobileNumber.setVisibility(View.VISIBLE);
        }else {
            binding.nameMobileSeparator.setVisibility(View.GONE);
            binding.mobileNumber.setVisibility(View.GONE);
        }

        String doorNumber = customer.getDoorNumber();
        if (!queryText.isEmpty()) {
            int startPos = doorNumber.toLowerCase().indexOf(queryText.toLowerCase());
            int endPos = startPos + queryText.length();

            if (startPos != -1) {
                binding.doorNumber.setText(getSpannable(doorNumber, startPos, endPos));
            }else {
                binding.doorNumber.setText(doorNumber);
            }
        } else {
            binding.doorNumber.setText(doorNumber);
        }

        if (customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() > 0) {
            String addressLine1 = Utils.getAddressLine1(mContext, customer.getAddressLine1()).trim();
            if( ! addressLine1.isEmpty()){
                binding.addressLine1.setText(addressLine1);
                binding.addressLine1Layout.setVisibility(View.VISIBLE);
            }else{
                binding.addressLine1Layout.setVisibility(View.GONE);
            }
        } else {
            binding.addressLine1Layout.setVisibility(View.GONE);
        }

        if (customer.getAddressLine2() != null && customer.getAddressLine2().trim().length() > 0) {
            binding.addressLine2.setText(customer.getAddressLine2());
            binding.addressLine2.setVisibility(View.VISIBLE);
        } else {
            binding.addressLine2.setVisibility(View.GONE);
        }

        if (customer.getTotalDue() != null && customer.getTotalDue() > 0) {
            binding.invoiceAmount.setText(FormatUtil.AMOUNT_FORMAT.format(customer.getTotalDue()));
            binding.invoiceAmountLayout.setVisibility(View.VISIBLE);
        } else {
            binding.invoiceAmountLayout.setVisibility(View.GONE);
        }

        if (customer.getInvoiceStatus() != null && customer.getInvoiceStatus() == InvoiceStatusEnum.PAID) {
            binding.invoicePaidStatusLayout.setVisibility(View.VISIBLE);
        } else {
            binding.invoicePaidStatusLayout.setVisibility(View.GONE);
        }

        if (customer.getMobileNumber() == null || customer.getMobileNumber().startsWith("1")) {
            binding.highlightBar.setVisibility(View.VISIBLE);
        } else {
            binding.highlightBar.setVisibility(View.GONE);
        }
        
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewCustomerActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, customer.getId());
                mContext.startActivity(intent);

                logClickEvent(customer.getId());
            }
        });

          binding.addNewOrder.setVisibility(View.GONE);
       
    }

    private Spannable getSpannable(String text, int startPos, int endPos) {
        Spannable spannable = new SpannableString(text);
        ColorStateList blackColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLACK});
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blackColor, null);
        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
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

    private void logClickEvent(String customerId) {
        Analytics.Builder analyticsBuilder = new Analytics.Builder()
                .setEventName(Event.NAV_CUSTOMER)
                .addParam(Param.CUSTOMER_ID, customerId);
        analyticsBuilder.logEvent();

    }
}