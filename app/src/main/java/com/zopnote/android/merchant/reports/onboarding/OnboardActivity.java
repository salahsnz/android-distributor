package com.zopnote.android.merchant.reports.onboarding;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.databinding.OnboardActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;

public class OnboardActivity extends AppCompatActivity {
    private OnboardActBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.onboard_act);

        setupToolbar();

        OnboardViewModel viewmodel = obtainViewModel(this);
        viewmodel.init();

        setupViewFragment();
    }

    private void setupViewFragment() {
        OnboardFragment onboardFragment =
                (OnboardFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);
        if (onboardFragment == null) {
            // Create the fragment
            onboardFragment = OnboardFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), onboardFragment, R.id.contentView);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static OnboardViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        OnboardViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(OnboardViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ONBORAD, "OnboardActivity");    }

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
