package com.hodanet.charge.utils;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * 蓝牙工具类
 */

public class BluetoothUtil {


    /**
     * 开启蓝牙
     * @param context
     * @return
     */
    public static boolean openBluetooth(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            boolean isOpen = false;
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return manager.getAdapter().enable();
        }
        return false;
    }


    public static boolean closeBluetooth(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            boolean isOpen = false;
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return manager.getAdapter().disable();
        }
        return false;
    }

}
