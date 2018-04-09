package com.hodanet.charge.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiConnect {

    // 定义加密方式
    private static final int LOCK_NO = 0;
    private static final int LOCK_WEP = 1;
    private static final int LOCK_WPA = 2;

    private WifiManager mWifiManager;

    public WifiConnect(WifiManager wifiManager) {
        mWifiManager = wifiManager;
    }

    /**
     * 判断加密方式
     *
     * @param capabilities
     * @return
     */
    public static int getLockType(String capabilities) {
        if (capabilities.contains("WEP")) {
            return 1;
        } else if (capabilities.contains("WPA")) {
            return 2;
        }
        return 0;
    }

    /**
     * 打开WIFI功能
     *
     * @return
     */
    private boolean OpenWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * 判断WIFI是否正常连接
     *
     * @return
     */
    @SuppressWarnings("static-access")
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 连接指定的WIFI热点
     *
     * @param SSID
     * @param Password
     * @param lock
     * @return
     */
    public boolean connect(String SSID, String Password, int lock) {
        if (!this.OpenWifi()) {
            return false;
        }

        if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            return false;
        }

        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, lock);

        if (wifiConfig == null) {
            return false;
        }

        WifiConfiguration tempConfig = isExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean bRet = mWifiManager.enableNetwork(netID, false);
        return bRet;
    }

    /**
     * 查看以前是否也配置过这个网络
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 创建一个WIFI配置
     *
     * @param SSID
     * @param Password
     * @param lock
     * @return
     */
    public WifiConfiguration createWifiInfo(String SSID, String Password,
                                            int lock) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (lock == LOCK_NO) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (lock == LOCK_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (lock == LOCK_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }
}
