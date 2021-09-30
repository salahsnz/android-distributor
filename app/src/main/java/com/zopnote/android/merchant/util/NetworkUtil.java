package com.zopnote.android.merchant.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.zopnote.android.merchant.R;

public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean enforceNetworkConnection(Context context) {
        if (isNetworkAvailable(context)) {
            return true;
        } else {
            Toast.makeText(context,
                    context.getResources().getString(R.string.no_network_error),
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
    }
}
