package com.zopnote.android.merchant.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.MyApplication;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.PushNotification;
import com.zopnote.android.merchant.notifications.inbox.InboxActivity;
import com.zopnote.android.merchant.util.Prefs;

/**
 * Created by Nizam on 11-07-2015.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "MyGcmListenerService";
    final int NOTIFY_ID=1;
    int count = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        if (DEBUG) Log.d(LOG_TAG, "onMessage()");


        //if ( ! data.containsKey("ireff")) {
            // not from iReff backend; might be from other tools
        //    return;
        //}
       //  String title =    data.getString("title");
       //  String message = data.getString("body");
        PushNotification pushNotification = new PushNotification(this, data);
        pushNotification.save();
        pushNotification.process();


/*
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my chanel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "my channel";  //R.string.channel_name
            String description = "my channel description";//getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

     //   Intent notificationIntent = new Intent(this.getApplicationContext(), SplashActivity.class);
     //   notificationIntent.putExtra("NOTIFICATION_TYPE", remoteMessage.getData().get("type"));
     //   PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, InboxActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);



        notificationBuilder.setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //    .setDefaults(Notification.DEFAULT_ALL)
           //     .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.zopnote_notification_logo)
          //      .setTicker(title)
                .setAutoCancel(true);
          //      .setContentTitle(title)
         //       .setSound(uri)
          //      .setContentText(message);

        notificationManager.notify(notification id 1, notificationBuilder.build());


        int pendingNotificationsCount = Prefs.getInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,0) + 1;
        Prefs.putInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,pendingNotificationsCount);
        notificationBuilder.setNumber(pendingNotificationsCount) ;


*/

    }

}
