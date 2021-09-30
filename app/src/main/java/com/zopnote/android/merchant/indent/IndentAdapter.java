package com.zopnote.android.merchant.indent;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.DailySubscription;
import com.zopnote.android.merchant.databinding.IndentItemBinding;
import com.zopnote.android.merchant.reports.ReportsActivity;
import com.zopnote.android.merchant.reports.subscription.HeaderItemDecoration;
import com.zopnote.android.merchant.reports.subscription.RouteHeader;
import com.zopnote.android.merchant.reports.subscription.SubscriptionsReportActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.List;

public class IndentAdapter extends BaseRecyclerAdapter implements HeaderItemDecoration.StickyHeaderInterface{
    private IndentViewModel viewmodel;
    private final int HEADER = 0, CONTENT = 1;

    public IndentAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List newItems) {
        Object oldItem = getItems().get(oldItemPosition);
        Object newItem = newItems.get(newItemPosition);

        boolean sameClass = oldItem.getClass().equals(newItem.getClass());
        if(sameClass){
            boolean itemsEqual = areItemsEqual(oldItem, newItem);
            return itemsEqual;
        }else{
            return false;
        }
    }

    private boolean areItemsEqual(Object oldItem, Object newItem) {
        if(oldItem instanceof RouteHeader && newItem instanceof RouteHeader){
            return ObjectsUtil.equals(((RouteHeader) oldItem).getName(), ((RouteHeader) newItem).getName());
        }

        if(oldItem instanceof DailySubscription && newItem instanceof DailySubscription){
            return ObjectsUtil.equals(((DailySubscription) oldItem).getName(), ((DailySubscription) newItem).getName());
        }

        return false;
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List newItems) {
        Object oldItem = getItems().get(oldItemPosition);
        Object newItem = newItems.get(newItemPosition);

        boolean sameClass = oldItem.getClass().equals(newItem.getClass());
        if(sameClass){
            boolean sameContents = areContentsSame(oldItem, newItem);
            return sameContents;
        }else{
            return false;
        }
    }

    private boolean areContentsSame(Object oldItem, Object newItem) {
        if(oldItem instanceof RouteHeader && newItem instanceof RouteHeader){
            return ObjectsUtil.equals(((RouteHeader) oldItem).getName(), ((RouteHeader) newItem).getName());
        }

        if(oldItem instanceof DailySubscription && newItem instanceof DailySubscription){
            boolean sameString = ObjectsUtil.equals(((DailySubscription) oldItem).getName(), ((DailySubscription) newItem).getName())
                    && ObjectsUtil.equals(((DailySubscription) oldItem).getAddressLine2(), ((DailySubscription) newItem).getAddressLine2())
                    && ObjectsUtil.equals(((DailySubscription) oldItem).getType(), ((DailySubscription) newItem).getType())
                    && ObjectsUtil.equals(((DailySubscription) oldItem).getRoute(), ((DailySubscription) newItem).getRoute());
            boolean sameInt = ObjectsUtil.equals(((DailySubscription) oldItem).getActiveCount(), ((DailySubscription) newItem).getActiveCount())
                    && ObjectsUtil.equals(((DailySubscription) oldItem).getPauseCount(), ((DailySubscription) newItem).getPauseCount())
                    && ObjectsUtil.equals(((DailySubscription) oldItem).getProcureCount(), ((DailySubscription) newItem).getProcureCount());
            if(sameString && sameInt){
                return true;
            }
        }
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.subscriptions_report_item_header, viewGroup, false);
                viewHolder = new HeaderViewHolder(v1);
                break;
            case CONTENT:
                IndentItemBinding binding = DataBindingUtil
                        .inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.indent_item,
                                viewGroup, false);
                viewHolder = new DailySubscriptionViewHolder(binding);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case HEADER:
                HeaderViewHolder vh1 = (HeaderViewHolder) viewHolder;
                configureHeaderViewHolder(vh1, position);
                break;
            case CONTENT:
                DailySubscriptionViewHolder vh2 = (DailySubscriptionViewHolder) viewHolder;
                configureDailySubscriptionViewHolder(vh2, position);
                break;
        }
    }

    private void configureHeaderViewHolder(HeaderViewHolder holder, int position) {
        RouteHeader routeHeader = (RouteHeader) getItems().get(position);
        View view = holder.itemView;
        if(view != null){
            ((TextView)view.findViewById(R.id.route)).setText(routeHeader.getName());
        }
    }

    private void configureDailySubscriptionViewHolder(final DailySubscriptionViewHolder holder, int position) {
        final DailySubscription dailySubscription = (DailySubscription) getItems().get(position);
        holder.binding.name.setText(dailySubscription.getName());

        if(viewmodel.indentType.equals("changes")){
            holder.binding.decrementCount.setText(String.valueOf(dailySubscription.getPauseCount()));
            holder.binding.decrementCount.setVisibility(View.VISIBLE);
            holder.binding.imageView.setVisibility(View.VISIBLE);
        }else{
            holder.binding.decrementCount.setVisibility(View.INVISIBLE);
            holder.binding.imageView.setVisibility(View.INVISIBLE);
        }
        holder.binding.count.setText(String.valueOf(dailySubscription.getProcureCount()));

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SubscriptionsReportActivity.class);
                intent.putExtra(Extras.PRODUCT, dailySubscription.getName());
                getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        if (getItems().get(position) instanceof RouteHeader) {
            return HEADER;
        } else if (getItems().get(position) instanceof DailySubscription) {
            return CONTENT;
        }
        return -1;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.subscriptions_report_item_sticky_header;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        if (getItems().get(headerPosition) instanceof RouteHeader) {
            RouteHeader routeHeader = (RouteHeader) getItems().get(headerPosition);
            String routeName = routeHeader.getName();
            ((TextView)header.findViewById(R.id.route)).setText(routeName);
        }
    }

    @Override
    public boolean isHeader(int itemPosition) {
        if (getItems().get(itemPosition) instanceof RouteHeader) {
            return true;
        }
        return false;
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }


    public class DailySubscriptionViewHolder extends RecyclerView.ViewHolder {
        final IndentItemBinding binding;

        public DailySubscriptionViewHolder(IndentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setViewModel(IndentViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }
}
