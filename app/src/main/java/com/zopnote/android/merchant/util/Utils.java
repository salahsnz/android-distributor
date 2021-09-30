package com.zopnote.android.merchant.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.event.AppUpdateAvailableEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class Utils {

    public static boolean enforceConnection(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || ((networkInfo.isConnected()) == false)) {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean enforceGooglePlayServices(Activity context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if(status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(context, status, 99).show();
            }
            return false;
        }
        return true;
    }

    public static String truncate40(String input) {
        return truncate(input, 40);
    }

    public static String truncate100(String input) {
        return truncate(input, 100);
    }

    public static String truncate(String input, int truncateLength) {
        if (input == null) {
            return null;
        }

        return input.substring(0, Math.min(input.length(), truncateLength));
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        return GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public static String getAssetFile(Context context, String assetFileName) {
        BufferedReader bufferedReader = null;
        StringBuilder data = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetFileName)));
            int ch;
            while ((ch = bufferedReader.read()) != -1) {
                char character = (char) ch;
                data.append(character);
            }
        } catch (Exception ignore) {
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException ignore) { }
        }
        return data.toString();
    }

    public static String getRegexString (String preString) {
        String postString = "";

        String[] preStringArray = preString.split("\\|");

        for (int i=0; i<preStringArray.length; i++) {
            if (i == preStringArray.length -1) {
                postString += "(?i)" + preStringArray[i];
            } else {
                postString += "(?i)" + preStringArray[i] + "|";
            }
        }

        postString = "\\b(" + postString + ")\\b";

        return postString;
    }

    public static void showSuccessToast(Activity activity, String message, int toastLength){
        View layout = activity.getLayoutInflater().inflate(R.layout.custom_toast_success, (ViewGroup) activity.findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.message);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(toastLength);
        toast.setView(layout);
        toast.show();
    }

    public static void showFailureToast(Activity activity, String message, int toastLength){
        View layout = activity.getLayoutInflater().inflate(R.layout.custom_toast_failure, (ViewGroup) activity.findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.message);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(toastLength);
        toast.setView(layout);
        toast.show();
    }

    public static String getAddressLine1(Context context, String addressLine1) {
        StringBuilder addressLine1Builder = new StringBuilder();
        try {
            JSONObject addressLine1Object = new JSONObject(addressLine1);
            Iterator<?> keys = addressLine1Object.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( addressLine1Object.get(key) instanceof String ) {
                    addressLine1Builder.append( addressLine1Object.get(key));
                }

                if(keys.hasNext()){
                    addressLine1Builder.append(" ");
                    addressLine1Builder.append(context.getResources().getString(R.string.bullet_char));
                    addressLine1Builder.append(" ");
                }
            }
        } catch (JSONException e) {
            //for legacy data: if not JSONArray then it's a String
            addressLine1Builder.append(addressLine1);
        }
        return addressLine1Builder.toString();
    }

    public static ArrayList<String> getAddressLine1(String addressLine1) {
        ArrayList<String> address = new ArrayList<>();
        try {
            JSONObject addressLine1Object = new JSONObject(addressLine1);
            Iterator<?> keys = addressLine1Object.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( addressLine1Object.get(key) instanceof String ) {
                    address.add(addressLine1Object.get(key).toString());
                }
            }
        } catch (JSONException e) {
            //for legacy data: if not JSONArray then it's a String
            address.add(addressLine1);
        }
        return address;
    }

    public static Calendar getStartOfMonthDateCalender() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static boolean hasValidMobileNumber(String mobileNumber) {
        if(mobileNumber != null && mobileNumber.trim().length() >0){
            if (mobileNumber.matches("^[6789]\\d{9}$")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNull(View view) {
        if(view == null){
            return true;
        }
        return false;
    }

    public static String numberToWord(int n){
        String  one[]={" "," one"," two"," three"," four"," five"," six"," seven"," eight"," Nine"," ten"," eleven"," twelve"," thirteen"," fourteen","fifteen"," sixteen"," seventeen"," eighteen"," nineteen"};

        String ten[]={" "," "," twenty"," thirty"," forty"," fifty"," sixty","seventy"," eighty"," ninety"};

        if(n > 19) { return ten[n/10]+ " "+ one[n%10]; } else { return one[n]; }
    }

    public static void checkForAppUpdate() {
        int currVersionCode = AppConstants.getAppVersionCode(getApplicationContext());
        int latestVersionCode = Integer.parseInt(FirebaseRemoteConfig.getInstance().getString(AppConstants.REMOTE_CONFIG_LATEST_APP_VERSION_CODE));

        if (latestVersionCode > currVersionCode) {
            EventBus.getDefault().postSticky(new AppUpdateAvailableEvent());
        }
    }
}
