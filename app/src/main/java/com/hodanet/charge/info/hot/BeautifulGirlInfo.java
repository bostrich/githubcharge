package com.hodanet.charge.info.hot;

/**
 *
 */
public class BeautifulGirlInfo {
    private String name;
    private String pic;

    public BeautifulGirlInfo() {
    }

    public BeautifulGirlInfo(String pic, String name) {
        this.pic = pic;
        this.name = name;
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
}
