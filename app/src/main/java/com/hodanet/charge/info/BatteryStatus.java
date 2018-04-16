package com.hodanet.charge.info;

import android.content.Context;
import android.os.BatteryManager;

import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.utils.ToastUtil;

import org.json.JSONObject;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

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
    private int connectType;
    private long chargeTime;
    private int randomTime;
    private String voltage;
    private String temp;
    private int health;


    public BatteryStatus() {
        randomTime = (int) (Math.random() * 30);
    }

    public int getBatteryAccelerateTime(){
        return (int) (0.2 * getChargeRemainTime());
    }

    public int getBatteryRemainTime(Context context){
        //优化后2小时内进入都显示以优化
        int saveTime = 0;
        try {
            String result = SpUtil.getStringData(context, SpUtil.OPTIMIZE_DATA, "");
            JSONObject obj = new JSONObject(result);
            long time = obj.optLong("time");

            if(System.currentTimeMillis() - time < 1000 * 60 * 60 *2){
                saveTime = (int) (obj.optInt("saveTime") * (1 - (System.currentTimeMillis() - time) / (1000.0 *60 * 60 * 2)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) ((60 * 15 + 9 * randomTime) * powerPercent / 100.0) + saveTime ;
    }

    public int getChargeRemainTime(){
        int time = 0;
        switch(getConnectType()){
            case BatteryManager.BATTERY_PLUGGED_AC:
                time = (int) ((120 + randomTime) * (100 - powerPercent) / 100.0);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                time = (int) ((240 + randomTime) * (100 - powerPercent) / 100.0);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                time = (int) ((180 + randomTime) * (100 - powerPercent) / 100.0);
                break;
            default:
                time = (int) ((180 + randomTime) * (100 - powerPercent) / 100.0);
                break;
        }

        return time;
    }


    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }


    public long getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(long chargeTime) {
        this.chargeTime = chargeTime;
    }

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

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
