package com.zopnote.android.merchant.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.util.Utils;

import java.util.Map;

/**
 * Created by nmohideen on 01/03/18.
 */

// diff name to avoid clash with FirebaseAnalytics library class
public class FirebaseBasedAnalytics implements IAnalytics {

    private FirebaseAnalytics firebaseAnalytics;

    public FirebaseBasedAnalytics(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void logEvent(String eventName, Map<String, Object> params) {
        Bundle bundle = null;

        if (params != null) {
            bundle = new Bundle();
            for (String key : params.keySet()) {
                setInBundle(bundle, key, params.get(key));
            }
        }

        firebaseAnalytics.logEvent(eventName, bundle);
    }

    private void setInBundle(Bundle bundle, String name, Object value) {

        if (value instanceof Boolean) {
            bundle.putBoolean(name, (Boolean) value);
        } else if (value instanceof Double) {
            bundle.putDouble(name, (Double) value);
        } else if (value instanceof Integer) {
            bundle.putInt(name, (Integer) value);
        } else if (value instanceof Long) {
            bundle.putLong(name, (Long) value);
        } else if (value instanceof String) {
            // ** truncating value **
            String truncatedValue = Utils.truncate100((String) value);
            bundle.putString(name, truncatedValue);
        } else if (value instanceof Float) {
            bundle.putFloat(name, (Float) value);
        } else {
            throw new IllegalArgumentException("Value type not supported: " + value.getClass());
        }
    }
}
