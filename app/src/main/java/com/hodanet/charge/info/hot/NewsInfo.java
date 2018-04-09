package com.hodanet.charge.info.hot;

import android.graphics.drawable.Drawable;

/**
 *
 */
public class NewsInfo implements Comparable<NewsInfo> {
    public long id;
    public String pk;
    public String name;
    public Drawable pic;
    public String picUrl;
    public String clickUrl;
    public String date;
    public Long position;
    public String source;
    public int type;
    public int source_type;//新闻来源新增字段，用于区分ZAKER或东方网新闻
    public int infoType;

    public String advUrl;
    public String banner;
    public String author;
    public String imgs;

    public Integer advOrder;
    public boolean isReportExternalShow;


    public int recommend;//0:第三方，1：自推

    public int compareTo(NewsInfo arg0) {
        return this.advOrder.compareTo(arg0.advOrder);
    }


    public Boolean isDataValid(NewsInfo info) { //判断数据是否有效，
        Boolean isValid = true;
        if (info.name == null || info.name.trim().equals("")) {
            isValid = false;
        }
        if (info.picUrl == null || info.picUrl.trim().equals("")) {
            isValid = false;
        }
        if (info.clickUrl == null || info.clickUrl.trim().equals("")) {
            isValid = false;
        }


        return isValid;
    }


}
