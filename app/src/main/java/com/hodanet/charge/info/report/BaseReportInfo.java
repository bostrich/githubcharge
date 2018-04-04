package com.hodanet.charge.info.report;

/**
 *
 */

public abstract class BaseReportInfo {

    private String umengEventId;
    private int reportAdType;
    private String location;//用于上报位置 + adName
    private int position;

    public BaseReportInfo(){
        initParams();
    }

    public abstract void initParams();

    public String getUmengEventId() {
        return umengEventId;
    }

    public void setUmengEventId(String umengEventId) {
        this.umengEventId = umengEventId;
    }

    public int getReportAdType() {
        return reportAdType;
    }

    public void setReportAdType(int reportAdType) {
        this.reportAdType = reportAdType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
