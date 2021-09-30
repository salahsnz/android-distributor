package com.zopnote.android.merchant.notifications;

import android.app.Activity;
import android.os.Bundle;

import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.util.Extras;

public class NotificationClearedActivity extends Activity {
    private String campaign;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        campaign = getIntent().getStringExtra(Extras.CAMPAIGN);
    }

    @Override
    public void onStart() {
        super.onStart();

        new Analytics.Builder()
                .setEventName(Event.NOTIFICATION_CLEARED_X)
                .addParam(Param.CAMPAIGN, campaign)
                .logEvent();

        // finish hidden activity
        finish();
    }
}
