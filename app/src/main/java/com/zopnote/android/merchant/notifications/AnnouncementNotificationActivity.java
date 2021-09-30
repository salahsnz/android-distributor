package com.zopnote.android.merchant.notifications;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.ui.WebViewActivity;

/**
 * Created by Ravindra on 9/1/2018.
 */

public class AnnouncementNotificationActivity extends WebViewActivity {
    private String campaign;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // all init is done in parent class

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ANNOUNCEMENT_NOTIFICATION, "AnnouncementNotificationActivity");
    }

}
