package com.hodanet.charge.event;

/**
 *
 */

public class BatteryConnectEvent {
    private boolean isConnected;

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
