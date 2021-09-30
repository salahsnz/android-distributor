package com.zopnote.android.merchant.notifications;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.AppUpdateInfo;
import com.zopnote.android.merchant.util.Extras;

public class AppUpdateNotificationActivity extends AppCompatActivity {
    private String campaign;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update_notification);
        setupToolbar();

        Bundle stateBundle = null;
        if (savedInstanceState != null) {
            stateBundle = savedInstanceState;
        } else {
            stateBundle = getIntent().getExtras();
        }

        campaign = stateBundle.getString(Extras.CAMPAIGN);
        content = stateBundle.getString(Extras.CONTENT);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppUpdateInfo appUpdateInfo = new AppUpdateInfo(content, this);
        TextView title = findViewById(R.id.title);
        TextView desc = findViewById(R.id.desc);
        WebView notes = findViewById(R.id.notes);
        View updateButton = findViewById(R.id.updateButton);
        if (appUpdateInfo.isUpdateAvailable()) {
            if (appUpdateInfo.isUpdateMandatory()) {
                title.setText(R.string.app_update_required);
                desc.setText(R.string.app_update_required_desc);
            } else {
                title.setText(R.string.app_update_available);
                desc.setText(R.string.app_update_available_desc);
            }
            if (appUpdateInfo.getNotes() != null) {
                notes.loadData(appUpdateInfo.getNotes(), "text/html", "utf-8");
            } else {
                notes.setVisibility(View.GONE);
            }
            updateButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.GOOGLE_PLAY_APP_LINK)));
                }
            });
        } else {
            Toast.makeText(this, R.string.app_already_updated_toast, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Extras.CAMPAIGN, campaign);
        outState.putString(Extras.CONTENT, content);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        campaign = intent.getStringExtra(Extras.CAMPAIGN);
        content = intent.getStringExtra(Extras.CONTENT);
    }
}
