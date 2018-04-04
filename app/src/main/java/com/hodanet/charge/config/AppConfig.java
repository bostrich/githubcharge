package com.hodanet.charge.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.utils.Tools;

/**
 * app 配置信息
 */

public class AppConfig {

    public static boolean IS_LINE_UP;//判断是否首发
    public static String CHANNEL;//渠道
    public static String IMEI;
    public static String PACKAGE_NAME;
    public static int VERSION_CODE;
    public static int USER_TYPE;

    public static void getAppConfig(Context context){
        if(context != null){
            CHANNEL = getMetaDate(context, "UMENG_CHANNEL");
            IS_LINE_UP = getMetaDate(context, "FIRST").equals("yes");
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = tm.getDeviceId();
            if (IMEI == null) {
                if (SpUtil.getImeiDefault(context) != "") {
                    IMEI = SpUtil.getImeiDefault(context);
                } else {
                    IMEI = "wifi" + Tools.getRandom(1, 9998) + Tools.getRandom(1, 9998) + Tools.getRandom(1, 9998);
                    SpUtil.saveImeiDefault(context, IMEI);
                }
            }

            // 获取应用信息
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            PACKAGE_NAME = context.getPackageName();
            try {
                pi = pm.getPackageInfo(PACKAGE_NAME, 0);
                VERSION_CODE = pi.versionCode;
            } catch (Exception e) {

            }
        }
    }


    /**
     * 读取AndroidManifest.xml中<meta-data>元素数据
     */
    public static String getMetaDate(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String value = info.metaData.getString(key);
            return value == null ? "" : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
