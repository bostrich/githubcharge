package com.hodanet.charge.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 消息提示
 *
 * @author YL
 */
public class ToastUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;
    private static Object synObj = new Object();

    public static void toast(final Context act, final String msg) {
        toast(act, msg, Toast.LENGTH_SHORT);
    }

    public static void toastLong(final Context act, final String msg) {
        toast(act, msg, Toast.LENGTH_LONG);
    }

    private static void toast(final Context act, final String msg, final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    if (toast != null) {
                        toast.setText(msg);
                    } else {
                        toast = Toast.makeText(act, msg, len);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                    }
                    toast.show();
                }
            }
        });
    }
}
