package com.zopnote.android.merchant.customers;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.addondemanditem.AddOnDemandActivity;
import com.zopnote.android.merchant.addsubscription.AddSubscriptionViewModel;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.PaymentModeEnum;
import com.zopnote.android.merchant.databinding.CustomerItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerActivity;

import java.util.List;

/**
 * Created by nmohideen on 30/01/18.
 */

public class CustomersAdapter extends BaseRecyclerAdapter<Customer, CustomersAdapter.CustomerViewHolder> {
    private CustomersViewModel viewmodel;
    public CustomersAdapter(Context context) {
        super(context);
    }

    public void setViewModel(CustomersViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }
    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Customer> newItems) {
        Customer oldItem = getItems().get(oldItemPosition);
        Customer newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Customer> newItems) {
        Customer oldItem = getItems().get(oldItemPosition);
        Customer newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId())
                && ObjectsUtil.equals(oldItem.getMobileNumber(), newItem.getMobileNumber())
                && ObjectsUtil.equals(oldItem.getDoorNumber(), newItem.getDoorNumber())
                && ObjectsUtil.equals(oldItem.getAddressLine1(), newItem.getAddressLine1())
                && ObjectsUtil.equals(oldItem.getAddressLine2(), newItem.getAddressLine2())
                && ObjectsUtil.equals(oldItem.getTotalDue(), newItem.getTotalDue())
                && ObjectsUtil.equals(oldItem.getInvoiceStatus(), newItem.getInvoiceStatus());
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomerItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.customer_item,
                        parent, false);
        final CustomerViewHolder vh = new CustomerViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomerViewHolder holder, int position) {
        final Customer customer = getItems().get(position);

        String name = getName(customer);
        boolean hasName = name.trim().length() > 0 ? true : false;

        if (hasName) {
            holder.binding.name.setText(name);
            holder.binding.name.setVisibility(View.VISIBLE);
        } else {
            holder.binding.name.setVisibility(View.GONE);
        }

        // not showing mobile in list view
        holder.binding.nameMobileSeparator.setVisibility(View.GONE);
        holder.binding.mobileNumber.setVisibility(View.GONE);

        holder.binding.doorNumber.setText(customer.getDoorNumber());

        if (customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() > 0) {
            String addressLine1 = Utils.getAddressLine1(getContext(), customer.getAddressLine1()).trim();
            if( ! addressLine1.isEmpty()){
                holder.binding.addressLine1.setText(addressLine1);
                holder.binding.addressLine1Layout.setVisibility(View.VISIBLE);
            }else{
                holder.binding.addressLine1Layout.setVisibility(View.GONE);
            }
        } else {
            holder.binding.addressLine1Layout.setVisibility(View.GONE);
        }

        if (customer.getAddressLine2() != null && customer.getAddressLine2().trim().length() > 0) {
            holder.binding.addressLine2.setText(customer.getAddressLine2());
            holder.binding.addressLine2.setVisibility(View.VISIBLE);
        } else {
            holder.binding.addressLine2.setVisibility(View.GONE);
        }

        if (customer.getTotalDue() != null && customer.getTotalDue() > 0) {
            holder.binding.invoiceAmount.setText(FormatUtil.AMOUNT_FORMAT.format(customer.getTotalDue()));
            holder.binding.invoiceAmountLayout.setVisibility(View.VISIBLE);
            Log.d("CSD",customer.getFirstName()+" --- "+customer.getTotalDue());
        } else {
            holder.binding.invoiceAmountLayout.setVisibility(View.GONE);
        }

        if (customer.getInvoiceStatus() != null && customer.getInvoiceStatus() == InvoiceStatusEnum.PAID) {
            holder.binding.invoicePaidStatusLayout.setVisibility(View.VISIBLE);
           /* if (customer.getTotalDue()==0) {
                 holder.binding.invoicePaidStatusLayout.setVisibility(View.VISIBLE);
                 holder.binding.invoicePaidAmount.setText(FormatUtil.AMOUNT_FORMAT.format(customer.getTotalDue()));
           }else {
                Double paidAmount = (customer.getTotalDue())*(-1);
                holder.binding.invoicePaidAmount.setText(FormatUtil.AMOUNT_FORMAT.format(paidAmount));
            }*/
        } else {
            holder.binding.invoicePaidStatusLayout.setVisibility(View.GONE);
        }

        if (customer.getMobileNumber() == null || customer.getMobileNumber().startsWith("+911")) {
            holder.binding.highlightBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.highlightBar.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewCustomerActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, customer.getId());
                getContext().startActivity(intent);

                logClickEvent(customer.getId());
            }
        });
        if (viewmodel.merchant.getValue().getProductList().contains("Ondemand")){
            holder.binding.addNewOrder.setVisibility(View.VISIBLE);
            holder.binding.addNewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AddOnDemandActivity.class);
                    intent.putExtra(Extras.CUSTOMER_ID, customer.getId());
                    getContext().startActivity(intent);
                }
            });
        }else {
            holder.binding.addNewOrder.setVisibility(View.GONE);
        }

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

    static class CustomerViewHolder extends RecyclerView.ViewHolder {

        final CustomerItemBinding binding;

        public CustomerViewHolder(CustomerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
