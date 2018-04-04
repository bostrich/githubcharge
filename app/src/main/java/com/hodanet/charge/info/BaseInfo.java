package com.hodanet.charge.info;

import android.content.Context;

import com.hodanet.charge.info.report.BaseReportInfo;
import com.hodanet.charge.utils.Stats;


/**
 *
 */

public abstract class BaseInfo<T extends BaseReportInfo> {
    private long id;
    private String name;
    private String pkgName;
    private int infoType;
    private String url;
    private T reportInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getInfoType() {
        return infoType;
    }

    public void setInfoType(int infoType) {
        this.infoType = infoType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(T reportInfo) {
        this.reportInfo = reportInfo;
    }

    public abstract  void click(Context context);

    public void report(Context context, Constants.Event event){
        if(reportInfo != null){
            Stats.eventToYoumeng(context, reportInfo.getUmengEventId()
                    , event.getActionName(), reportInfo.getLocation() + name);
            if(infoType == Constants.INFO_TYPE_WEB){
                Stats.reportAdv(id, event.getActionType(), reportInfo.getReportAdType(), reportInfo.getPosition());
            }else if(infoType == Constants.INFO_TYPE_APP){
                Stats.reportWallAppStats(id, pkgName, event.getActionType(), reportInfo.getReportAdType()
                        , reportInfo.getPosition());
            }
        }
    }

}
