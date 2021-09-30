package com.zopnote.android.merchant.reports.onboarding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.OnboardReportItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.List;

public class OnboardReportAdapter extends BaseRecyclerAdapter<OnboardInfo, OnboardReportAdapter.ReportViewHolder> {

    public OnboardReportAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<OnboardInfo> newItems) {
        OnboardInfo oldItem = getItems().get(oldItemPosition);
        OnboardInfo newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getRoute(), newItem.getRoute());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<OnboardInfo> newItems) {
        OnboardInfo oldItem = getItems().get(oldItemPosition);
        OnboardInfo newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getRoute(), newItem.getRoute())
                && ObjectsUtil.equals(oldItem.getNameOrMobileAvailableCount(), newItem.getNameOrMobileAvailableCount())
                && ObjectsUtil.equals(oldItem.getNameOrMobileNumberNotAddedCount(), newItem.getNameOrMobileNumberNotAddedCount())
                && ObjectsUtil.equals(oldItem.getTotalCustomers(), newItem.getTotalCustomers());
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OnboardReportItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.onboard_report_item,
                        parent, false);
        final ReportViewHolder vh = new ReportViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ReportViewHolder holder, int position) {
        final OnboardInfo onboardInfo = getItems().get(position);

        holder.binding.route.setText(onboardInfo.getRoute());
        holder.binding.totalCustomersCount.setText(String.valueOf(onboardInfo.getTotalCustomers()));
        holder.binding.nameOrMobileNumberAvailableCount.setText(String.valueOf(onboardInfo.getNameOrMobileAvailableCount()));
        holder.binding.nameOrMobileNumberNotAddedCount.setText(String.valueOf(onboardInfo.getNameOrMobileNumberNotAddedCount()));

        holder.binding.customerActiveCount.setText(String.valueOf(onboardInfo.getActiveCount()));
        holder.binding.customerInActiveCount.setText(String.valueOf(onboardInfo.getInactiveCount()));

        float  nameOrMobileNumberAvailableCountPercentage = (float)onboardInfo.getNameOrMobileAvailableCount()*100/(float)onboardInfo.getTotalCustomers();
        holder.binding.nameOrMobileNumberAvailableCountPercentage.setText(String.format("%.1f",nameOrMobileNumberAvailableCountPercentage)+
                getContext().getResources().getString(R.string.percentage_symbol));
        float  nameOrMobileNumberNotAddedCountPercentage = (float)onboardInfo.getNameOrMobileNumberNotAddedCount()*100/(float)onboardInfo.getTotalCustomers();
        holder.binding.nameOrMobileNumberNotAddedCountPercentage.setText(String.format("%.1f",nameOrMobileNumberNotAddedCountPercentage)+
                getContext().getResources().getString(R.string.percentage_symbol));


        float  customerActiveCountPercentage = (float)onboardInfo.getActiveCount()*100/(float)onboardInfo.getTotalCustomers();
        holder.binding.customerActiveCountPercentage.setText(String.format("%.1f",customerActiveCountPercentage)+
                getContext().getResources().getString(R.string.percentage_symbol));
        float  customerInActiveCountPercentage = (float)onboardInfo.getInactiveCount()*100/(float)onboardInfo.getTotalCustomers();
        holder.binding.customerInActiveCountPercentage.setText(String.format("%.1f",customerInActiveCountPercentage)+
                getContext().getResources().getString(R.string.percentage_symbol));

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        final OnboardReportItemBinding binding;

        public ReportViewHolder(OnboardReportItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
