package com.hodanet.charge.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.SQLDownloadInfo;
import com.hodanet.charge.greendao.gen.SQLDownloadInfoDao;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.Stats;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * app 安装接收
 */

public class AppInstallReceiver extends BroadcastReceiver{

    private static final String TAG = AppInstallReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String pkgName = intent.getData().getSchemeSpecificPart();
        final String action = intent.getAction();
        LogUtil.e(TAG, "包名：" + pkgName);
        if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
            SQLDownloadInfoDao dao = GreenDaoManager.getInstance(context).getSession().getSQLDownloadInfoDao();
            List<SQLDownloadInfo> list = dao.queryBuilder()
                    .where(SQLDownloadInfoDao.Properties.PkgName.eq(pkgName))
                    .orderDesc(SQLDownloadInfoDao.Properties.DownloadTime)
                    .build()
                    .list();
            if(list.size() > 0){
                SQLDownloadInfo downloadInfo = list.get(0);
                Stats.reportWallAppStats(downloadInfo.getAdId(), downloadInfo.getPkgName(), Constants.Event.INSTALLED.getActionType()
                        , downloadInfo.getReportType(), downloadInfo.getPosition());
            }
        }

    }
}
