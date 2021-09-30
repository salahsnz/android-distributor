package com.zopnote.android.merchant.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Installation;
import java.io.IOException;


public class AboutActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // app version
        ((TextView) findViewById(R.id.appVersionName)).setText(AppConstants.getAppVersionName(this));

        // handle debug gesture
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
        findViewById(R.id.logo).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }
    
//    public void onClickEmail(View v) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.getSupportMailtoLink(this)));
//        intent.putExtra(Intent.EXTRA_SUBJECT, AppConstants.getFeedbackMailSubject(this));
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
//    }
//
//    public void onClickRateUs(View v) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "Unable to open Google Play Store", Toast.LENGTH_SHORT).show();
//        }
//        ((MyApplication) getApplication()).trackEvent("Rating", "Rate", null, null);
//    }
//
    public void onClickShare(View v) {
        startActivity(Intent.createChooser( AppConstants.getAppShareIntent(this), "Share via"));

        Analytics.logEvent(Event.SHARE_APP_FROM_ABOUT);
    }

    public void onClickTos(View v) {
        Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
        intent.putExtra(Extras.TITLE, "Terms");
        intent.putExtra(Extras.URL, AppConstants.TOS_URL);
        startActivity(intent);
    }


    public void onClickPrivacy(View v) {
        Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
        intent.putExtra(Extras.TITLE, "Privacy");
        intent.putExtra(Extras.URL, AppConstants.PRIVACY_URL);
        startActivity(intent);
    }

    public void onCross(View v) {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ABOUT, "AboutActivity");
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            // from Android docs
            // it's best practice to implement an onDown() method that returns true.
            // This is because all gestures begin with an onDown() message.
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    InstanceID instanceID = InstanceID.getInstance(AboutActivity.this);
                    try {
                        return instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    } catch (IOException e) {
                        return "<unknown>";
                    }
                }

                @Override
                protected void onPostExecute(String gcmId) {
                    String uid = Installation.getUid(AboutActivity.this);
                    // format: gcmId+a,uid+a
                    String message = String.format("%sa,%sa", gcmId, uid);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Zopnote", message);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(AboutActivity.this, "Copied debug info", Toast.LENGTH_SHORT).show();
                }

            }.execute();
            return true;
        }

    }
}
