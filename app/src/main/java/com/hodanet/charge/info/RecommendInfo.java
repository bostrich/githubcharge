package com.hodanet.charge.info;

import android.content.Context;

import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.utils.DialogUtil;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;


/**
 *
 */

public class RecommendInfo extends BaseInfo {

    private String slogan;
    private String buttonName;
    private String iconUrl;
    private String bigPictureUrl;


    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBigPictureUrl() {
        return bigPictureUrl;
    }

    public void setBigPictureUrl(String bigPictureUrl) {
        this.bigPictureUrl = bigPictureUrl;
    }


    @Override
    public void click(final Context context) {
        RecommendInfo.this.report(context, Constants.Event.CLICK);
        if(getInfoType() == Constants.INFO_TYPE_WEB){
            WebLaunchUtil.launchWeb(context, RecommendInfo.this, new WebHelper.SimpleWebLoadCallBack(){
                @Override
                public void loadComplete(String url) {
                    RecommendInfo.this.report(context, Constants.Event.CONTENT_SHOW);
                }
            });
        }else if(getInfoType() == Constants.INFO_TYPE_APP){
            DialogUtil.showDownloadHint(context, getName(), new DialogUtil.ConfirmListener() {
                @Override
                public void confirm() {
                    DownloadUtil.downloadApk(context, RecommendInfo.this, DownloadManager.DOWNLOAD_STRATERY_SERVICE
                            , new NotificationDownloadFeedback(context));
                }

                @Override
                public void cancle() {

                }
            });
        }
    }

}
