package com.zopnote.android.merchant.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.addarea.AddAreaActivity;
import com.zopnote.android.merchant.addroute.AddRouteActivity;
import com.zopnote.android.merchant.agreement.AgreementActivity;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.remote.FirestoreDataSource;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.event.AppUpdateAvailableEvent;
import com.zopnote.android.merchant.login.LoginActivity;
import com.zopnote.android.merchant.merchantsetup.MerchantProfileActivity;
import com.zopnote.android.merchant.merchantsetup.ShopSetupActivity;
import com.zopnote.android.merchant.notifications.inbox.InboxActivity;
import com.zopnote.android.merchant.ui.AboutActivity;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.vendor.VendorsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String LOG_TAG = HomeActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.PRODUCT_FLAVOUR_MERCHANT)
        setContentView(R.layout.home_act_merchant);
        else
        setContentView(R.layout.home_act);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                Analytics.logEvent(Event.NAV_DRAWER_OPEN);
            }
        };
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(BuildConfig.PRODUCT_FLAVOUR_MERCHANT == false)
        showHideSwitch(navigationView);

        HomeViewModel viewmodel = obtainViewModel(this);
        viewmodel.init();

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                ((TextView) findViewById(R.id.merchant_title)).setText(merchant.getName());

                View navigationHeader = navigationView.getHeaderView(0);
                TextView mobileNumberTextView = navigationHeader.findViewById(R.id.user);
                mobileNumberTextView.setText(merchant.getOwnerName());
            }
        });


        findViewById(R.id.notificationInbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_NOTIFICATION_INBOX);
                startActivity(new Intent(HomeActivity.this, InboxActivity.class));

            }
        });

        setupViewFragment();
     EventBus.getDefault().register(this);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        int maxDays = cal.getActualMaximum(Calendar.DATE);
        SimpleDateFormat format = new SimpleDateFormat("MMM yyyy");

        // Log.d("FCMToken", "token "+ FirebaseInstanceId.getInstance().getToken());

    }


    private void showHideSwitch(NavigationView navigationView) {
        if( ! FirebaseRemoteConfig.getInstance().getString(AppConstants.REMOTE_CONFIG_A_GROUP).contains(Prefs.getString(AppConstants.PREFS_SIGNED_IN_MOBILE_NUMBER, "-1"))){
            navigationView.getMenu().findItem(R.id.switch_vendor_navigation_menu_item).setVisible(false);
        }
    }

    private void setupViewFragment() {

        CollectionSummaryFragment collectionSummaryFragment =
                (CollectionSummaryFragment) getSupportFragmentManager().findFragmentById(R.id.collectionSummaryFragment);
        if(collectionSummaryFragment == null){
            collectionSummaryFragment = CollectionSummaryFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), collectionSummaryFragment, R.id.collectionSummaryFragment);
        }

        HomeFragment homeFragment =
                (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.homeFragment);
        if (homeFragment == null) {
            // Create the fragment
            homeFragment = HomeFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), homeFragment, R.id.homeFragment);
        }
    }

    public static HomeViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        HomeViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(HomeViewModel.class);

        return viewModel;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (BuildConfig.PRODUCT_FLAVOUR_MERCHANT) {
            switch (item.getItemId()) {
                case R.id.about_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                    break;
                case R.id.app_share_navigation_menu_item:
                    startActivity(Intent.createChooser(AppConstants.getAppShareIntent(this), "Share via"));
                    Analytics.logEvent(Event.SHARE_APP_FROM_NAV_DRAWER);
                    break;
                case R.id.agreement_navigation_menu_item:
                    Analytics.logEvent(Event.NAV_AGREEMENT);
                    Intent intent = new Intent(HomeActivity.this, AgreementActivity.class);
                    intent.putExtra(Extras.TITLE, "Agreement");
                    intent.putExtra(Extras.URL, AppConstants.AGREEMENT_URL);
                    startActivity(intent);
                    break;
                case R.id.log_out_menu_item:
                    openLogoutDialog();
                    break;
                case R.id.add_route_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, AddRouteActivity.class));
                    break;
                case R.id.merchant_setup_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, MerchantProfileActivity.class));
                    break;
                case R.id.add_area_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, AddAreaActivity.class));
                    break;
                default:
                    break;
            }
        } else {

            switch (item.getItemId()) {
                case R.id.about_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                    break;
                case R.id.app_share_navigation_menu_item:
                    startActivity(Intent.createChooser(AppConstants.getAppShareIntent(this), "Share via"));

                    Analytics.logEvent(Event.SHARE_APP_FROM_NAV_DRAWER);
                    break;
                case R.id.agreement_navigation_menu_item:
                    Analytics.logEvent(Event.NAV_AGREEMENT);
                    Intent intent = new Intent(HomeActivity.this, AgreementActivity.class);
                    intent.putExtra(Extras.TITLE, "Agreement");
                    intent.putExtra(Extras.URL, AppConstants.AGREEMENT_URL);
                    startActivity(intent);
                    break;
                case R.id.log_out_menu_item:
                    openLogoutDialog();

                    break;
                case R.id.switch_vendor_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, VendorsActivity.class));
                    break;
                case R.id.add_route_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, AddRouteActivity.class));
                    break;
                case R.id.merchant_setup_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, MerchantProfileActivity.class));
                    break;
                case R.id.shop_setup_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, ShopSetupActivity.class));
                    break;
                case R.id.add_area_navigation_menu_item:
                    startActivity(new Intent(HomeActivity.this, AddAreaActivity.class));
                    break;

                default:
                    break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.log_out_warning);
        builder.setMessage(R.string.clear_catch_warning);
        builder.setPositiveButton(R.string.menu_log_out_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Repository.destroyInstance();
                FirestoreDataSource.destroyInstance();
                ViewModelFactory.destroyInstance();

                deleteCache(getApplicationContext());
                Prefs.putBoolean(AppConstants.PREFS_APP_INIT_COMPLETE, false);
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Prefs.getInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,0)>0){
            findViewById(R.id.layout_count).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.notification_count)).setText(String.valueOf(Prefs.getInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,0)));

        }else {
            findViewById(R.id.layout_count).setVisibility(View.GONE);
        }
        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.HOME, "HomeActivity");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppUpdateAvailableEvent(AppUpdateAvailableEvent event) {
        if(event != null){
            showAppUpdateAvailableDialog();
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void showAppUpdateAvailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_update_available);
        builder.setMessage(R.string.app_update_available_desc);
        builder.setPositiveButton(R.string.button_update_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.GOOGLE_PLAY_APP_LINK)));
            }
        });
        builder.setNegativeButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
