package com.zopnote.android.merchant.reports.settlement;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.SettlementInfo;
import com.zopnote.android.merchant.databinding.SettlementReportItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.List;

public class SettlementReportAdapter extends BaseRecyclerAdapter<SettlementInfo, SettlementReportAdapter.ReportViewHolder> {

    public SettlementReportAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<SettlementInfo> newItems) {
        SettlementInfo oldItem = getItems().get(oldItemPosition);
        SettlementInfo newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getAmount(), newItem.getAmount());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<SettlementInfo> newItems) {
        SettlementInfo oldItem = getItems().get(oldItemPosition);
        SettlementInfo newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getAmount(), newItem.getAmount()) &&
                ObjectsUtil.equals(oldItem.getCharges(), newItem.getCharges()) &&
                ObjectsUtil.equals(oldItem.getTransferredAmount(), newItem.getTransferredAmount());
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SettlementReportItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.settlement_report_item,
                        parent, false);
        final ReportViewHolder vh = new ReportViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {
        SettlementInfo settlementInfo = getItems().get(position);

        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, settlementInfo.getDate());
        holder.binding.date.setText(date);

        String formattedAmount = FormatUtil.getRupeePrefixedAmount(getContext(), settlementInfo.getAmount(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        String formattedCharges = FormatUtil.getRupeePrefixedAmount(getContext(), settlementInfo.getCharges(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        String amountAndCharges = String.format("%s - %s", formattedAmount, formattedCharges);
        holder.binding.amountAndCharges.setText(amountAndCharges);
        holder.binding.transferred.setText(FormatUtil.getRupeePrefixedAmount(getContext(), settlementInfo.getTransferredAmount(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
    }



    public class ReportViewHolder extends RecyclerView.ViewHolder {
        final SettlementReportItemBinding binding;

        public ReportViewHolder(SettlementReportItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
