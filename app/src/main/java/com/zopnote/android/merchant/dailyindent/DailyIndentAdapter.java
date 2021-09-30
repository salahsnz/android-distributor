package com.zopnote.android.merchant.dailyindent;

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
import com.zopnote.android.merchant.data.model.DailyIndent;
import com.zopnote.android.merchant.data.model.DailyIndentSubscription;
import com.zopnote.android.merchant.data.model.IndentUpdate;
import com.zopnote.android.merchant.data.model.InvoiceItem;
import com.zopnote.android.merchant.databinding.DailyIndentItemBinding;
import com.zopnote.android.merchant.invoice.InvoiceFragment;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyIndentAdapter extends BaseRecyclerAdapter<DailyIndent, DailyIndentAdapter.IndentUpdateViewHolder> {
    private DailyIndentViewModel viewmodel;

    public DailyIndentAdapter(Context context) {
        super(context);
    }

    public void setViewModel(DailyIndentViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<DailyIndent> newItems) {
        DailyIndent oldItem = getItems().get(oldItemPosition);
        DailyIndent newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getCustomerId(), newItem.getCustomerId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<DailyIndent> newItems) {
        DailyIndent oldItem = getItems().get(oldItemPosition);
        DailyIndent newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getCustomerId(), newItem.getCustomerId());
    }

    @Override
    public IndentUpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DailyIndentItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.daily_indent_item,
                        parent, false);
        final IndentUpdateViewHolder vh = new IndentUpdateViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final IndentUpdateViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final DailyIndent item = getItems().get(position);

        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }

        LinearLayout parent = new LinearLayout(getContext());
        parent.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText(item.getCustomerName());
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getContext().getResources().getColor(R.color.text_secondary));

        parent.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.setMargins(14, 8, 14, 8);
        parent.setLayoutParams(params);
        holder.binding.linearContent.addView(parent);

        for (DailyIndentSubscription daily : item.getSubscriptions()) {
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


        holder.binding.linearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog(item);
            }
        });


    }

    private void openEditDialog(final DailyIndent item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.daily_indent_edit_dialog, null);
        LinearLayout container = view.findViewById(R.id.linearContainer);

        TextView title = new TextView(getContext());
        title.setText("By default, it will take the subscription copies. Please update quantities as necessary for the selected date");
        title.setTextAppearance(getContext(), R.style.FontThin);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        title.setTextColor(getContext().getResources().getColor(R.color.text_primary));
        container.addView(title);

        TextView vendor = new TextView(getContext());
        vendor.setText("Vendor : "+item.getCustomerName());
        vendor.setTextAppearance(getContext(), R.style.FontMedium);
        vendor.setTextColor(getContext().getResources().getColor(R.color.text_secondary));
        container.addView(vendor);

        TextView route = new TextView(getContext());
        route.setText("Route : "+item.getRoute());
        route.setTextAppearance(getContext(), R.style.FontMedium);
        route.setTextColor(getContext().getResources().getColor(R.color.text_secondary));
        container.addView(route);

        LinearLayout save = view.findViewById(R.id.save);
        LinearLayout cancel = view.findViewById(R.id.cancel);

        final HashMap<String,DailyIndentSubscription> map = new HashMap<>(viewmodel.subscriptionMap);
        for (final DailyIndentSubscription daily : item.getSubscriptions()) {


                if (daily.getSubscriptionQuantity() != -1){

                    final DailyIndentSubscription dis = new DailyIndentSubscription();
                    dis.setSubscriptionQuantity(daily.getSubscriptionQuantity());
                    dis.setProductShortCode(daily.getProductShortCode());
                    dis.setProductId(daily.getProductId());

                    map.put(daily.getProductId(),dis);
                    final View invoiceItemView = LayoutInflater.from(DailyIndentAdapter.this.getContext())
                            .inflate(R.layout.daily_indent_dialog_item_container, null);
                    ((TextView) invoiceItemView.findViewById(R.id.productShortCode)).setText(daily.getProductShortCode());
                    final EditText etQty = invoiceItemView.findViewById(R.id.etQuantityMM);
                    etQty.setText(String.valueOf(daily.getSubscriptionQuantity()));

                    etQty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!TextUtils.isEmpty(s.toString().trim())) {

                                int qty = Integer.parseInt(etQty.getText().toString().trim());
                                dis.setSubscriptionQuantity(qty);
                            }else {
                                dis.setSubscriptionQuantity(0);
                            }
                            map.put(daily.getProductId(),dis);
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {


                        }
                    });

                    container.addView(invoiceItemView);
                }

        }

        builder.setView(view);

        builder.setMessage("Daily Indent Edit");
        final AlertDialog dialog = builder.create();
        dialog.show();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DailyIndentSubscription> additionalList = new ArrayList<>(map.values());
                Collections.sort(additionalList, new Comparator<DailyIndentSubscription>() {
                    @Override
                    public int compare(DailyIndentSubscription s1, DailyIndentSubscription s2) {
                        return s1.getProductShortCode().compareToIgnoreCase(s2.getProductShortCode());
                    }
                });
                viewmodel.itemUpdated = item;
                item.setSubscriptions(additionalList);
                dialog.dismiss();
                viewmodel.quantityUpdated.setValue(true);
                viewmodel.isEdited = true;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    static class IndentUpdateViewHolder extends RecyclerView.ViewHolder {

        final DailyIndentItemBinding binding;

        public IndentUpdateViewHolder(DailyIndentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
