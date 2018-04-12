package com.hodanet.charge.event;

/**
 *
 */

public class BatteryConnectEvent {
    private boolean isConnected;
    private int conncectType;


    public BatteryConnectEvent(boolean isConnected, int conncectType) {
        this.isConnected = isConnected;
        this.conncectType = conncectType;
    }

    public int getConncectType() {
        return conncectType;
    }

    public void setConncectType(int conncectType) {
        this.conncectType = conncectType;
    }

    public BatteryConnectEvent() {
    }

    public BatteryConnectEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
