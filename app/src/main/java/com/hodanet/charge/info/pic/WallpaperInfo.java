package com.hodanet.charge.info.pic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2017/5/12.
 */

public class WallpaperInfo implements Parcelable {

    /**
     * id : 10001
     * vip : 1
     * name : 邻家小妹
     * info :
     * favorites : 49893
     * pic : /wallpaper/beauty/1hh.jpg
     */

    private int id;
    private int vip;
    private String name;
    private String info;
    private int favorites;
    private String pic;

    @Override
    public String toString() {
        return "WallpaperInfo{" +
                "id=" + id +
                ", vip=" + vip +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", favorites=" + favorites +
                ", pic='" + pic + '\'' +
                '}';
    }

    public WallpaperInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.vip);
        dest.writeString(this.name);
        dest.writeString(this.info);
        dest.writeInt(this.favorites);
        dest.writeString(this.pic);
    }

    protected WallpaperInfo(Parcel in) {
        this.id = in.readInt();
        this.vip = in.readInt();
        this.name = in.readString();
        this.info = in.readString();
        this.favorites = in.readInt();
        this.pic = in.readString();
    }

    public static final Creator<WallpaperInfo> CREATOR = new Creator<WallpaperInfo>() {
        @Override
        public WallpaperInfo createFromParcel(Parcel source) {
            return new WallpaperInfo(source);
        }

        @Override
        public WallpaperInfo[] newArray(int size) {
            return new WallpaperInfo[size];
        }
    };
}
