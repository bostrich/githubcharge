package com.hodanet.charge.info.pic;

/**
 * Created by June on 2017/6/23.
 */
public class WallpaperClassifyBean {
    /**
     * id : scenery
     * name : 风景
     * dsc : 世界这么大，我要去看看！
     * cover : /wallpaper/scenery/vsf.jpg
     */

    private String id;
    private String name;
    private String dsc;
    private int newVer;
    private String cover;

    public WallpaperClassifyBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }

    public int getNewVer() {
        return newVer;
    }

    public void setNewVer(int newVer) {
        this.newVer = newVer;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "WallpaperClassifyBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dsc='" + dsc + '\'' +
                ", newVer=" + newVer +
                ", cover='" + cover + '\'' +
                '}';
    }
}
