package com.hodanet.charge.utils;

import android.os.Environment;
import android.util.Log;

import com.hodanet.charge.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * LogCat的工具，实现全局开关
 *
 * @author YL
 */

public class LogUtil {

    public static final boolean DEBUG = BuildConfig.LOG_DEBUG;

    public static void v(String tag, String msg) {
        if (DEBUG) {
            if (msg == null) {
                msg = "null";
            }
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            if (msg == null) {
                msg = "null";
            }
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            if (msg == null) {
                msg = "null";
            }
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            if (msg == null) {
                msg = "null";
            }
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            if (msg == null) {
                msg = "null";
            }
            Log.e(tag, msg);
        }
    }

    public static void saveLog(String fileName, String log){
        DateFormat formatter = new SimpleDateFormat("HH-mm-ss", Locale.CHINA);
        String date = formatter.format(new Date());
        String sb = date + ":" + log +"\n";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = "/sdcard/crash/";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path + fileName,true);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}