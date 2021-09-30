package com.zopnote.android.merchant.util;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.zopnote.android.merchant.AppConstants;

import java.util.HashMap;
import java.util.Map;


public class RemoteConfigHelper {

    private static RemoteConfigHelper remoteConfigHelper;
    private final Context mContext;

    private RemoteConfigHelper(Context context) {
        mContext = context;
    }

    public static RemoteConfigHelper getInstance(Context context) {
        if (remoteConfigHelper == null){
            remoteConfigHelper =  new RemoteConfigHelper(context);
        }
        return remoteConfigHelper;
    }

    public int getCacheExpiration() {
        if (FirebaseRemoteConfig
                .getInstance()
                .getInfo()
                .getConfigSettings()
                .isDeveloperModeEnabled()) {
            return 0;
        } else {
            return AppConstants.REMOTE_CONFIG_CACHE_SECONDS;
        }
    }

    public Map<String, Object> getRemoteConfigDefaults() {
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstants.REMOTE_CONFIG_LATEST_APP_VERSION_CODE, AppConstants.getAppVersionCode(mContext));
        return map;
    }
}
