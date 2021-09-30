package com.zopnote.android.merchant.movecustomer;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.databinding.CustomerItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Utils;

import java.util.List;

public class MoveCustomerAdapter extends BaseRecyclerAdapter<Customer, MoveCustomerAdapter.CustomerViewHolder> {

    private MoveCustomerViewModel viewmodel;

    public MoveCustomerAdapter(Context context) {
        super(context);
    }

    public void setViewModel(MoveCustomerViewModel viewmodel){
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
                && ObjectsUtil.equals(oldItem.getAddressLine2(), newItem.getAddressLine2());
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
    public void onBindViewHolder(final CustomerViewHolder holder, final int position) {
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
        } else {
            holder.binding.invoiceAmountLayout.setVisibility(View.GONE);
        }

        if (customer.getInvoiceStatus() != null && customer.getInvoiceStatus() == InvoiceStatusEnum.PAID) {
            holder.binding.invoicePaidStatusLayout.setVisibility(View.VISIBLE);
        } else {
            holder.binding.invoicePaidStatusLayout.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Customer previousCustomer = customer;
                Customer nextCustomer = null;
                if (getItemCount() > position + 1) {
                    nextCustomer = getItems().get(position + 1);
                }
             showConfirmMoveDialog(previousCustomer, nextCustomer);
            }
        });
    }

    public void showConfirmMoveDialog(final Customer previousCustomer, final Customer nextCustomer) {
        String message = getMoveCustomerConfirmationText(previousCustomer);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setPositiveButton(R.string.button_yes_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkUtil.enforceNetworkConnection(getContext())) {
                            viewmodel.moveCustomer(previousCustomer, nextCustomer);
                        }
                    }
                })
                .setNegativeButton(R.string.button_no_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private String getMoveCustomerConfirmationText(Customer previousCustomer) {

        String previousDoorNumber = previousCustomer.getDoorNumber();
        String previousAddressLine1 = previousCustomer.getAddressLine1();
        StringBuilder previousBuilder = new StringBuilder();
        previousBuilder.append(previousDoorNumber);
        if(previousAddressLine1 != null && ! previousAddressLine1.isEmpty()){
            String prevAddrLine1 = Utils.getAddressLine1(getContext(), previousAddressLine1).trim();
            if( ! prevAddrLine1.isEmpty()){
                previousBuilder.append(" - ");
                previousBuilder.append(prevAddrLine1);
            }
        }

        String doorNumber = viewmodel.customer.getValue().getDoorNumber();
        String addressLine1 = viewmodel.customer.getValue().getAddressLine1();
        StringBuilder builder = new StringBuilder();
        builder.append(doorNumber);
        if(addressLine1 != null && ! addressLine1.isEmpty()){
            String addrLine1 = Utils.getAddressLine1(getContext(), addressLine1).trim();
            if( ! addrLine1.isEmpty()){
                builder.append(" - ");
                builder.append(addrLine1);
            }
        }
        return String.format(getContext().getResources().getString(R.string.move_customer_confirmation_text),
                builder.toString(),
                previousBuilder.toString());
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

    static class CustomerViewHolder extends RecyclerView.ViewHolder {

        final CustomerItemBinding binding;

        public CustomerViewHolder(CustomerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
