package com.hodanet.charge.info.hot;

/**
 * Created by June on 2016/8/10.
 */
public class VideoInfo {
    private String name;
    private String pic;
    private String url;

    public VideoInfo() {
    }

    public VideoInfo(String name, String pic, String url) {
        this.name = name;
        this.pic = pic;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
