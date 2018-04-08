package com.hodanet.charge.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;

import com.hodanet.charge.download.DownloadBean;
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.DownloadFeedbackImpl;
import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.SQLDownloadInfo;
import com.hodanet.charge.greendao.gen.SQLDownloadInfoDao;
import com.hodanet.charge.info.BaseInfo;

import java.io.File;
import java.util.List;

/**
 *
 */

public class DownloadUtil {
    private static final String TAG = DownloadUtil.class.getName();
    public static final String DOWNLOAD_DIR = "/downloadApk"; // 下载文件的保存路径

    public static void downloadApk(final Context context, final BaseInfo baseInfo, int strategy, DownloadFeedbackImpl feedback){

        if(checkInstall(context, baseInfo.getPkgName())){
            openApp(context, baseInfo.getPkgName());
        }else{
            if(checkDownLoad(context, baseInfo.getName())){
                installApk(context, baseInfo.getName());
            }else{
                if(baseInfo.getReportInfo() != null){
                    //将下载信息保存到数据库中去，便于判断下载完成后续的上报
                    SQLDownloadInfoDao dao = GreenDaoManager.getInstance(context).getSession().getSQLDownloadInfoDao();
                    List<SQLDownloadInfo> list = dao.queryBuilder()
                            .where(SQLDownloadInfoDao.Properties.PkgName.eq(baseInfo.getPkgName())).build().list();
                    if(list.size() > 0){
                        if(baseInfo != null && baseInfo.getReportInfo() != null){
                            SQLDownloadInfo info = list.get(0);
                            info.setAdId(baseInfo.getId());
                            info.setAppName(baseInfo.getName());
                            info.setPosition(baseInfo.getReportInfo().getPosition());
                            info.setLocation(baseInfo.getReportInfo().getLocation());
                            info.setReportType(baseInfo.getReportInfo().getReportAdType());
                            info.setUmengId(baseInfo.getReportInfo().getUmengEventId());
                            info.setDownloadTime(System.currentTimeMillis());
                            dao.update(info);
                        }
                    }else{
                        SQLDownloadInfo info = new SQLDownloadInfo(baseInfo);
                        dao.insertOrReplace(info);
                    }
                }
                DownloadBean bean = DownloadBean.convert(baseInfo);
                DownloadManager.getInstance(context).download(bean, strategy, feedback);
            }
        }
    }


    /**
     * 通过包名，判断某应用是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkInstall(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "checkInstall e:" + e.toString());
            return false;
        }
    }

    /**
     * 通过包名，打开某个应用
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 通过APK名称，判断某应用是否已下载完
     *
     * @param context
     * @param apkName
     * @return
     */
    public static boolean checkDownLoad(Context context, String apkName) {
        File file = new File(getApkPath(apkName));
        return file.exists();
    }

    /**
     * 安装APK
     *
     * @param context
     * @param apkName
     */
    public static boolean installApk(Context context, String apkName) {
        try {
            File file = new File(getApkPath(apkName));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            LogUtil.i(TAG, "installApk e:" + e.toString());
            return false;
        }
    }

    /**
     * 通过APK名称获取设定的APK文件路径， 注意：需要对应downLoadFile()中的路径
     *
     * @param apkName
     * @return
     */
    public static String getApkPath(String apkName) {
        final String fileName = apkName + ".apk";
        String dirPath = Environment.getExternalStorageDirectory() + DOWNLOAD_DIR;
        return dirPath + "/" + fileName;
    }
}
