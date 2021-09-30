package com.zopnote.android.merchant.notifications;

import android.content.Context;
import android.util.Log;

import com.zopnote.android.merchant.util.RemoteConfigRefreshService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by nmohideen on 02/03/18.
 */

public class RefreshConfigCommand implements Command {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "RefreshConfigCommand";

    private static final String ATTR_FETCH_DELAY_WINDOW_SECS = "fetchDelayWindowSecs";
    private static final String ATTR_REPORT_ANALYTICS = "reportAnalytics";

    private static final int FETCH_DELAY_WINDOW_SECS_DEFAULT = 600; // 10 mins

    private Context mContext;
    private JSONObject mJsonObj;

    public RefreshConfigCommand(Context context, JSONObject jsonObj) {
        this.mContext = context;
        this.mJsonObj = jsonObj;
    }

    @Override
    public void process() {

        int fetchDelayWindowSecs = FETCH_DELAY_WINDOW_SECS_DEFAULT;
        if (mJsonObj.has(ATTR_FETCH_DELAY_WINDOW_SECS)) {
            try {
                fetchDelayWindowSecs = mJsonObj.getInt(ATTR_FETCH_DELAY_WINDOW_SECS);
            } catch (JSONException ignore) {
            }
        }
        if (DEBUG) Log.d(LOG_TAG, "fetchDelayWindow: " + fetchDelayWindowSecs);

        int fetchDelay = new Random().nextInt(fetchDelayWindowSecs);
        if (DEBUG) Log.d(LOG_TAG, "fetchDelay: " + fetchDelay);

        boolean reportAnalytics = false;
        if (mJsonObj.has(ATTR_REPORT_ANALYTICS)) {
            try {
                reportAnalytics = mJsonObj.getBoolean(ATTR_REPORT_ANALYTICS);
            } catch (JSONException ignore) {
            }
        }
        if (DEBUG) Log.d(LOG_TAG, "reportAnalytics: " + reportAnalytics);

        if (DEBUG) Log.d(LOG_TAG, "Scheduling background task");
        RemoteConfigRefreshService.scheduleSelf(mContext,
                0, // cacheExpiration
                fetchDelay,
                reportAnalytics);
    }
}
