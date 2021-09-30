package com.zopnote.android.merchant.notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.util.Extras;

/**
 * Created by Ravindra on 9/1/2018.
 */

public class AppUpdateHiddenActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int notificationId = getIntent().getIntExtra(Extras.NOTIFICATION_ID, -1);
        if (notificationId != -1) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }

        String campaign = getIntent().getStringExtra(Extras.CAMPAIGN);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.GOOGLE_PLAY_APP_LINK)));
    }
}
