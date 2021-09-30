package com.zopnote.android.merchant.data.model;

import android.content.Context;

import com.zopnote.android.merchant.AppConstants;

import org.json.JSONObject;

/**
 * Created by Ravindra on 9/1/2018.
 */

public class AppUpdateInfo {
    /* sample json
	{
		  "newestVersion": {
		    "versionCode": 26,
		  },
		    "minVersion": {
		    "versionCode": 12,
		  },
		  "notes": "some notes"
	}
	*/

    private int newestVersionCode;
    private int minVersionCode;
    private int currVersionCode;
    private String notes;

    private final static String ATTR_NEWEST_VERSION = "newestVersion";
    private final static String ATTR_MIN_VERSION = "minVersion";
    private final static String ATTR_VERSION_CODE = "versionCode";
    private final static String ATTR_NOTES = "notes";

    public AppUpdateInfo(String jsonStr, Context context) {
        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONObject jsonObj = root.getJSONObject(ATTR_NEWEST_VERSION);
            newestVersionCode = jsonObj.getInt(ATTR_VERSION_CODE);

            // reuse same name to avoid copy paste errors
            jsonObj = root.getJSONObject(ATTR_MIN_VERSION);
            minVersionCode = jsonObj.getInt(ATTR_VERSION_CODE);

            if (root.has(ATTR_NOTES)) {
                notes = root.getString(ATTR_NOTES);
            }

            currVersionCode = AppConstants.getAppVersionCode(context);
        } catch (Exception e) {
            // TODO
//			Crashlytics.logException(e);
            throw new IllegalArgumentException();
        }
    }

    public boolean isUpdateAvailable() {
        return newestVersionCode > currVersionCode;
    }

    public boolean isUpdateMandatory() {
        return currVersionCode < minVersionCode;
    }

    public String getNotes() {
        return notes;
    }
}
