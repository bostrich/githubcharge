package com.hodanet.charge.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;

/**
 * 屏幕亮度工具类
 */

public class BrightnessUtil {


    public static int getSystemBrightness(Context context){
        int systemBrightness = 0;
        try{
            systemBrightness = Settings.System.getInt(context.getContentResolver()
                    , Settings.System.SCREEN_BRIGHTNESS);
        }catch(Exception e){
            e.printStackTrace();
        }
        return systemBrightness;
    }

    public static int getSystemBrightnessMode(Context context){
        int mode = 0;
        try{
            mode = Settings.System.getInt(context.getContentResolver()
                    , Settings.System.SCREEN_BRIGHTNESS_MODE);
        }catch(Exception e){
            e.printStackTrace();
        }
        return mode;
    }

    public static void saveBrightness(Activity activity, int brightness, int mode) {
        //需要关闭屏幕自动调节亮度的功能
        try{
            Settings.System.putInt(activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            activity.getContentResolver().notifyChange(uri, null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
