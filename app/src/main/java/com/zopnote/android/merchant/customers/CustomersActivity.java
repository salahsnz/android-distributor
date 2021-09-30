package com.zopnote.android.merchant.customers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.addcustomer.AddCustomerActivity;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.search.SearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomersActivity extends AppCompatActivity {

    private static String LOG_TAG = CustomersActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    private List<String> tabs;
    private ViewPager viewPager;
    private CustomersViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.customers_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        findViewById(R.id.addCustomer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomersActivity.this, AddCustomerActivity.class));
            }
        });

        setupTabs();
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

        final AddressCategoryPagerAdapter pagerAdapter =
                new AddressCategoryPagerAdapter(getSupportFragmentManager(), tabs);

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
                Collections.sort(routes);
                for (int i= 0; i<routes.size(); i++){
                    String routeName = routes.get(i);
                    if( !tabs.contains(routeName)){
                        tabs.add(routeName);
                        pagerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    public static CustomersViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        CustomersViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(CustomersViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        setScreenName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_search_menu_main:
                Intent intent = new Intent(CustomersActivity.this, SearchActivity.class);
                startActivity(intent);
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
        String screenName = String.format(ScreenName.VIEW_CUSTOMERS + " - %s", currentTab);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, screenName, "CustomerActivity");
    }
}
