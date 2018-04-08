package com.hodanet.charge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;


import com.hodanet.charge.config.CustomInfo;

import java.util.List;


/**
 *
 */
public class CustomUtil {
    public static final String taobaoCityCode = "北京,110000,1;天津,120000,2;河北,130000,4;山西,140000,8;内蒙古,150000,16;辽宁,210000,32;吉林,220000,64;黑龙江,230000,128;上海,310000,256;江苏,320000,512;浙江,330000,1024;安徽,340000,2048;福建,350000,4096;江西,360000,8192;山东,370000,16384;河南,410000,32768;湖北,420000,65536;湖南,430000,131072;广东,440000,262144;广西,450000,524288;海南,460000,1048576;重庆,500000,2097152;四川,510000,4194304;贵州,520000,8388608;云南,530000,16777216;西藏,540000,33554432;陕西,610000,67108864;甘肃,620000,134217728;青海,630000,268435456;宁夏,640000,536870912;新疆,650000,1073741824;香港,HK_01,2147483648;台湾,TW_01,4294967296;澳门,MO_01,8589934592";
    //女性关键词：美颜，大姨妈，美发，美甲，母婴，美拍
    public static String[] femaleKeyWords = {"美颜", "大姨妈", "美发", "美甲", "母婴", "美拍", "喵呜"};
    public static String[][] interestCategory = {{"1", "社交通讯"}, {"2", "影音播放"}, {"4", "系统工具"}, {"8", "拍摄美化"}, {"16", "理财购物"}, {"32", "生活实用"}, {"64", "主题壁纸"}, {"128", "资讯阅读"}, {"256", "旅游出行"},
            {"512", "办公学习"}, {"1024", "休闲益智"}, {"2048", "动作射击"}, {"4096", "赛车竞速"}, {"8192", "网络游戏"}, {"16384", "棋牌桌游"}, {"32768", "经营养成"}, {"65536", "角色扮演"}};
    public static String[][] interestKeywords = {{"聊天", "社交", "婚恋", "通讯"}, {"音乐", "播放器", "电台", "视频", "铃声"}, {"优化", "安全", "浏览器", "输入法"}, {"相机", "照片", "摄像", "美化"}, {"网购", "彩票", "银行", "股票"}, {"天气", "运动", "母婴", "生活"},
            {"壁纸", "桌面", "主题"}, {"新闻", "漫画", "电子书", "笑话"}, {"旅行", "购票", "酒店", "导航"}, {"办公", "笔记", "考试", "驾考"}, {"消除", "休闲", "策略", "物理"}, {"跑酷", "格斗", "空战", "坦克"}, {"赛车", "摩托", "驾驶"},
            {"卡牌", "经营", "RPG", "动作"}, {"斗地主", "麻将", "棋类", "纸牌"}, {"战争", "经营", "装扮"}, {"解谜", "三国", "ARPG"}};

    public static void initCustom(Context context) {
        getGenderAndInterest(context);
        getNetWorkType(context);
        getPhoneBrand(context);
        getPhoneResolution(context);
        getOperator(context);
        getSdkVersion(context);
    }

    /**
     * 获得性别和兴趣
     *
     * @param context
     */
    private static void getGenderAndInterest(final Context context) {
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
                    int interest = 0;
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < packages.size(); i++) {
                        PackageInfo packageInfo = packages.get(i);
                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                            //性别关键词检查
                            for (int j = 0; j < femaleKeyWords.length; j++) {
                                if (appName.contains(femaleKeyWords[j])) {
                                    //标记为女性
                                    saveCustomGender(context, 2);
                                    break;
                                }
                            }
                            //兴趣关键词检测
                            for (int j = 0; j < interestCategory.length; j++) {
                                for (int k = 0; k < interestKeywords[j].length; k++) {
                                    if (appName.contains(interestKeywords[j][k])) {
                                        interest = interest | Integer.valueOf(interestCategory[j][0]);
                                        sb.append(interestCategory[j][0] + ",");
                                        break;
                                    }
                                }
                            }
                            saveCustomInterest(context, sb.toString());
                        }
                    }
                } catch (Exception e) {

                }
            }
        });

    }

    /**
     * 获得网络类型
     *
     * @param context
     */
    private static void getNetWorkType(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectMgr.getActiveNetworkInfo();
                if (info != null) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        saveCustomNetType(context, 8);
                    } else {
                        if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS || info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA || info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                            saveCustomNetType(context, 2);
                        } else {
                            saveCustomNetType(context, 1);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 获得手机品牌
     */
    private static void getPhoneBrand(Context context) {
        try {
            String brand = Build.BRAND.toLowerCase();
            if (brand.contains("meizu")) {
                saveCustomDevice(context, 32);
            } else if (brand.contains("huawei")) {
                saveCustomDevice(context, 8);
            } else if (brand.contains("mi") || brand.contains("hm") || brand.contains("红米")) {
                saveCustomDevice(context, 2);
            } else if (brand.contains("oppo")) {
                saveCustomDevice(context, 4);
            } else if (brand.contains("lenovo")) {
                saveCustomDevice(context, 16);
            } else if (brand.contains("vivo")) {
                saveCustomDevice(context, 64);
            } else if (brand.contains("360")) {
                saveCustomDevice(context, 256);
            } else {
                saveCustomDevice(context, 512);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 获得屏幕分辨率
     *
     * @param context
     */
    private static void getPhoneResolution(Context context) {
        try {
            // 获取屏幕信息
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int screen_width = dm.widthPixels;
            int screen_height = dm.heightPixels;
            if (screen_width == 1920 && screen_height == 1080) {
                saveCustomResolution(context, 1);
            } else if (screen_width == 1776 && screen_height == 1080) {
                saveCustomResolution(context, 2);
            } else if (screen_width == 1280 && screen_height == 800) {
                saveCustomResolution(context, 4);
            } else if (screen_width == 1280 && screen_height == 720) {
                saveCustomResolution(context, 8);
            } else if (screen_width == 960 && screen_height == 540) {
                saveCustomResolution(context, 16);
            } else if (screen_width == 854 && screen_height == 480) {
                saveCustomResolution(context, 32);
            } else {
                saveCustomResolution(context, 64);
            }
        } catch (Exception e) {

        }

    }

    /**
     * 获得运营商
     *
     * @param context
     */
    private static void getOperator(Context context) {
        try {
            if (context != null) {
                TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String operator = telManager.getSimOperator();
                if (operator != null) {
                    if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {//中国移动
                        saveCustomOperator(context, 1);
                    } else if (operator.equals("46001")) {//中国联通
                        saveCustomOperator(context, 2);
                    } else if (operator.equals("46003")) {//中国电信
                        saveCustomOperator(context, 4);
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private static int getSdkVersion(Context context) {
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK_INT);
            saveCustomOsVer(context, version);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    /**
     * 保存性别
     */
    public static synchronized void saveCustomGender(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("CUSTOM_GENDER", value).commit();
    }

    /**
     * 获取性别
     *
     * @param context
     * @return
     */
    public static int getCustomGender(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("CUSTOM_GENDER", 1);
    }

    /**
     * 保存兴趣
     */
    public static synchronized void saveCustomInterest(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("CUSTOM_INTEREST", value).commit();
    }

    /**
     * 获取兴趣
     *
     * @param context
     * @return
     */
    public static String getCustomInterest(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("CUSTOM_INTEREST", "");
    }

    /**
     * 保存网络类型
     */
    public static synchronized void saveCustomNetType(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("CUSTOM_NETTYPE", value).commit();
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static int getCustomNetType(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("CUSTOM_NETTYPE", 8);
    }

    /**
     * 保存设备品牌
     */
    public static synchronized void saveCustomDevice(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("CUSTOM_DEVICE", value).commit();
    }

    /**
     * 获取设备品牌
     *
     * @param context
     * @return
     */
    public static int getCustomDevice(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("CUSTOM_DEVICE", 0);
    }

    /**
     * 保存系统版本
     */
    public static synchronized void saveCustomOsVer(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("CUSTOM_OSVER", value).commit();
    }

    /**
     * 获取系统版本
     *
     * @param context
     * @return
     */
    public static int getCustomOsVer(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("CUSTOM_OSVER", 0);
    }

    /**
     * 保存设备分辨率
     */
    public static synchronized void saveCustomResolution(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("CUSTOM_RESOLUTION", value).commit();
    }

    /**
     * 获取设备分辨率
     *
     * @param context
     * @return
     */
    public static int getCustomResolution(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("CUSTOM_RESOLUTION", 0);
    }

    /**
     * 保存运营商
     */
    public static synchronized void saveCustomOperator(Context context, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("CUSTOM_OPERATOR", value).commit();
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return
     */
    public static int getCustomOperator(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt("CUSTOM_OPERATOR", 1);
    }

    /**
     * 保存地域
     */
    public static synchronized void saveCustomArea(Context context, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong("CUSTOM_AREA", value).commit();
    }

    /**
     * 获取地域
     *
     * @param context
     * @return
     */
    public static long getCustomArea(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong("CUSTOM_AREA", 0);
    }

    public static boolean checkCustom(Context context, CustomInfo customInfo) {
        //匹配性别
        if (getCustomGender(context) != customInfo.gender && customInfo.gender != 3) {
            return false;
        }
        //匹配兴趣
        if (customInfo.interest != 2147483647) {//2147483647为兴趣不限，直接过
            String interest = getCustomInterest(context);
            if (StringUtils.isNotBlank(interest)) {
                boolean hasInterest = false;
                String[] str = interest.split(",");
                for (int i = 0; i < str.length; i++) {
                    if (StringUtils.isNotBlank(str[i])) {
                        String ownInterBinary = Integer.toBinaryString(Integer.parseInt(str[i]));
                        String serverInterBinary = Integer.toBinaryString(customInfo.interest);
                        if (serverInterBinary.length() >= ownInterBinary.length()) {
                            if (ownInterBinary.charAt(0) == serverInterBinary.charAt(serverInterBinary.length() - ownInterBinary.length())) {
                                hasInterest = true;
                                break;
                            }
                        }
                    }
                }
                if (!hasInterest) {
                    return false;
                }
            } else {
                return false;
            }
        }
        //匹配网络类型
        boolean netTypeCheck = false;
        int netType = getCustomNetType(context);
        String ownNetTypeBinary = Integer.toBinaryString(netType);
        String serverNetTypeBinary = Integer.toBinaryString(customInfo.netType);
        if (serverNetTypeBinary.length() >= ownNetTypeBinary.length()) {
            if (ownNetTypeBinary.charAt(0) == serverNetTypeBinary.charAt(serverNetTypeBinary.length() - ownNetTypeBinary.length())) {
                netTypeCheck = true;
            }
        }
        if (!netTypeCheck) return false;

        //设备品牌匹配
        if (customInfo.device != 65535) {//65535:为不限品牌，直接过
            boolean deviceCheck = false;
            int device = getCustomDevice(context);
            String ownDeviceBinary = Integer.toBinaryString(device);
            String serverDeviceBinary = Integer.toBinaryString(customInfo.device);
            if (serverDeviceBinary.length() >= ownDeviceBinary.length()) {
                if (ownDeviceBinary.charAt(0) == serverDeviceBinary.charAt(serverDeviceBinary.length() - ownDeviceBinary.length())) {
                    deviceCheck = true;
                }
            }
            if (!deviceCheck) return false;
        }

        //系统版本匹配
        if (customInfo.osVer != 1) {//1:为不限版本，直接过
            int sysVer = getCustomOsVer(context);
            if (sysVer < customInfo.osVer) {
                return false;
            }
        }


        //手机分辨率匹配
        if (customInfo.resolution != 65535) {//65535:不限分辨率，直接过
            boolean resolutionCheck = false;
            int resolution = getCustomResolution(context);
            String ownResolutionBinary = Integer.toBinaryString(resolution);
            String serverResolutionBinary = Integer.toBinaryString(customInfo.resolution);
            if (serverResolutionBinary.length() >= ownResolutionBinary.length()) {
                if (ownResolutionBinary.charAt(0) == serverResolutionBinary.charAt(serverResolutionBinary.length() - ownResolutionBinary.length())) {
                    resolutionCheck = true;
                }
            }
            if (!resolutionCheck) return false;
        }


        //运营商匹配
        if (customInfo.operator != 127) {//127:不限运营商，直接过
            boolean operatorCheck = false;
            int operator = getCustomOperator(context);
            String ownOperatorBinary = Integer.toBinaryString(operator);
            String serverOperatorBinary = Integer.toBinaryString(customInfo.operator);
            if (serverOperatorBinary.length() >= ownOperatorBinary.length()) {
                if (ownOperatorBinary.charAt(0) == serverOperatorBinary.charAt(serverOperatorBinary.length() - ownOperatorBinary.length())) {
                    operatorCheck = true;
                }
            }
            if (!operatorCheck) return false;
        }

        if (customInfo.area != 17179869183L) {//17179869183:为不限地区，直接过
            // 地区匹配
            boolean areaCheck = false;
            long area = getCustomArea(context);
            String ownAreaBinary = Long.toBinaryString(area);
            String serverAreaBinary = Long.toBinaryString(customInfo.area);
            if (serverAreaBinary.length() >= ownAreaBinary.length()) {
                if (ownAreaBinary.charAt(0) == serverAreaBinary.charAt(serverAreaBinary.length() - ownAreaBinary.length())) {
                    areaCheck = true;
                }
            }
            if (!areaCheck) return false;
        }
        return true;
    }

}
