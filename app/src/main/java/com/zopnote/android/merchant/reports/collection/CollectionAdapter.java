package com.zopnote.android.merchant.reports.collection;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Collection;
import com.zopnote.android.merchant.databinding.CollectionItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerActivity;

import java.util.List;

public class CollectionAdapter extends BaseRecyclerAdapter<Collection, CollectionAdapter.CollectionViewHolder> {
    private CollectionViewModel viewmodel;

    public CollectionAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Collection> newItems) {
        Collection oldItem = getItems().get(oldItemPosition);
        Collection newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Collection> newItems) {
        Collection oldItem = getItems().get(oldItemPosition);
        Collection newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId())
                && ObjectsUtil.equals(oldItem.getDoorNumber(), newItem.getDoorNumber())
                && ObjectsUtil.equals(oldItem.getInvoiceAmount(), newItem.getInvoiceAmount())
                && ObjectsUtil.equals(oldItem.getStatus(), newItem.getInvoiceAmount())
                && ObjectsUtil.equals(oldItem.getAddressLine1(), newItem.getStatus())
                && ObjectsUtil.equals(oldItem.getAddressLine2(), newItem.getAddressLine2());
    }

    @Override
    public CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.collection_item,
                        parent, false);
        final CollectionViewHolder vh = new CollectionViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CollectionViewHolder holder, int position) {
        final Collection collectionItem = getItems().get(position);
        holder.binding.doorNumber.setText(collectionItem.getDoorNumber());

        if (collectionItem.getAddressLine1() != null && collectionItem.getAddressLine1().trim().length() > 0) {
            String addressLine1 = Utils.getAddressLine1(getContext(), collectionItem.getAddressLine1()).trim();
            if( ! addressLine1.isEmpty()){
                holder.binding.addressLine1.setText(addressLine1);
                holder.binding.addressLine1Layout.setVisibility(View.VISIBLE);
            }else{
                holder.binding.addressLine1Layout.setVisibility(View.GONE);
            }
        } else {
            holder.binding.addressLine1Layout.setVisibility(View.GONE);
        }

        if (collectionItem.getAddressLine2() != null && collectionItem.getAddressLine2().trim().length() > 0) {
            holder.binding.addressLine2.setText(collectionItem.getAddressLine2());
            holder.binding.addressLine2.setVisibility(View.VISIBLE);
        } else {
            holder.binding.addressLine2.setVisibility(View.GONE);
        }

        if (collectionItem.getInvoiceDate() != null ) {
            holder.binding.invoiceDate.setText(FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_D_MMM, collectionItem.getInvoiceDate()));
            holder.binding.invoiceDate.setVisibility(View.VISIBLE);
        } else {
            holder.binding.invoiceDate.setVisibility(View.GONE);
        }

        holder.binding.invoiceAmount.setText(FormatUtil.
                getRupeePrefixedAmount(getContext(),
                        collectionItem.getInvoiceAmount(), FormatUtil.AMOUNT_FORMAT));
        String invoiceStatus= "";

        if(collectionItem.getStatus().equalsIgnoreCase("pending")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.warning_red));
            invoiceStatus = "Pending";

        }else if(collectionItem.getStatus().equalsIgnoreCase("online")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Online";

        }else if(collectionItem.getStatus().equalsIgnoreCase("cash")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Cash";
        }
        else if(collectionItem.getStatus().equalsIgnoreCase("CHEQUE")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Cheque";
        }
        else if(collectionItem.getStatus().equalsIgnoreCase("GPAY")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "GPay";
        }
        else if(collectionItem.getStatus().equalsIgnoreCase("PAYTM")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Paytm";
        }
        else if(collectionItem.getStatus().equalsIgnoreCase("PHONEPE")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Phonepe";
        }
        else if(collectionItem.getStatus().equalsIgnoreCase("UPI")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Upi";
        }
        else if(collectionItem.getStatus().equalsIgnoreCase("Other")){
            holder.binding.invoiceAmount.setTextColor(getContext().getResources().getColor(R.color.accent));
            invoiceStatus = "Other";
        }
        holder.binding.invoiceStatus.setText(invoiceStatus);

        if (collectionItem.getInvoicePaidDate() != null ) {
            holder.binding.invoicePaidDate.setText(FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_D_MMM, collectionItem.getInvoicePaidDate()));
            holder.binding.invoicePaidDate.setVisibility(View.VISIBLE);
        } else {
            holder.binding.invoicePaidDate.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewCustomerActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, collectionItem.getId());
                getContext().startActivity(intent);
            }
        });
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {

        final CollectionItemBinding binding;

        public CollectionViewHolder(CollectionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setViewModel(CollectionViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }
}
