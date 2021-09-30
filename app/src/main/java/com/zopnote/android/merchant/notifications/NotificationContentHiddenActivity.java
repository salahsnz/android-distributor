package com.zopnote.android.merchant.notifications;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.ui.SplashActivity;
import com.zopnote.android.merchant.util.Extras;

/**
 * Created by nmohideen on 02/03/18.
 */

public class NotificationContentHiddenActivity extends Activity {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "NotifContentHiddentActv";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (DEBUG) Log.d(LOG_TAG, "onCreate");

        String title = getIntent().getStringExtra(Extras.TITLE);
        String body = getIntent().getStringExtra(Extras.CONTENT);
        String actionLabel = getIntent().getStringExtra(Extras.CTA);
        String action = getIntent().getStringExtra(Extras.CTA_LINK);
        String campaign = getIntent().getStringExtra(Extras.CAMPAIGN);

        if (body != null) {
            // announcement activity
            Intent intent = new Intent(this, AnnouncementNotificationActivity.class);
            intent.putExtra(Extras.TITLE, title);
            intent.putExtra(Extras.CONTENT, body);
            intent.putExtra(Extras.CTA, actionLabel);
            intent.putExtra(Extras.CTA_LINK, action);
            intent.putExtra(Extras.CAMPAIGN, campaign);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (DEBUG) Log.d(LOG_TAG, "Starting announcement activity");
        } else if (action != null) {
            // generic view intent
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(action));
            startActivity(intent);
            if (DEBUG) Log.d(LOG_TAG, "Starting generic view activity");
        } else {
            // no action; launch our app
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
            if (DEBUG) Log.d(LOG_TAG, "Starting default activity");
        }

        new Analytics.Builder()
                .setEventName(Event.NOTIFICATION_OPEN_X)
                .addParam(Param.CAMPAIGN, campaign)
                .logEvent();

        // finish hidden activity
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (DEBUG) Log.d(LOG_TAG, "onNewIntent");
    }
}
