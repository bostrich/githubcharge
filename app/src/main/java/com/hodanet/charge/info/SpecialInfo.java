package com.hodanet.charge.info;

import android.content.Context;

import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.utils.DialogUtil;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class SpecialInfo extends BaseInfo {

    private String iconUrl;
    private Long pkgSize;
    private String slogan;
    private List<String> pics = new ArrayList<>();

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        if(pics != null){
            this.pics.addAll(pics);
        }
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

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
                    SpecialInfo.this.report(context, Constants.Event.CONTENT_SHOW);
                }
            });
        }else if(getInfoType() == Constants.INFO_TYPE_APP){
            DialogUtil.showDownloadHint(context, getName(), new DialogUtil.ConfirmListener() {
                @Override
                public void confirm() {
                    DownloadUtil.downloadApk(context, SpecialInfo.this, DownloadManager.DOWNLOAD_STRATERY_SERVICE
                            , new NotificationDownloadFeedback(context));
                }

                @Override
                public void cancle() {

                }
            });
        }
    }
}
