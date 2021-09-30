package com.zopnote.android.merchant.notifications.inbox;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.agreement.AgreementActivity;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Prefs;


public class InboxActivity extends AppCompatActivity {

    private static String LOG_TAG = InboxActivity.class.getSimpleName();
    private static boolean DEBUG = false;


    private InboxViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getIntent().getStringExtra(Extras.NOTIFICATION_TYPE) != null) {
            String type = getIntent().getStringExtra(Extras.NOTIFICATION_TYPE);
            if(type.equalsIgnoreCase("agreement")){
                Intent intent = new Intent(this, AgreementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        setContentView(R.layout.inbox_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupViewFragment();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    private void setupViewFragment() {

        InboxFragment inboxFragment =
                (InboxFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (inboxFragment == null) {
            // Create the fragment
            inboxFragment = InboxFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), inboxFragment, R.id.contentFrame);
        }
    }


    public static InboxViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        InboxViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(InboxViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Prefs.putInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,0);
        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.NOTIFICATION_INBOX, "InboxActivity");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
