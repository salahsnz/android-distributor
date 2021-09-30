package com.zopnote.android.merchant.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zopnote.android.merchant.AppConstants;


public class Prefs {

    private static SharedPreferences sharedPref;

    private Prefs() {}

    public static void init(Context context) {
        sharedPref = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences() {
        if (sharedPref == null) {
            throw new IllegalStateException("Prefs is not initialized.");
        }

        return sharedPref;
    }

    public static String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        return getSharedPreferences().getFloat(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static Long getLong(String key, Long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    // write methods
    public static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    public static void putBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    public static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }


    public static void putFloat(String key, float value) {
        getSharedPreferences().edit().putFloat(key, value).apply();
    }


    public static void putLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }

    public static boolean contains(String key) {
        return getSharedPreferences().contains(key);
    }
}
