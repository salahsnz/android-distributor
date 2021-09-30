package com.zopnote.android.merchant.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.intro.IntroActivity;
import com.zopnote.android.merchant.login.LoginActivity;

import static com.zopnote.android.merchant.BuildConfig.PRODUCT_FLAVOUR_MERCHANT;

/**
 * Created by nmohideen on 03/03/18.
 */

public class AppLaunchUtil {

    private static boolean sessionSkipIntro = false;

    public static void setSessionSkipIntro(boolean sessionSkipIntro) {
        AppLaunchUtil.sessionSkipIntro = sessionSkipIntro;
    }

    public static void startNextActivity(Activity activityContext) {
        boolean initComplete = Prefs.getBoolean(AppConstants.PREFS_APP_INIT_COMPLETE, false);
        //remove
       // startNextActivity(activityContext, HomeActivity.class);
        if(PRODUCT_FLAVOUR_MERCHANT)
        {
            Log.d("CSD","------------------MERCHANT BUILD----------------------------");
        }
        else
        {
            Log.d("CSD","---------------------------ADMIN BUILD--------------------------");
        }

        if ( ! initComplete) {
            if ( ! sessionSkipIntro) {
                startNextActivity(activityContext, IntroActivity.class);
                return;
            }
        }

        if ( ! initComplete) {
            startNextActivity(activityContext, LoginActivity.class);
            return;
        }

        startNextActivity(activityContext, HomeActivity.class);
    }

    private static void startNextActivity(Context context, Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

}
