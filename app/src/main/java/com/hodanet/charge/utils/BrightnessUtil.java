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

    public static void saveBrightness(Activity activity, int brightness) {
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        activity.getContentResolver().notifyChange(uri, null);
    }



}
