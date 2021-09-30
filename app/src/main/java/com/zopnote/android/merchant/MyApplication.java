package com.zopnote.android.merchant;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.notifications.GcmRegistrationIntentService;
import com.zopnote.android.merchant.util.Installation;
import com.zopnote.android.merchant.util.MyEventBusIndex;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.RemoteConfigHelper;
import com.zopnote.android.merchant.util.RemoteConfigRefreshService;
import com.zopnote.android.merchant.util.UpgradeManager;
import com.zopnote.android.merchant.util.Utils;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.eventbus.EventBus;

public class MyApplication extends MultiDexApplication {

    private String advertisingId = null;
    private Boolean isLimitAdTrackingEnabled = null;
    private boolean isAdvertisingIdInitialized = false;

    static {
        System.loadLibrary("native-lib");
    }

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initCrashlytics();

        initAdvertisingId();

        initFirebaseRemoteConfig();

        initEventBus();

        initAnalytics();

        Prefs.init(this);

        PreferenceManager.setDefaultValues(this, com.zopnote.android.merchant.R.xml.preferences, false);

        UpgradeManager.upgrade(this);
    }

    private void initCrashlytics() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore
                .Builder()
                .disabled(BuildConfig.DEBUG)
                .build();

        Fabric.with(this, new Crashlytics.Builder()
                .core(crashlyticsCore)
                .build());
    }

    private void initFirebaseRemoteConfig() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings
                .Builder()
                .setDeveloperModeEnabled(BuildConfig.BUILD_TYPE_DEBUG)
                .build();

        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(RemoteConfigHelper.getInstance(this).getRemoteConfigDefaults());


        final int cacheExpiration = RemoteConfigHelper.getInstance(this).getCacheExpiration();

        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseRemoteConfig
                                    .getInstance()
                                    .activateFetched();

                            Utils.checkForAppUpdate();

                        } else if (firebaseRemoteConfig
                                .getInfo()
                                .getLastFetchStatus() == FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE) {

                            RemoteConfigRefreshService.scheduleSelf(MyApplication.this,
                                    cacheExpiration,
                                    0,
                                    false
                            );
                        }
                    }
                });

    }

    private void initAdvertisingId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(MyApplication.this);
                    advertisingId = adInfo.getId();
                    isLimitAdTrackingEnabled = adInfo.isLimitAdTrackingEnabled();
                } catch (Exception ignore) {
                }
                isAdvertisingIdInitialized = true;
            }
        }).start();

    }

    private void initEventBus() {
        EventBus.builder()
                .addIndex(new MyEventBusIndex())
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .throwSubscriberException(BuildConfig.DEBUG)
                .installDefaultEventBus();
    }

    private void initAnalytics() {
        Analytics.init(Injection.provideAnalytics(this));
    }

    public String getAdvertisingId() {
        // TODO check for isAdvertisingIdInitialized
        return advertisingId;
    }

    public void updateIds() {

        // GCM registration
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);

        String uid = Installation.getUid(this);

//        Injection.provideContentRepository(uid)
//                .initUser();

        // Firebase Analytics user id
        FirebaseAnalytics
                .getInstance(this)
                .setUserId(uid);

        // Firebase Crashlytics user id
        Crashlytics.setUserIdentifier(uid);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





}