package com.hodanet.charge.event;

/**
 * Created by June on 2018/4/11.
 */

public class ShowSlideMenuRedDot {
    private boolean isShow;


    public ShowSlideMenuRedDot(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
