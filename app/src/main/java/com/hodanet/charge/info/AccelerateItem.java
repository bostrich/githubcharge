package com.hodanet.charge.info;

/**
 *
 */

public class AccelerateItem {

    public static final int ITEM_WIFI = 0;
    public static final int ITEM_BLUE = 1;
    public static final int ITEM_BRIGHTNESS = 2;
    public static final int ITEM_OTHER = 3;

    private String dsc;
    private int item;

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }
}
