package com.hodanet.charge.download;

import android.os.Parcel;
import android.os.Parcelable;


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
public class DownloadBean implements Parcelable{
    @Id(autoincrement =  true)
    private Long id;
    @NotNull
    @Unique
    private String pkgName;
    private long totalSize;
    private String appName;
    @NotNull
    private String url;
    @NotNull
    private int adId;


    @Generated(hash = 2109125964)
    public DownloadBean(Long id, @NotNull String pkgName, long totalSize,
            String appName, @NotNull String url, int adId) {
        this.id = id;
        this.pkgName = pkgName;
        this.totalSize = totalSize;
        this.appName = appName;
        this.url = url;
        this.adId = adId;
    }


    @Generated(hash = 2040406903)
    public DownloadBean() {
    }


    protected DownloadBean(Parcel in) {
        pkgName = in.readString();
        totalSize = in.readLong();
        appName = in.readString();
        url = in.readString();
        adId = in.readInt();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel in) {
            return new DownloadBean(in);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };

    public static DownloadBean convert(BaseInfo info){
        DownloadBean bean = new DownloadBean();
        bean.setId(info.getId());
        bean.setUrl(info.getUrl());
        bean.setPkgName(info.getPkgName());
        bean.setAppName(info.getName());
        bean.setAdId((int)info.getId());
        return bean;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getPkgName() {
        return this.pkgName;
    }


    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }


    public long getTotalSize() {
        return this.totalSize;
    }


    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }


    public String getAppName() {
        return this.appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getUrl() {
        return this.url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public int getAdId() {
        return this.adId;
    }


    public void setAdId(int adId) {
        this.adId = adId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pkgName);
        dest.writeLong(totalSize);
        dest.writeString(appName);
        dest.writeString(url);
        dest.writeInt(adId);
    }
}
