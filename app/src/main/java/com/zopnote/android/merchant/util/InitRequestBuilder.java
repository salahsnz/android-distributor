package com.zopnote.android.merchant.util;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.MyApplication;

public class InitRequestBuilder {

    private Context mContext;
    private JSONObject mJsonObj;

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "InitRequestBuilder";

    public InitRequestBuilder(Context context) {
        mContext = context;
        mJsonObj = new JSONObject();
    }

    public JSONObject build() {
        try {
            return mJsonObj;
        } finally {
            // no more operations allowed
            mJsonObj = null;
        }
    }

    public InitRequestBuilder setDebugIfApplicable() {
        try {
            if (BuildConfig.BUILD_TYPE_DEBUG) {
                mJsonObj.put(AppConstants.ATTR_DEBUG, true);
                mJsonObj.put(AppConstants.ATTR_KEY, AppConstants.DEBUG_KEY);
            }
        } catch (JSONException ignore) {
        }
        return this;
    }

    public InitRequestBuilder setAifa() {
        try {
            mJsonObj.put(AppConstants.ATTR_AIFA,
                    ((MyApplication) mContext.getApplicationContext()).getAdvertisingId());
        } catch (JSONException ignore) {
        }
        return this;
    }

    public InitRequestBuilder setAppVersion() {
        try {
            mJsonObj.put(AppConstants.ATTR_APP_VERSION,
                    mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
        } catch (JSONException ignore) {
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return this;
    }


    public InitRequestBuilder setUid() {
        try {
            mJsonObj.put(AppConstants.ATTR_UID, Installation.getUid(mContext));
        } catch (JSONException ignore) {
        }
        return this;
    }

    public InitRequestBuilder setAccountKitAuthCode(String accountKitAuthCode) {
        if (accountKitAuthCode == null) {
            // nothing to do
            return this;
        }
        try {
            mJsonObj.put(AppConstants.ATTR_ACCOUNT_KIT_AUTHCODE, accountKitAuthCode);
        } catch (JSONException ignore) {
        }
        return this;
    }

    public InitRequestBuilder setFirebaseProjectId() {
        try {
            int projectStringResourceId = mContext.getResources().getIdentifier("project_id", "string", mContext.getPackageName());
            String projectId = mContext.getResources().getString(projectStringResourceId);
            mJsonObj.put(AppConstants.ATTR_FIREBASE_PROJECT_ID, projectId);
        } catch (JSONException ignore) {
        }
        return this;
    }

    public InitRequestBuilder setKey() {
        try {
            mJsonObj.put("key", "76380346");
        } catch (JSONException ignore) {
        }
        return this;
    }

    public InitRequestBuilder setMobileNumber(String mobile) {
        try {
            mJsonObj.put("mobileNumber", mobile);
        } catch (JSONException ignore) {
        }
        return this;
    }


}
