package com.zopnote.android.merchant.reports.subscription;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.managesubscription.SubscriptionUtil;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerActivity;

import java.util.Date;
import java.util.List;

public class SubscriptionsReportAdapter extends BaseRecyclerAdapter implements HeaderItemDecoration.StickyHeaderInterface{
    private final int HEADER = 0, CONTENT = 1;

    public SubscriptionsReportAdapter(Context context) {
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

        if(oldItem instanceof SubscriptionReportItem && newItem instanceof SubscriptionReportItem){
            return ObjectsUtil.equals(((SubscriptionReportItem) oldItem).getId(), ((SubscriptionReportItem) newItem).getId());
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

        if(oldItem instanceof SubscriptionReportItem && newItem instanceof SubscriptionReportItem){
            return ObjectsUtil.equals(((SubscriptionReportItem) oldItem).getId(), ((SubscriptionReportItem) newItem).getId())
                    && ObjectsUtil.equals(((SubscriptionReportItem) oldItem).getAddressLine1(), ((SubscriptionReportItem) newItem).getAddressLine1())
                    && ObjectsUtil.equals(((SubscriptionReportItem) oldItem).getDoorNumber(), ((SubscriptionReportItem) newItem).getDoorNumber())
                    && ObjectsUtil.equals(((SubscriptionReportItem) oldItem).getSubscriptions(), ((SubscriptionReportItem) newItem).getSubscriptions());
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
                View v2 = inflater.inflate(R.layout.subscriptions_report_item_content, viewGroup, false);
                viewHolder = new ReportViewHolder(v2);
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
                ReportViewHolder vh2 = (ReportViewHolder) viewHolder;
                configureContentViewHolder(vh2, position);
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

    private void configureContentViewHolder(ReportViewHolder holder, int position) {

        final SubscriptionReportItem reportItem = (SubscriptionReportItem) getItems().get(position);
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

            if(reportItem.getSubscriptions() != null && ! reportItem.getSubscriptions().isEmpty()){
                holder.itemView.findViewById(R.id.subscriptionsContainer).setVisibility(View.VISIBLE);
                addSubscriptionViews(holder.itemView, reportItem.getSubscriptions());
            }else {
                holder.itemView.findViewById(R.id.subscriptionsContainer).setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ViewCustomerActivity.class);
                    intent.putExtra(Extras.CUSTOMER_ID, reportItem.getId());
                    getContext().startActivity(intent);
                }
            });
        }
    }

    private void addSubscriptionViews(View view, List<SubscriptionInfo> subscriptions) {

        LinearLayout subscriptionsContainer = view.findViewById(R.id.subscriptionsContainer);
        subscriptionsContainer.removeAllViews();

        for (int i=0; i< subscriptions.size(); i++) {

            SubscriptionInfo subscriptionInfo = subscriptions.get(i);
            View subscriptionView = LayoutInflater.from(getContext()).inflate(R.layout.subscription_report_subscription_item, subscriptionsContainer, false);

            ((TextView)subscriptionView.findViewById(R.id.name)).setText(subscriptionInfo.getProductName());

            if(subscriptionStartsInCurrentMonth(subscriptionInfo.getStartDate())){
                String startDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, subscriptionInfo.getStartDate());
                ((TextView)subscriptionView.findViewById(R.id.subscriptionStartDate)).setText(startDate);
            }else{
                ((TextView)subscriptionView.findViewById(R.id.subscriptionStartDate)).setText("-");
                ((TextView) subscriptionView.findViewById(R.id.subscriptionStartDate)).setGravity(Gravity.CENTER);
            }

            if(subscriptionInfo.getEndDate() != null){
                String endDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, subscriptionInfo.getEndDate());
                ((TextView)subscriptionView.findViewById(R.id.subscriptionEndDate)).setText(endDate);
            }else {
                ((TextView)subscriptionView.findViewById(R.id.subscriptionEndDate)).setText("-");
                ((TextView) subscriptionView.findViewById(R.id.subscriptionEndDate)).setGravity(Gravity.CENTER);
            }

            if(subscriptionInfo.getPauseList() != null && ! subscriptionInfo.getPauseList().isEmpty()){
                addPausesView(subscriptionView, subscriptionInfo.getPauseList());
            }

            //remove last divider
            if(i == subscriptions.size()-1){
                subscriptionView.findViewById(R.id.subscriptionDivider).setVisibility(View.GONE);
            }

            subscriptionsContainer.addView(subscriptionView);
        }
    }

    private boolean subscriptionStartsInCurrentMonth(Date startDate) {
        return SubscriptionUtil.isDateInCurrentMonth(startDate) || SubscriptionUtil.isDateInUpcomingMonth(startDate);
    }

    private void addPausesView(View subscriptionView, List<Pause> pauseList) {
        LinearLayout pausesContainer = subscriptionView.findViewById(R.id.pausesContainer);
        pausesContainer.removeAllViews();
        
      //  for (int i=0; i< pauseList.size(); i++) {
            Pause pause = pauseList.get(pauseList.size()-1);
            View pauseView = LayoutInflater.from(getContext()).inflate(R.layout.subscription_report_pause_item, pausesContainer, false);

            String pauseStartDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, pause.getPauseStartDate());
            ((TextView)pauseView.findViewById(R.id.pauseStartDate)).setText(pauseStartDate);

            if(pause.getPauseEndDate() != null){
                String pauseEndDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, pause.getPauseEndDate());
                ((TextView)pauseView.findViewById(R.id.pauseEndDate)).setText(pauseEndDate);
                pauseView.findViewById(R.id.pauseEndDate).setVisibility(View.VISIBLE);
            }else{
                pauseView.findViewById(R.id.pauseEndDate).setVisibility(View.INVISIBLE);
            }

            //remove last divider
           // if(i == pauseList.size() -1){
                pauseView.findViewById(R.id.pauseDivider).setVisibility(View.GONE);
           // }
            pausesContainer.addView(pauseView);
      //  }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItems().get(position) instanceof RouteHeader) {
            return HEADER;
        } else if (getItems().get(position) instanceof SubscriptionReportItem) {
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

    private class ReportViewHolder extends RecyclerView.ViewHolder {
        public ReportViewHolder(View view) {
            super(view);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }
}
