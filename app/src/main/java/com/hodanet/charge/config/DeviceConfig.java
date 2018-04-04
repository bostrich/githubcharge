package com.hodanet.charge.config;

import android.content.Context;
import android.os.Build;

/**
 * 设备参数信息
 */

public class DeviceConfig {

    public static float SCREEN_DENSITY;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static float SCREEN_SCALE;

    public static int SDK_INT;


    public static void getDeviceConfig(Context context){
        if(context != null){
            SCREEN_DENSITY = context.getResources().getDisplayMetrics().density;
            SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
            SCREEN_HEIGHT = context.getResources().getDisplayMetrics().heightPixels;
            SCREEN_SCALE = context.getResources().getDisplayMetrics().scaledDensity;
            SDK_INT = Build.VERSION.SDK_INT;
        }
    }
}
