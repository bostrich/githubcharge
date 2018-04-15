package com.hodanet.charge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *
 */

public class SpUtil {
    public static final String IMEI = "IMEI_DEFAULT_VALUE";
    public static final String SPLASH_ORDER = "SPLASH_ORDER";
    public static final String OPTIMIZE_DATA = "opzimize_data";
    public static final String RECOVER_TIME = "recover_time";
    public static final String RECOVER_INFO = "recover_info";
    public static final String DISCOVERY_CLICK_TIME = "discovery_click_time";
    private static String CONFIG = "config";

    private static SharedPreferences sharedPreferences;



    /**
     * 保存随机生成的imei号
     */
    public static synchronized void saveImeiDefault(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(IMEI, value).commit();
    }

    /**
     * 获得随机生成的imei号
     */
    public static String getImeiDefault(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(IMEI, "");
    }


    public static synchronized void saveSplashOrder(Context context, int order){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(SPLASH_ORDER, order).commit();
    }

    public static synchronized int getSplashOrder(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SPLASH_ORDER, 0);
    }


    public static void saveBooleanData(Context context, String key, boolean value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanData(Context context, String key, boolean defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static void saveLongData(Context context, String key, long value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static long getLongData(Context context, String key, long defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getLong(key, defValue);
    }

    public static void saveStringData(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringData(Context context, String key, String defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }


}
