package com.hodanet.charge;

import android.support.multidex.MultiDexApplication;

import com.umeng.analytics.MobclickAgent;

/**
 *
 */

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType. E_UM_NORMAL);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setCatchUncaughtExceptions(true);
    }
}
