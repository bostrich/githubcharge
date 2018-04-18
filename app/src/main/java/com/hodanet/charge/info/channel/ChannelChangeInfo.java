package com.hodanet.charge.info.channel;

/**
 *
 */

public class ChannelChangeInfo {
    private String needChangeChannelName;
    private String changedChannelName;

    public ChannelChangeInfo(){}

    public ChannelChangeInfo(String needChangeChannelName, String changedChannelName) {
        this.needChangeChannelName = needChangeChannelName;
        this.changedChannelName = changedChannelName;
    }

    public String getNeedChangeChannelName() {
        return needChangeChannelName;
    }

    public void setNeedChangeChannelName(String needChangeChannelName) {
        this.needChangeChannelName = needChangeChannelName;
    }

    public String getChangedChannelName() {
        return changedChannelName;
    }

    public void setChangedChannelName(String changedChannelName) {
        this.changedChannelName = changedChannelName;
    }
}
