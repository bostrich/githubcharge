package com.hodanet.charge.greendao;


import com.hodanet.charge.info.BannerInfo;
import com.hodanet.charge.info.BaseInfo;
import com.hodanet.charge.info.report.BaseReportInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 同一广告类型便于统一及统计
 *
 */
@Entity
public class StandardInfo {
    public static final int SPLASH = 2;
    public static final int FLOAT = 9;
    public static final int RECOMMAND = 8;
    public static final int BANNER = 9;
    public static final int TYPE_NEWS = 1;
    public static final int TYPE_WEB = 2;
    public static final int TYPE_APK = 3;
    public static final int TYPE_RING = 12;
    @NotNull
    @Unique
    private long adId;//广告ID
    private String picUrl;//大图片地址
    private String icon;//icon图片地址
    private int order;//广告顺序
    private String desUrl;//目标URL：网页、apk下载地址、跳转activity名称

    //广告类型：新闻、网页广告、apk广告、activity类型
    private int type;
    //广告位置用于上报：2:开屏,9：首页悬浮球，8：wifi信息页为您推荐，9：发现页新闻banner，3：发现页新闻中广告，3：段子中广告
    private int position;

    private int state;//用于记录apk 广告类型状态： 0：未下载，1：下载未完成， 2：暂停， 3：下载完成
    private long size;//记录apk大小
    private String name;//广告名称
    private String description;//广告描述，apk包名
    private String slogan;//应用描述
    private String desc1;//为您推荐 按钮描述

    @Generated(hash = 1467483926)
    public StandardInfo(long adId, String picUrl, String icon, int order,
            String desUrl, int type, int position, int state, long size,
            String name, String description, String slogan, String desc1) {
        this.adId = adId;
        this.picUrl = picUrl;
        this.icon = icon;
        this.order = order;
        this.desUrl = desUrl;
        this.type = type;
        this.position = position;
        this.state = state;
        this.size = size;
        this.name = name;
        this.description = description;
        this.slogan = slogan;
        this.desc1 = desc1;
    }
    @Generated(hash = 1393596397)
    public StandardInfo() {
    }

    public long getAdId() {
        return this.adId;
    }
    public void setAdId(long adId) {
        this.adId = adId;
    }
    public String getPicUrl() {
        return this.picUrl;
    }
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public String getDesUrl() {
        return this.desUrl;
    }
    public void setDesUrl(String desUrl) {
        this.desUrl = desUrl;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getPosition() {
        return this.position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSlogan() {
        return this.slogan;
    }
    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
    public String getDesc1() {
        return this.desc1;
    }
    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    /**
     *
     * @return
     */
    public static BannerInfo convertInfo(StandardInfo adInfo, BaseReportInfo reportInfo){
        BannerInfo info = new BannerInfo();
        info.setInfoType(adInfo.getType());
        info.setId(adInfo.getAdId());
        info.setUrl(adInfo.getDesUrl());
        info.setName(adInfo.getName());
        info.setReportInfo(reportInfo);
        return info;
    }

}
