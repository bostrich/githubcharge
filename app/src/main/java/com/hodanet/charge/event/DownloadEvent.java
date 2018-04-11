package com.hodanet.charge.event;

/**
 * Created by pillage on 2018/4/11.
 */

public class DownloadEvent {
    public static final int DOWNLOAD_START = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_STOP = 2;
    public static final int DOWNLOAD_FINISH = 3;
    public static final int DOWNLOAD_FAILED = 4;


    public DownloadEvent(int state, String pkgName) {
        this.state = state;
        this.pkgName = pkgName;
    }

    private int state;
    private String pkgName;
    private long currentSize;
    private long totalSize;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
