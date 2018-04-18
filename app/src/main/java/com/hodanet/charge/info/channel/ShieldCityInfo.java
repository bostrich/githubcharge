package com.hodanet.charge.info.channel;

/**
 *
 */

public class ShieldCityInfo {
    private String cityId;
    private String cityName;
    private String channel;

    public ShieldCityInfo(){}

    public ShieldCityInfo(String cityId, String cityName, String channel) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.channel = channel;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
