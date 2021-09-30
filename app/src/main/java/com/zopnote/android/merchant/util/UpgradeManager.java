package com.zopnote.android.merchant.util;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;

/**
 * Created by nmohideen on 26/03/18.
 */

public class UpgradeManager {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "UpgradeManager";

    public static void upgrade(Context context) {

        int currentVersion = AppConstants.getAppVersionCode(context);
        int savedVersion = Prefs.getInt(AppConstants.PREFS_CURRENT_INITIALIZED_APP_VERSION, 0);

        if (savedVersion >= currentVersion) {
            if (DEBUG) Log.d(LOG_TAG, "Already upgraded");
            return;
        }

        if (isAppFirstLaunch(context)) {
            if (DEBUG) Log.d(LOG_TAG, "Skipping upgrade for fresh install");
            // save current version
            Prefs.putInt(AppConstants.PREFS_CURRENT_INITIALIZED_APP_VERSION, currentVersion);
            // nothing else to do
            return;
        }

        // do upgrade as necessary; best effort basis
        try {
            if (savedVersion < 7) {
                if (DEBUG) Log.d(LOG_TAG, "Upgrading for 7");
                upgradeFor7(context);
            }
        } catch (Exception e) {
            // report issue
            Crashlytics.logException(e);
        }
        // save current version regardless of upgrade status; best effort basis
        Prefs.putInt(AppConstants.PREFS_CURRENT_INITIALIZED_APP_VERSION, currentVersion);

    }

    private static boolean isAppFirstLaunch(Context context) {
        return ! Prefs.getSharedPreferences().contains(AppConstants.PREFS_APP_INIT_COMPLETE);
    }

    private static void upgradeFor7(Context context) {
        String uid = Installation.getUid(context);
        if (uid != null) {
            Crashlytics.setUserIdentifier(uid);
        }
    }
}
