package com.zopnote.android.merchant.reports.draftinvoice;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.DraftInvoiceReportActBinding;
import com.zopnote.android.merchant.event.RefreshDraftReportEvent;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DraftInvoiceReportActivity extends AppCompatActivity {

    private static String LOG_TAG = DraftInvoiceReportActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    private List<String> tabs;
    private ViewPager viewPager;
    private DraftInvoiceReportViewModel viewmodel;
    private DraftInvoiceReportActBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.draft_invoice_report_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        boolean prefSwitchChecked = Prefs.getBoolean(AppConstants.PREFS_DRAFT_INVOICE_SWITCH_IS_CHECKED, true);
        if(prefSwitchChecked){
            viewmodel.invoiceType = "changes";
        }else{
            viewmodel.invoiceType = "all";
        }

        setupTabs();

        binding.invoiceSwitch.setChecked(prefSwitchChecked);
        binding.invoiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Prefs.putBoolean(AppConstants.PREFS_DRAFT_INVOICE_SWITCH_IS_CHECKED, true);
                    viewmodel.invoiceType = "changes";
                    viewmodel.invoiceTypeChanged.setValue(true);
                }else{
                    Prefs.putBoolean(AppConstants.PREFS_DRAFT_INVOICE_SWITCH_IS_CHECKED, false);
                    viewmodel.invoiceType = "all";
                    viewmodel.invoiceTypeChanged.setValue(true);
                }
            }
        });

        EventBus.getDefault().register(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupTabs() {

        tabs = new ArrayList<>(10);

        final DraftInvoiceReportAddressPagerAdapter pagerAdapter =
                new DraftInvoiceReportAddressPagerAdapter(getSupportFragmentManager(), tabs);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                setScreenName();
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                List<String> routes = merchant.getRoutes();

                for (int i= 0; i<routes.size(); i++){
                    String routeName = routes.get(i);
                    if( !tabs.contains(routeName)){
                        tabs.add(routeName);
                        pagerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();
                getReports();
                //to avoid calling api multiple times due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });

        viewmodel.monthInfoChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean changed) {
                if(changed){
                    setToolbarDate();
                }
            }
        });
    }

    private void setToolbarDate() {
        String displayMonthYear = String.format("%s %s", viewmodel.monthString, viewmodel.year);
        ((TextView)findViewById(R.id.monthYearText)).setText(displayMonthYear);
    }

    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(this)) {
            viewmodel.getDraftInvoicesReport();
        }else{
            viewmodel.networkError.postValue(true);
        }
    }

    public static DraftInvoiceReportViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        DraftInvoiceReportViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(DraftInvoiceReportViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        setScreenName();
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

    private void setScreenName() {

        if (tabs.size() == 0) {
            return;
        }

        int currentItem = viewPager.getCurrentItem();
        String currentTab = tabs.get(currentItem);
        String screenName = String.format(ScreenName.DRAFT_INVOICE_REPORT + " - %s", currentTab);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, screenName, "DraftInvoiceReportActivity");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshDraftReportEvent(RefreshDraftReportEvent event) {
        if(event != null){
            viewmodel.getDraftInvoicesReport();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
