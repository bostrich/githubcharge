package com.hodanet.charge.download.downloadUnit;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hodanet.charge.download.DownloadBean;
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.DownloadFeedbackImpl;
import com.hodanet.charge.event.DownloadEvent;
import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.gen.DownloadBeanDao;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 *
 */

public class DownloadEventbus implements Runnable, DownloadImpl {


    private static final String TAG = DownloadEventbus.class.getName();
    private int exitStrategy;
    private Context context;
    private DownloadBean downloadBean;
    private boolean stop;

    public DownloadEventbus() {}


    public DownloadEventbus(Context context, DownloadBean downloadBean) {
        this.context = context;
        this.downloadBean = downloadBean;
    }

    public void setExitStrategy(int exitStrategy) {
        this.exitStrategy = exitStrategy;
    }


    @Override
    public void run() {
        download(downloadBean);
    }

    @Override
    public void download(DownloadBean bean) {
        File directory = new File(Environment.getExternalStorageDirectory() + DownloadManager.DOWNLOAD_LOCATION);
        if(!directory.exists()) directory.mkdir();
        GreenDaoManager.getInstance(context).getSession().clear();//清除缓存信息
        DownloadBeanDao dao = GreenDaoManager.getInstance(context).getSession().getDownloadBeanDao();
        DownloadBean info = null;
        File fileTemp = new File(directory + "/" + downloadBean.getAppName() + ".temp");
        File filePath = new File(directory + "/" + downloadBean.getAppName() + ".apk");
        long totalSize = 0;
        long currentSize = 0;
        if(fileTemp.exists()){
            List<DownloadBean> list = dao.queryBuilder().where(DownloadBeanDao.Properties.PkgName.eq(downloadBean.getPkgName())).build().list();
            if(list.size() > 0){
                info = list.get(0);
                totalSize = info.getTotalSize();
                currentSize = fileTemp.length();
                Log.e(TAG, "已有文件继续下载" + "currentSize:" + currentSize + "--totalSize:" + totalSize);
            }else{
                fileTemp.delete();
                info = downloadBean;
                Log.e(TAG, "没有文件--" + "currentSize:" + currentSize + "--totalSize:" + totalSize);
            }
        }else{
            info = downloadBean;
        }

        HttpURLConnection conn = null;
        InputStream in = null;
        RandomAccessFile raf = null;
        try {
            conn = (HttpURLConnection) new URL(downloadBean.getUrl()).openConnection();
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            if(currentSize > 0){
                conn.setRequestProperty("RANGE", "bytes=" + currentSize + "-" + totalSize);
            }else{
                totalSize = conn.getContentLength();
            }
            info.setTotalSize(totalSize);
            dao.insertOrReplace(info);
            if(conn.getResponseCode() >= 400){

            }else{
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.DOWNLOAD_START, downloadBean.getPkgName()));
                in = conn.getInputStream();
                raf = new RandomAccessFile(fileTemp, "rwd");
                raf.seek(currentSize);
                byte[] buf = new byte[1024];
                int readNum = 0;
                long reportTime = System.currentTimeMillis();
                while(!stop && (readNum = in.read(buf)) != -1){
                    raf.write(buf, 0, readNum);
                    currentSize += readNum;
                    if(System.currentTimeMillis() - reportTime > 1000){
                        reportTime = System.currentTimeMillis();
                        Log.e(TAG, "下载进度：" + "currentSize:" + currentSize + "--totalSize:" + totalSize);
                        DownloadEvent downloadEvent = new DownloadEvent(DownloadEvent.DOWNLOAD_PROGRESS, downloadBean.getPkgName());
                        downloadEvent.setCurrentSize(currentSize);
                        downloadEvent.setTotalSize(totalSize);
                        EventBus.getDefault().post(downloadEvent);
                    }
                }
                if(currentSize == totalSize){//下载完成
                    dao.delete(info);
                    fileTemp.renameTo(filePath);
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.DOWNLOAD_FINISH, downloadBean.getPkgName()));
                    DownloadManager.getInstance(context).remove(downloadBean.getUrl());
                    Log.e(TAG, "下载完成");
                }else{
                    Log.e(TAG, "下载到一半" + "currentSize:" + currentSize + "--totalSize:" + totalSize);
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.DOWNLOAD_STOP, downloadBean.getPkgName()));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new DownloadEvent(DownloadEvent.DOWNLOAD_FAILED, downloadBean.getPkgName()));
        }finally{
            try {
                conn.disconnect();
                raf.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop(){
        stop = true;
    }

    @Override
    public void setDownloadFeedback(DownloadFeedbackImpl feedback) {

    }


    @Override
    public int getExitStrategy() {
        return exitStrategy;
    }
}
