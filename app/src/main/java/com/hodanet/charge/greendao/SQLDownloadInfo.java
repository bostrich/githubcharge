package com.hodanet.charge.greendao;


import com.hodanet.charge.info.BaseInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 *
 */

@Entity
public class SQLDownloadInfo {

    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    @Unique
    private String pkgName;
    private String appName;

    private long adId;
    private String apkUrl;

    private int reportType;
    private String umengId;
    private String location;
    private int position;
    private long downloadTime;
    private int state;
    
    public SQLDownloadInfo(BaseInfo baseInfo){
        if(baseInfo != null && baseInfo.getReportInfo() != null){
            this.pkgName = baseInfo.getPkgName();
            this.appName = baseInfo.getName();
            this.adId = baseInfo.getId();
            this.apkUrl = baseInfo.getUrl();
            this.position = baseInfo.getReportInfo().getPosition();
            this.location = baseInfo.getReportInfo().getLocation();
            this.reportType = baseInfo.getReportInfo().getReportAdType();
            this.umengId = baseInfo.getReportInfo().getUmengEventId();
            this.downloadTime = System.currentTimeMillis();
        }
    }

    @Generated(hash = 1487942314)
    public SQLDownloadInfo(Long _id, @NotNull String pkgName, String appName,
            long adId, String apkUrl, int reportType, String umengId,
            String location, int position, long downloadTime, int state) {
        this._id = _id;
        this.pkgName = pkgName;
        this.appName = appName;
        this.adId = adId;
        this.apkUrl = apkUrl;
        this.reportType = reportType;
        this.umengId = umengId;
        this.location = location;
        this.position = position;
        this.downloadTime = downloadTime;
        this.state = state;
    }

    @Generated(hash = 1621672597)
    public SQLDownloadInfo() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAdId() {
        return this.adId;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public String getApkUrl() {
        return this.apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public int getReportType() {
        return this.reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getUmengId() {
        return this.umengId;
    }

    public void setUmengId(String umengId) {
        this.umengId = umengId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getDownloadTime() {
        return this.downloadTime;
    }

    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
