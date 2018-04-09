package com.hodanet.charge.info;

/**
 *
 */

public class Constants {

    public static final int REPORT_AD_TYPE_INTERGRAL_WALL = 1;
    public static final int REPORT_AD_TYPE_SPECIAL = 7;
    public static final int REPORT_AD_TYPE_RECOMMEND = 8;
    public static final int REPORT_AD_TYPE_BANNER = 9;
    public static final int REPORT_AD_TYPE_RING = 12;


    public static final String UMENG_ID_INTERGRAL_WALL = "intergral_wall";
    public static final String UMENG_ID_RECOMMEND = "recommend";
    public static final String UMENG_ID_RING = "ring";
    public static final String UMENG_ID_FLOAT = "float";
    public static final String UMENG_ID_SPECIAL = "special";
    public static final String UMENG_ID_DAILY = "daily";
    public static final String UMENG_ID_BANNER = "banner";


    public static final String UMENG_ID_INTERGRAL_WALL_LOCATION_CHECK_PWD = "check_pwd-";//查看密码页面
    public static final int UMENG_ID_INTERGRAL_WALL_POSITION_CHECK_PWD = 11;


    public static final int UMENG_ID_SLIDE_MEMU = 121;
    public static final int UMENG_ID_CHARGE = 91;
    public static final int UMENG_ID_HOT = 92;


    public static final String UMENG_ID_RECOMMEND_LOCATION_SLIDE_MENU = "slide_menu-";//侧滑菜单
    public static final String UMENG_ID_RECOMMEND_LOCATION_STRONG_CRACK = "strong_crack-";//强力破解
    public static final String UMENG_ID_RECOMMEND_LOCATION_COMMON_SECRET = "common_secret-";//常见密码
    public static final String UMENG_ID_RECOMMEND_LOCATION_SPEED_TEST_RESULT = "speed_test_result-";//测速结果页面
    public static final int UMENG_ID_RECOMMEND_POSITION_SLIDE_MENU = 801;
    public static final int UMENG_ID_RECOMMEND_POSITION_STRONG_CRACK = 802;
    public static final int UMENG_ID_RECOMMEND_POSITION_COMMON_SECRET = 803;
    public static final int UMENG_ID_RECOMMEND_POSITION_SPEED_TEST_RESULT = 804;

    public static final String UMENG_ID_FLOAT_LOCATION_CHARGE = "charge-";//充电
    public static final String UMENG_ID_FLOAT_LOCATION_HOT= "hot-";//热点


    public static final int INFO_TYPE_WEB = 1;
    public static final int INFO_TYPE_APP = 2;

    public static final int REPORT_TYPE_EXTERNAL_SHOW = 0;
    public static final int REPORT_TYPE_CONTENT_SHOW = 1;
    public static final int REPORT_TYPE_CLICK = 2;
    public static final int REPORT_TYPE_DOWNLOAD = 3;
    public static final int REPORT_TYPE_INSTALLED = 4;
    public static final int REPORT_TYPE_REAL_ACTION = 5;


    //新闻来源：
    public static final int NEWS_SOURCE_SELF_AD = 0;
    public static final int NEWS_SOURCE_SELF_ZAKER = 1;
    public static final int NEWS_SOURCE_SELF_EAST = 2;
    public static final int NEWS_SOURCE_SELF_HAPPY_LOOK = 3;
    public static final int NEWS_SOURCE_SELF_KAIJIA = 4;
    public static final int NEWS_SOURCE_SELF_SYEZON = 5;
    public static final int NEWS_SOURCE_COMPANION = 6;
    public static final int NEWS_SOURCE_KAIJIA = 7;
    public static final int NEWS_SOURCE_TT = 8;

    //新闻接口访问来源
    public static final int NEWS_GET_SOURCE_COMPANION = 3;
    public static final int NEWS_GET_SOURCE_KAIJIA = 4;
    public static final int NEWS_GET_SOURCE_TT = 5;

    public static final int NEWS_TYPE_SELF_AD = 0;

    public static final int ITEM_SHOW_TYPE_SINGLE_IMG = 1;
    public static final int ITEM_SHOW_TYPE_SINGLE_BIG_IMG = 2;
    public static final int ITEM_SHOW_TYPE_THREE_IMG = 3;
    public static final int ITEM_SHOW_TYPE_SIMPLE_TEXT = 4;

    public enum Event{
        OUT_SHOW("out_show", REPORT_TYPE_EXTERNAL_SHOW),
        CLICK("click", REPORT_TYPE_CLICK),
        CONTENT_SHOW("content_show", REPORT_TYPE_CONTENT_SHOW),
        DOWNLOAD("download", REPORT_TYPE_DOWNLOAD),
        INSTALLED("installed", REPORT_TYPE_INSTALLED),
        ACTIVED("actived", REPORT_TYPE_REAL_ACTION);


        private String actionName;
        private int actionType;

        private Event(String actionName, int actionType){
            this.actionName = actionName;
            this.actionType = actionType;
        }

        public String getActionName() {
            return actionName;
        }

        public void setActionName(String actionName) {
            this.actionName = actionName;
        }

        public int getActionType() {
            return actionType;
        }

        public void setActionType(int actionType) {
            this.actionType = actionType;
        }
    }
}
