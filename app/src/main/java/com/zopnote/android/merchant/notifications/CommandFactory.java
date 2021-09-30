package com.zopnote.android.merchant.notifications;

import android.content.Context;
import android.os.Bundle;

import com.zopnote.android.merchant.data.model.PushNotification;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nmohideen on 02/03/18.
 */

public class CommandFactory {
    public static final String TYPE_APP_CHECK = "AppCheck";
    public static final String TYPE_REFRESH_CONFIG = "RefreshConfig";
    public static final String TYPE_SURVEY_CHECK = "SurveyCheck";

    public static Command create(Context context, Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        if (bundle.getString(PushNotification.ATTR_TYPE) == null) {
            return null;
        }
        String type = bundle.getString(PushNotification.ATTR_TYPE);

        if (bundle.getString(PushNotification.ATTR_DATA) == null) {
            return null;
        }
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(bundle.getString(PushNotification.ATTR_DATA));
        } catch (JSONException ignore) {
            return null;
        }

        if (TYPE_REFRESH_CONFIG.equalsIgnoreCase(type)) {
            return new RefreshConfigCommand(context, jsonObj);
        }

        // no match
        return null;
    }
}
