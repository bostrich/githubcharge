package com.hodanet.charge.info.hot;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/2/3.
 */
public class AdInfo implements Comparable<AdInfo> {
    public static int AD_TYPE_WEB = 1;
    public static int AD_TYPE_APP = 2;
    //一般广告属性
    public int subType;//51悬浮球类型
    public int cooperationMode;
    public int id;
    public String name;
    public String picUrl;
    public String clickUrl;
    public String date;
    public String source;
    public Drawable bigPic;//信息流退出广告中使用
    //应用推荐属性
    public int appId;
    public Drawable appIcon;
    public String iconUrl;
    public String appName;
    public String pkgName;
    public float score;
    public String slogan;
    public String introduce;
    public long apkSize;
    public String apkUrl;
    public String advUrl;
    public String buttonName;
    public String picture;
    //应用banner条图片url
    public String banner;
    //排位属性
    public Integer order;
    public int closeTime;//超时未点击，自动关闭广告时间
    //类型属性
    public int adType = 1;//1：网页广告，2：应用广告
    public boolean isSelected;//判断app是否被选中下载
    public boolean isDownloaded;//判断是否已下载完成
    public boolean isClick;//判断是否点击
    public boolean isFiltered;//判断此广告是否被过滤了
    public boolean isStandbyAd;//判断此广告是否是备用的
    public List<String> picSources = new ArrayList<>();//图片轮播资源
    public int showCount;//展示次数 用于图片资源轮播
    public int checkStatus;//广告状态，用于判断是否报毒过滤
    public boolean isReportExtShow;//为您推荐判断是否上报外部展示

    public int compareTo(AdInfo arg0) {
        return this.order.compareTo(arg0.order);
    }
}
