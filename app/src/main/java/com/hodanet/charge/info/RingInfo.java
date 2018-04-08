package com.hodanet.charge.info;

import android.content.Context;

import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.utils.DialogUtil;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;

/**
 * Created by June on 2018/4/8.
 */

public class RingInfo extends BaseInfo {

    private String iconUrl;
    private Long pkgSize;


    public Long getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(Long pkgSize) {
        this.pkgSize = pkgSize;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public void click(final Context context) {
        this.report(context, Constants.Event.CLICK);
        if(getInfoType() == Constants.INFO_TYPE_WEB){
            WebLaunchUtil.launchWeb(context, this, new WebHelper.SimpleWebLoadCallBack(){
                @Override
                public void loadComplete(String url) {
                   RingInfo.this.report(context, Constants.Event.CONTENT_SHOW);
                }
            });
        }else if(getInfoType() == Constants.INFO_TYPE_APP){
            DialogUtil.showDownloadHint(context, getName(), new DialogUtil.ConfirmListener() {
                @Override
                public void confirm() {
                    DownloadUtil.downloadApk(context, RingInfo.this, DownloadManager.DOWNLOAD_STRATERY_SERVICE
                            , new NotificationDownloadFeedback(context));
                }

                @Override
                public void cancle() {

                }
            });
        }
    }
}
