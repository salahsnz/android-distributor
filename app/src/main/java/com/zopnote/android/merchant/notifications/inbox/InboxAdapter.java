package com.zopnote.android.merchant.notifications.inbox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.agreement.AgreementActivity;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Notification;
import com.zopnote.android.merchant.databinding.InboxItemBinding;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerActivity;

import java.util.Date;
import java.util.List;

/**
 * Created by nmohideen on 30/01/18.
 */

public class InboxAdapter extends BaseRecyclerAdapter<Notification, InboxAdapter.InboxViewHolder> {

    private InboxViewModel viewmodel;
    public InboxAdapter(Context context) {
        super(context);
    }

    public void setViewModel(InboxViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Notification> newItems) {
        Notification oldItem = getItems().get(oldItemPosition);
        Notification newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getNotificationID(), newItem.getNotificationID());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Notification> newItems) {
        Notification oldItem = getItems().get(oldItemPosition);
        Notification newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getNotificationID(), newItem.getNotificationID());
    }

    @Override
    public InboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        InboxItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.inbox_item,
                        parent, false);
        final InboxViewHolder vh = new InboxViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final InboxViewHolder holder, int position) {
        final Notification notification = getItems().get(position);

        holder.binding.notificationTitle.setText(notification.getTitle());
        if(notification.getAction() !=null &&notification.getAction().equalsIgnoreCase("Online Payment"))
            holder.binding.notificationTitle.setTextColor(getContext().getResources().getColor(R.color.green));
        holder.binding.notificationDescription.setText(notification.getDescription());

        Date date = new Date(notification.getCreated());
        String dateStr = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, date); //FormatUtil.formatTimeAgo(notification.getCreated());
        holder.binding.notificationDay.setText(dateStr.substring(0,2));
        holder.binding.notificationMonth.setText(dateStr.substring(2,6));




        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.getAction() !=null &&
                        !notification.getAction().equalsIgnoreCase("")){
                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notification.getAction()));
                    getContext().startActivity(intent);
                }
              else if (notification.getAction() !=null && notification.getAction().equalsIgnoreCase("agreement")){
                    Intent intent = new Intent(getContext(), AgreementActivity.class);
                    intent.putExtra(Extras.TITLE, "Agreement");
                    intent.putExtra(Extras.URL, AppConstants.AGREEMENT_URL);
                    getContext().startActivity(intent);
              }
            }
        });

        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.binding.getRoot().setBackgroundColor(getContext().getResources().getColor(R.color.blue_50));
                deleteNotificationConfirmationDialog(notification.getNotificationID(),holder.binding.getRoot());
                return false;
            }
        });
    }



    private void deleteNotificationConfirmationDialog(final String notificationID, View root) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(R.string.delete_notification_warning))
                .setPositiveButton(R.string.button_yes_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyDataSetChanged();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.enforceConnection(getContext())) {
                    notifyDataSetChanged();
                    return;
                }
                viewmodel.deleteNotification(notificationID,false);
                dialog.dismiss();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    private void logClickEvent(String customerId) {
        Analytics.Builder analyticsBuilder = new Analytics.Builder()
                .setEventName(Event.NAV_CUSTOMER)
                .addParam(Param.CUSTOMER_ID, customerId);
        analyticsBuilder.logEvent();
    }

    static class InboxViewHolder extends RecyclerView.ViewHolder {

        final InboxItemBinding binding;

        public InboxViewHolder(InboxItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
