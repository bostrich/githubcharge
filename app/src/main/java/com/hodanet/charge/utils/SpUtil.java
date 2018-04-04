package com.hodanet.charge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *
 */

public class SpUtil {
    public static final String IMEI = "IMEI_DEFAULT_VALUE";



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
}
