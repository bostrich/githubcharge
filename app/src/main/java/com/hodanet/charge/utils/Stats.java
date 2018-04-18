package com.hodanet.charge.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 统计
 */
public class Stats {

    public static final int REPORT_TYPE_EXTERNAL_SHOW = 0;//网页类型广告的外部展示
    public static final int REPORT_TYPE_SHOW = 1;
    public static final int REPORT_TYPE_CLICK = 2;
    public static final int REPORT_TYPE_DOWNLOAD = 3;
    public static final int REPORT_TYPE_ACTION = 4;
    public static final int REPORT_TYPE_REAL_ACTION = 5;
    public static final int ADV_TYPE_APP = 1;
    public static final int ADV_TYPE_WELCOME = 2;
    public static final int ADV_TYPE_CONNECT = 3;
    public static final int ADV_TYPE_INSIDE = 4;
    //public static final int ADV_TYPE_RECOMMEND_APP = 5;
    public static final int ADV_TYPE_RECOMMEND = 6;
    public static final int ADV_TYPE_SPECIAL = 7;
    public static final int ADV_TYPE_HOTRECOMMEND = 8;
    public static final int ADV_TYPE_BANNER = 9;
    public static final int ADV_TYPE_FLOATING = 10;
    public static final int ADV_TYPE_LOTTERY = 11;
    public static final int ADV_TYPE_EXIT_AD = 12;
    public static final int ADV_TYPE_INFO_EXIT_AD = 13;
    public static final int ADV_TYPE_NOVICE_PACKS = 14;
    public static final int ADV_TYPE_GOLD_ADV = 15;
    public static final int ADV_POSITION_WEB_EXIT = 71;
    public static final int ADV_POSITION_WEB_WIFI_SAFE = 81;
    public static final int ADV_POSITION_APP_WIFI_SAFE = 82;
    public static final int ADV_POSITION_WEB_WIFI_CLEAN_RESULT = 400;
    public static final int ADV_POSITION_EXIT_AD = 91;
    //添加位置字段用于判断广告位置
    public static final int SPEED_FLOATING = 101;//speed_fragment 优化页面
    public static final int SPEED_DAILY_RECOMMEND = 102;//每日推荐
    public static final int SPEED_SPECIAL_RECOMMAND = 103;//特型推荐
    public static final int SURFING_NEWS_BANNER = 111;//发现页面的banner
    public static final int LOGO_ADV = 201;//启动APP闪屏广告
    public static final int WIFICONNECTEDHOTACTIVITY_BANNER = 211;//信息流里面的banner
    public static final int WIFICONNECTEDHOTACTIVITY_NEWS = 212;//信息流里的新闻
    public static final int WIFICONNECTEDHOTACTIVITY_BOOKS = 213;//信息流里面的小说
    public static final int WIFICONNECTEDHOTACTIVITY_APPS = 214;//信息流里面的精品应用

    public static final int SURFING_NEWS_TUIGUANG = 215;//发现页里的推广新闻
    public static final int INSIDE_AD_ACTIVITY = 221;//优化完成页面退出后弹出的广告页
    public static final int LOTTERY_TURNNABLE = 1;
    public static final int LOTTERY_EGG = 2;
    public static final int LOTTERY_TIGER = 3;
    public static final int LOTTERY_SCRATCH = 4;
    public static final int LOTTERY_TURNOVER_CARD = 5;
    private static final String TAG = Stats.class.getSimpleName();

    public static final int APP_STATE_CLICK = 101;
    public static final int APP_STATE_DOWNLOADED = 102;
    public static final int APP_STATE_INSTALLED = 103;
    public static final int APP_STATE_ACTIVED = 104;

    /**
     * 统计计数事件
     *
     * @param key 统计标签
     */
    public static void event(Context context, String key) {
        MobclickAgent.onEvent(context, key);
        LogUtil.i(TAG, "key:" + key);
    }



    /**
     * 统计计数事件
     *
     * @param key 统计标签
     * @param id  附带参数
     */
    public static void event(Context context, String key, String id) {
        MobclickAgent.onEvent(context, key,id);
        LogUtil.i(TAG, "key:" + key + " id:" + id);
    }

    /**
     * 统计计数事件
     *
     * @param key  统计标签
     * @param lab1 附带参数1
     * @param lab2 附带参数2
     */
    public static void event(Context context, String key, String lab1, String lab2) {
        MobclickAgent.onEvent(context, key);
        LogUtil.i(TAG, "key:" + key + " lab1:" + lab1 + " lab2:" + lab2);
    }

    /**
     * 统计计数事件
     *
     * @param eventId  统计标签
     * @param eventType 附带参数1
     * @param eventValue 附带参数2
     */
    public static void eventToYoumeng(Context context, String eventId, String eventType, String eventValue) {
        HashMap<String, String> map = new HashMap<>();
        map.put(eventType, eventValue);
        MobclickAgent.onEvent(context, eventId, map);
        LogUtil.i(TAG, "事件ID:" + eventId + " 事件类型:" + eventType + " 事件值:" + eventValue);
    }


    /**
     * 带map参数的统计事件
     *
     * @param context
     * @param key     统计标签
     * @param idkey   map key
     * @param idvalue map value
     */
    public static void eventWithMap(Context context, String key, String idkey, String idvalue) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(idkey, idvalue);
        MobclickAgent.onEvent(context, key, map);
        LogUtil.i(TAG, "事件ID:" + key + " 事件类型:" + idkey + " 事件值:" + idvalue);
    }




    /**
     * 上报广告流水
     *
     * @param advId
     * @param opType
     * @param advType
     */
    public static void reportAdv(final int advId, final int opType, final int advType) {
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = HttpUtils.requestAdvReport(String.valueOf(advId), opType, advType);
                    LogUtil.i("advReport", "advId:" + advId + ",opType:" + opType + ",advType:" + advType + ",result" + result);
                } catch (Exception e) {

                }
            }
        });

    }

    /**
     * 上报广告流水
     *
     * @param advId
     * @param opType
     * @param advType
     * @param advPosition 以前版本中使用，用于判断哪个类型的广告上报错误，没有什么用
     */
    public static void reportAdv(final long advId, final int opType, final int advType, final int advPosition) {
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = HttpUtils.requestAdvReport(String.valueOf(advId), opType, advType, advPosition);
                    LogUtil.i("advReport", "advId:" + advId + ",opType:" + opType + ",advType:" + advType + ",advPosition:" + advPosition + ",result" + result);
                } catch (Exception e) {

                }
            }
        });

    }


    /**
     * 自有积分应用操作上报
     *
     * @param pkgName
     * @param opType
     * @param advType
     */
    public static void reportWallAppStats(final long id, final String pkgName, final int opType, final int advType, final int advPosition) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String result = HttpUtils.requestWallAppReport(id, pkgName, opType, advType, advPosition);
                    LogUtil.i("appAdvReport", "pkgName:" + pkgName + ",opType:" + opType + ",advType:" + advType + "advPosition:" + advPosition + ",result:" + result);
                } catch (Exception e) {

                }
            }
        }.start();
    }


    public static void eventReportAdv(Context context, String pkgName, int actionType, int reportType) {

    }
}
