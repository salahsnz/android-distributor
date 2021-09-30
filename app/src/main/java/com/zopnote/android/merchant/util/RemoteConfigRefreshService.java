package com.zopnote.android.merchant.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_FAILURE;
import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET;
import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_SUCCESS;
import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.LAST_FETCH_STATUS_THROTTLED;

public class RemoteConfigRefreshService extends GcmTaskService {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "RemoteConfigFetch";

    public static void scheduleSelf(Context context, int cacheExpiration, int fetchDelay, boolean reportAnalytics) {

        Bundle bundle = new Bundle();
        bundle.putInt(Extras.REMOTE_CONFIG_CACHE_EXPIRATION, cacheExpiration);
        bundle.putBoolean(Extras.REPORT_ANALYTICS, reportAnalytics);
        bundle.putLong(Extras.START_TIME, System.currentTimeMillis());

        OneoffTask task = new OneoffTask.Builder()
                .setService(RemoteConfigRefreshService.class)
                .setExecutionWindow(fetchDelay, 259200) // 60 * 60 * 24 * 3 (3 days in seconds)
                .setPersisted(true)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                .setTag("remote_config_fetch")
                .setUpdateCurrent(true)
                .setExtras(bundle)
                .build();
        GcmNetworkManager.getInstance(context).schedule(task);
    }

    @Override
    public int onRunTask(final TaskParams taskParams) {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        final Bundle extras = taskParams.getExtras();

        // rare crashes on some older devices
        if (extras == null) {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        int cacheExpiration = extras.getInt(Extras.REMOTE_CONFIG_CACHE_EXPIRATION);

        if (DEBUG) Log.d(LOG_TAG, "Starting fetch");
        remoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (DEBUG) Log.d(LOG_TAG, "Fetch succeeded");
                            remoteConfig.activateFetched();
                        } else {
                            if (DEBUG) Log.d(LOG_TAG, "Fetch failed: " + task.getException());
                        }

                        // report analytics, if applicable
                        if ( ! extras.getBoolean(Extras.REPORT_ANALYTICS)) {
                            return;
                        }

                        if (task.isSuccessful()) {
                            long startTime = extras.getLong(Extras.START_TIME);
                            long durationMins = (System.currentTimeMillis() - startTime)/(1000 * 60);
                            if (DEBUG) Log.d(LOG_TAG, "durationMins: " + durationMins);
                            new Analytics.Builder()
                                    .setEventName(Event.CONFIG_REFRESH_SUCCESS)
                                    .addParam(Param.DURATION_MINS, durationMins)
                                    .logEvent();
                        } else {
                            String fetchStatus = null;
                            switch (remoteConfig.getInfo().getLastFetchStatus()) {
                                case LAST_FETCH_STATUS_FAILURE:
                                    fetchStatus = "failure";
                                    break;
                                case LAST_FETCH_STATUS_NO_FETCH_YET:
                                    fetchStatus = "no_fetch_yet";
                                    break;
                                case LAST_FETCH_STATUS_SUCCESS:
                                    fetchStatus = "success";
                                    break;
                                case LAST_FETCH_STATUS_THROTTLED:
                                    fetchStatus = "throttled";
                                    break;
                            }
                            Log.d(LOG_TAG, "Fetch status: " + fetchStatus);
                            new Analytics.Builder()
                                    .setEventName(Event.CONFIG_REFRESH_FAILED)
                                    .addParam(Param.FETCH_STATUS, fetchStatus)
                                    .logEvent();
                        }
                    }
                });

        return GcmNetworkManager.RESULT_SUCCESS;
    }

}
