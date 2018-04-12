package com.hodanet.charge.info;

/**
 *
 */

public class BatteryStatus {
    public static final int BATTERY_NOCHARGE = 1;
    public static final int BATTERY_CHARGE_NOMAL = 2;
    public static final int BATTERY_CHARGE_ACCELERATE = 3;


    private int powerPercent;
    private int status;
    private boolean isCharging;
    private boolean isAccelerate;


    public int getPowerPercent() {
        return powerPercent;
    }

    public void setPowerPercent(int powerPercent) {
        this.powerPercent = powerPercent;
    }

    public boolean isAccelerate() {
        return isAccelerate;
    }

    public void setAccelerate(boolean accelerate) {
        isAccelerate = accelerate;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
        if(!charging) isAccelerate = false;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
