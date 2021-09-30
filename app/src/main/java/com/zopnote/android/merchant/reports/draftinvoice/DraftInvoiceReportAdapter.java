package com.zopnote.android.merchant.reports.draftinvoice;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.DraftInvoiceItem;
import com.zopnote.android.merchant.data.model.DraftInvoiceReportItem;
import com.zopnote.android.merchant.databinding.DraftInvoiceReportItemBinding;
import com.zopnote.android.merchant.editdraftinvoice.EditDraftInvoiceActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Utils;

import java.util.List;

public class DraftInvoiceReportAdapter extends BaseRecyclerAdapter<DraftInvoiceReportItem, DraftInvoiceReportAdapter.DraftInvoiceViewHolder> {

    public DraftInvoiceReportAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<DraftInvoiceReportItem> newItems) {
        DraftInvoiceReportItem oldItem = getItems().get(oldItemPosition);
        DraftInvoiceReportItem newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getCustomerId(), newItem.getCustomerId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<DraftInvoiceReportItem> newItems) {
        DraftInvoiceReportItem oldItem = getItems().get(oldItemPosition);
        DraftInvoiceReportItem newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getCustomerId(), newItem.getCustomerId()) &&
                ObjectsUtil.equals(oldItem.getInvoice().getInvoiceAmount(), newItem.getInvoice().getInvoiceAmount()) &&
                ObjectsUtil.equals(oldItem.getInvoice().getInvoiceItems(), newItem.getInvoice().getInvoiceItems());
    }

    @Override
    public DraftInvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DraftInvoiceReportItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.draft_invoice_report_item,
                        parent, false);
        final DraftInvoiceViewHolder vh = new DraftInvoiceViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DraftInvoiceViewHolder holder, int position) {
        final DraftInvoiceReportItem reportItem = getItems().get(position);
        if(reportItem != null){

            ((TextView)holder.itemView.findViewById(R.id.doorNumber)).setText(reportItem.getDoorNumber());

            if (reportItem.getAddressLine1() != null && reportItem.getAddressLine1().trim().length() > 0) {
                String addressLine1 = Utils.getAddressLine1(getContext(), reportItem.getAddressLine1()).trim();
                if( ! addressLine1.isEmpty()){
                    ((TextView)holder.itemView.findViewById(R.id.addressLine1)).setText(addressLine1);
                    holder.itemView.findViewById(R.id.addressLine1Layout).setVisibility(View.VISIBLE);
                }else{
                    holder.itemView.findViewById(R.id.addressLine1Layout).setVisibility(View.GONE);
                }
            }else {
                holder.itemView.findViewById(R.id.addressLine1Layout).setVisibility(View.GONE);
            }

            if (reportItem.getAddressLine2() != null && reportItem.getAddressLine2().trim().length() > 0) {
                ((TextView)holder.itemView.findViewById(R.id.addressLine2)).setText(reportItem.getAddressLine2());
                holder.itemView.findViewById(R.id.addressLine2).setVisibility(View.VISIBLE);
            } else {
                holder.itemView.findViewById(R.id.addressLine2).setVisibility(View.GONE);
            }

            if(reportItem.getInvoice() != null && ! reportItem.getInvoice().getInvoiceItems().isEmpty()){
                holder.itemView.findViewById(R.id.invoiceItemsContainer).setVisibility(View.VISIBLE);

                addInvoiceItemsViews(holder.itemView, reportItem.getInvoice().getInvoiceItems());
            }else {
                holder.itemView.findViewById(R.id.invoiceItemsContainer).setVisibility(View.GONE);
            }

            if(reportItem.getInvoice() != null && reportItem.getInvoice().getInvoiceAmount() != null){
                holder.binding.totalDueAtBottom.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(reportItem.getInvoice().getInvoiceAmount()));
                holder.binding.totalDueLayout.setVisibility(View.VISIBLE);
            }else{
                holder.binding.totalDueLayout.setVisibility(View.GONE);
            }

            if (reportItem.getNotes() != null && ! reportItem.getNotes().trim().isEmpty()) {
                holder.binding.notesLayout.setVisibility(View.VISIBLE);
                holder.binding.notes.setText(reportItem.getNotes());

                holder.binding.highlightBar.setVisibility(View.VISIBLE);
            } else {
                holder.binding.notesLayout.setVisibility(View.GONE);

                holder.binding.highlightBar.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EditDraftInvoiceActivity.class);
                    intent.putExtra(Extras.INVOICE_ID, reportItem.getInvoice().getId());
                    intent.putExtra(Extras.CUSTOMER_ID, reportItem.getCustomerId());
                    getContext().startActivity(intent);
                }
            });
        }
    }

    private void addInvoiceItemsViews(View view, List<DraftInvoiceItem> draftInvoiceItems) {
        LinearLayout invoiceItemsContainer = view.findViewById(R.id.invoiceItemsContainer);
        invoiceItemsContainer.removeAllViews();
        for (int i = 0; i < draftInvoiceItems.size(); i++) {
            DraftInvoiceItem draftInvoiceItem = draftInvoiceItems.get(i);

            View draftInvoiceItemView = LayoutInflater.from(getContext()).inflate(R.layout.draft_invoice_report_invoice_item, invoiceItemsContainer, false);
            ((TextView)draftInvoiceItemView.findViewById(R.id.invoiceItemName)).setText(draftInvoiceItem.getItem());

            ((TextView)draftInvoiceItemView.findViewById(R.id.invoiceItemAmount)).setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(draftInvoiceItem.getAmount()));
            invoiceItemsContainer.addView(draftInvoiceItemView);
        }
    }

    static class DraftInvoiceViewHolder extends RecyclerView.ViewHolder {

        final DraftInvoiceReportItemBinding binding;

        public DraftInvoiceViewHolder(DraftInvoiceReportItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
