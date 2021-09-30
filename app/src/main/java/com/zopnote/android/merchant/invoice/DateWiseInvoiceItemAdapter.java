package com.zopnote.android.merchant.invoice;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.dailyindent.DailyIndentViewModel;
import com.zopnote.android.merchant.data.model.DailyIndent;
import com.zopnote.android.merchant.data.model.DailyIndentSubscription;
import com.zopnote.android.merchant.data.model.DateWiseBills;
import com.zopnote.android.merchant.databinding.DatewiseInvoiceItemItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DateWiseInvoiceItemAdapter extends BaseRecyclerAdapter<DateWiseBills, DateWiseInvoiceItemAdapter.DateWiseInvoiceItemViewHolder> {
    private DailyIndentViewModel viewmodel;

    public DateWiseInvoiceItemAdapter(Context context) {
        super(context);
    }

    public void setViewModel(DailyIndentViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<DateWiseBills> newItems) {
        DateWiseBills oldItem = getItems().get(oldItemPosition);
        DateWiseBills newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getIndentDate(), newItem.getIndentDate());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<DateWiseBills> newItems) {
        DateWiseBills oldItem = getItems().get(oldItemPosition);
        DateWiseBills newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getIndentDate(), newItem.getIndentDate());
    }

    @Override
    public DateWiseInvoiceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DatewiseInvoiceItemItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.datewise_invoice_item_item,
                        parent, false);
        final DateWiseInvoiceItemViewHolder vh = new DateWiseInvoiceItemViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DateWiseInvoiceItemViewHolder holder, int position) {
        final DateWiseBills item = getItems().get(position);

        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }
        setDate(item,holder);
        setAmount(item,holder);
        setAdvancePaid(item,holder);

        for (DailyIndentSubscription daily : item.getIndents()) {
            LinearLayout parent1 = new LinearLayout(getContext());

            parent1.setLayoutParams(new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
            parent1.setOrientation(LinearLayout.HORIZONTAL);
            TextView shortCode = new TextView(getContext());

             if (daily.getSubscriptionQuantity() != -1) {
                shortCode.setText(String.valueOf(daily.getSubscriptionQuantity()));

            } else {
                shortCode.setText("-");
            }

            shortCode.setTextAppearance(getContext(), R.style.FontMedium);
            shortCode.setTypeface(null, Typeface.BOLD);
            shortCode.setTextColor(getContext().getResources().getColor(R.color.text_secondary));
            parent1.addView(shortCode);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) parent1.getLayoutParams();
            params1.setMargins(14, 8, 14, 8);
            parent1.setLayoutParams(params1);
            holder.binding.linearContent.addView(parent1);
        }



    }

    private void setAdvancePaid(final DateWiseBills item, final DateWiseInvoiceItemViewHolder holder){
        LinearLayout parent = new LinearLayout(getContext());
        parent.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText(FormatUtil.getRupeePrefixedAmount(
                getContext(),
                item.getAdvancePaid(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getContext().getResources().getColor(R.color.text_secondary));

        parent.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.setMargins(14, 8, 14, 8);
        parent.setLayoutParams(params);
        holder.binding.linearContent.addView(parent);
    }
    private void setAmount(final DateWiseBills item, final DateWiseInvoiceItemViewHolder holder){
        LinearLayout parent = new LinearLayout(getContext());
        parent.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText(FormatUtil.getRupeePrefixedAmount(
               getContext(),
                item.getDailyTotal(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getContext().getResources().getColor(R.color.text_secondary));

        parent.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.setMargins(14, 8, 14, 8);
        parent.setLayoutParams(params);
        holder.binding.linearContent.addView(parent);
    }

    private void setDate(final DateWiseBills item, final DateWiseInvoiceItemViewHolder holder){
        LinearLayout parent = new LinearLayout(getContext());
        parent.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR.format(item.getIndentDate()));
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getContext().getResources().getColor(R.color.text_secondary));

        parent.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.setMargins(14, 8, 14, 8);
        parent.setLayoutParams(params);
        holder.binding.linearContent.addView(parent);
    }



    static class DateWiseInvoiceItemViewHolder extends RecyclerView.ViewHolder {

        final DatewiseInvoiceItemItemBinding binding;

        public DateWiseInvoiceItemViewHolder(DatewiseInvoiceItemItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
