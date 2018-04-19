package com.hodanet.charge.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 *
 */

public class WifiUtil {

    public static boolean openWifi(Context context){
        boolean open = false;
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return manager.setWifiEnabled(true);
    }

    public static boolean closeWifi(Context context){
        boolean close = false;
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return manager.setWifiEnabled(false);
    }

    public static boolean isWifiOpen(Context context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return manager.isWifiEnabled();
    }
}
