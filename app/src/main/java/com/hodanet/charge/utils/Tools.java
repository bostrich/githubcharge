package com.hodanet.charge.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 *
 */

public class Tools {

    private static final String TAG = Tools.class.getName();

    /**
     * 获取两个值范围内的随机值
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        if (max > min) {
            return min + new Random().nextInt(max - min + 1);
        } else if (max < min) {
            return max + new Random().nextInt(min - max + 1);
        } else {
            return min;
        }
    }


    /**
     * 获取下载到本地的文件
     */
    public static File getDownloadFile(Context context, String dirName, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + dirName;
        File dirFile = new File(dirPath);
        if (dirFile.exists()) {
            String filePath = dirPath + "/" + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }


    /**
     * 删除下载到本地的文件
     */
    public static void deleteDownloadFile(Context context, String dirName, String fileName) {
        try {
            String dirPath = Environment.getExternalStorageDirectory() + "/" + dirName;
            File dirFile = new File(dirPath);
            if (dirFile.exists()) {
                String filePath = dirPath + "/" + fileName;
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载文件到本地目录
     */
    public static File downloadFile(Context context, String dirName, String fileName, String url) {
        // 设置下载保存路径
        String dirPath = Environment.getExternalStorageDirectory() + "/" + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        final String tempPath = dirPath + "/" + fileName + ".temp";
        final String filePath = dirPath + "/" + fileName;
        final File file = new File(tempPath);
        try {
            // 创建下载链接
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(6 * 1000);
            conn.setReadTimeout(6 * 1000);
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[256];
            conn.connect();
            // 开始下载
            if (conn.getResponseCode() >= 400) {
                LogUtil.i(TAG, "下载失败，连接超时");
            } else {
                int numRead = 0;
                while ((numRead = is.read(buf)) != -1) {
                    fos.write(buf, 0, numRead);
                }
            }
            conn.disconnect();
            fos.close();
            is.close();
            // 下载完毕后更改为正式名称
            File newFile = new File(filePath);
            file.renameTo(newFile);
            return newFile;
        } catch (Exception e) {
            LogUtil.e(TAG, "download false : " + e.getMessage());
        }
        return null;
    }
}
