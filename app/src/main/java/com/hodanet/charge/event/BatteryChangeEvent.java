package com.hodanet.charge.event;

/**
 *
 */

public class BatteryChangeEvent {
    private String batteryVoltage;
    private String batteryTem;
    private int status;
    private String batteryPercent;
    private int batteryHealth;
    private int percent;

    public BatteryChangeEvent() {

    }

    public BatteryChangeEvent(String batteryVoltage, String batteryTem, int status, String batteryPercent, int batteryHealth) {
        this.batteryVoltage = batteryVoltage;
        this.batteryTem = batteryTem;
        this.status = status;
        this.batteryPercent = batteryPercent;
        this.batteryHealth = batteryHealth;
    }

    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getBatteryTem() {
        return batteryTem;
    }

    public void setBatteryTem(String batteryTem) {
        this.batteryTem = batteryTem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(String batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public int getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(int batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
