package com.fillingapps.twittnearby.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtils {

    private static final String PREF_CENTER_LATITUDE = "com.fillingapps.twittnearby.utils.SharedPreferencesUtils.PREF_CENTER_LATITUDE";
    private static final String PREF_CENTER_LONGITUDE = "com.fillingapps.twittnearby.utils.SharedPreferencesUtils.PREF_CENTER_LONGITUDE";

    public static void savePrefCenterLocation(Context context, double latitude, double longitude) {
        savePrefLatitude(context, latitude);
        savePrefLongitude(context, longitude);
    }

    public static void savePrefLatitude(Context context, double value) {
        savePreferece(context, PREF_CENTER_LATITUDE, value);
    }

    public static void savePrefLongitude(Context context, double value) {
        savePreferece(context, PREF_CENTER_LONGITUDE, value);
    }


    public static void savePreferece(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(key, value).apply();
    }

    public static void savePreferece(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(key, value).apply();
    }

    public static void savePreferece(Context context, String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putLong(key, value).apply();
    }

    public static void savePreferece(Context context, String key, double value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putFloat(key, (float) value).apply();
    }

    public static void savePreferece(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(key, value).apply();
    }

    public static double getPrefCenterLatitude(Context context) {
        return getPreferenceDouble(context, PREF_CENTER_LATITUDE, 42.818432);
    }

    public static double getPrefCenterLongitude(Context context) {
        return getPreferenceDouble(context, PREF_CENTER_LONGITUDE, -1.644143);
    }

    public static String getPreferenceString(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, "");
    }

    public static int getPreferenceInt(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, Integer.MIN_VALUE);
    }

    public static long getPreferenceLong(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, -1);
    }

    public static double getPreferenceDouble(Context context, String key, double defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (double)prefs.getFloat(key, (float)defaultValue);
    }

    public static void removePreference(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(key).apply();
    }

}
