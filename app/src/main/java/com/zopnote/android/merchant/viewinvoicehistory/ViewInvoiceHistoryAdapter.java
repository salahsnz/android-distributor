package com.zopnote.android.merchant.viewinvoicehistory;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.PaymentModeEnum;
import com.zopnote.android.merchant.databinding.InvoicehistoryItemBinding;
import com.zopnote.android.merchant.invoice.InvoiceActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.List;


public class ViewInvoiceHistoryAdapter extends BaseRecyclerAdapter<Invoice, ViewInvoiceHistoryAdapter.InvoiceHistory> {

    private ViewModelInvoiceHistory viewModelInvoiceHistory;

    public ViewInvoiceHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Invoice> newItems) {
        Invoice oldItem = getItems().get(oldItemPosition);
        Invoice newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Invoice> newItems) {
        Invoice oldItem = getItems().get(oldItemPosition);
        Invoice newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId())
                && ObjectsUtil.equals(oldItem.getInvoiceAmount(), newItem.getInvoiceAmount())
                && ObjectsUtil.equals(oldItem.getInvoiceNumber(), newItem.getInvoiceNumber())
                && ObjectsUtil.equals(oldItem.getInvoiceDate(), newItem.getInvoiceDate())
                && ObjectsUtil.equals(oldItem.getInvoiceAmount(), newItem.getInvoiceAmount())
                && ObjectsUtil.equals(oldItem.getDueDate(), newItem.getDueDate());
    }

    @Override
    public InvoiceHistory onCreateViewHolder(ViewGroup parent, int viewType) {
        InvoicehistoryItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.invoicehistory_item,
                        parent, false);
        final InvoiceHistory vh = new InvoiceHistory(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final InvoiceHistory holder, int position) {
        final Invoice invoice = getItems().get(position);

        holder.binding.invoiceMerchantName.setText(viewModelInvoiceHistory.merchantName);
        holder.binding.invoiceNo.setText(invoice.getInvoiceNumber());
        holder.binding.invoiceDate.setText(FormatUtil.DATE_FORMAT_DMY.format(invoice.getInvoiceDate()));
        holder.binding.invoiceInvoicePeriod.setText(invoice.getInvoicePeriod());

        if(invoice.getDueDate() != null){
            holder.binding.invoiceDueDate.setText(FormatUtil.DATE_FORMAT_DMY.format(invoice.getDueDate()));
        }else {
            holder.binding.invoiceDueDateLabel.setVisibility(View.GONE);
            holder.binding.invoiceDueDate.setVisibility(View.GONE);
        }

        if(invoice.getInvoicePaidDate() != null){
            holder.binding.invoicePaidDate.setText(FormatUtil.DATE_FORMAT_DMY.format(invoice.getInvoicePaidDate()));
        }else {
            holder.binding.invoicePaidDateLabel.setVisibility(View.GONE);
            holder.binding.invoicePaidDate.setVisibility(View.GONE);
        }

        holder.binding.invoiceAmount.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoice.getInvoiceAmount()));
        if(invoice.getStatus().equals(InvoiceStatusEnum.PAID)){
            String paymentModeText;
            PaymentModeEnum paymentMode = invoice.getPaymentMode();
            if(paymentMode != null){
                paymentModeText = String.format(getContext().getString(R.string.payment_mode_label), paymentMode.name().toLowerCase());
            }else{
                paymentModeText = String.format(getContext().getString(R.string.payment_mode_label),"");  //TODO: for legacy entries, remove if not needed
            }
            holder.binding.invoiceNotPaidStatusText.setVisibility(View.GONE
            );
            holder.binding.invoicePaidStatusText.setVisibility(View.VISIBLE);
            holder.binding.invoicePaidStatusText.setText(paymentModeText);
        }else {
            holder.binding.invoicePaidStatusText.setVisibility(View.GONE);
            holder.binding.invoiceNotPaidStatusText.setVisibility(View.VISIBLE);
        }
        holder.binding.viewBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), InvoiceActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, invoice.getCustomer().getId());
                intent.putExtra(Extras.INVOICE_ID, invoice.getId());
                getContext().startActivity(intent);
            }
        });

    }


    static class InvoiceHistory extends RecyclerView.ViewHolder {

        final InvoicehistoryItemBinding binding;

        public InvoiceHistory(InvoicehistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setViewModel(ViewModelInvoiceHistory viewmodel) {
        this.viewModelInvoiceHistory = viewmodel;
    }
}
