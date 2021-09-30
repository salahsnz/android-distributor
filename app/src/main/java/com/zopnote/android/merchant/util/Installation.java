package com.zopnote.android.merchant.util;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.zopnote.android.merchant.AppConstants;

public class Installation {

    public static final boolean DEBUG_MODE = false;
    public static final String DEBUG_UID = "set-me-if-true";

    public synchronized static String getUid(Context context) {
        if (DEBUG_MODE) {
            return DEBUG_UID;
        } else {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public static String getVendorUid(Context context) {
        String vendorUid = Prefs.getString(AppConstants.PREF_VENDOR_UID, "");
        if( ! vendorUid.isEmpty()){
            return vendorUid;
        }else{
            return getUid(context);
        }
    }
}
