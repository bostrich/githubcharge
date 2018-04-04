package com.hodanet.charge.utils;


import com.hodanet.charge.config.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *
 *
 */
public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();

    private static final String getSurfingNavigation =  "http://stwifi.playbobo.com/doc/static/netcontent.html";//获取上网导航
//    private static final String getSurfingNavigation =  "http://60.191.52.101:43001/advplat-server/doc/static/netcontent.html";
    private static final String getNewsRecommend = "http://stwifi.playbobo.com/doc/static/news_adv.html";//上网新闻推广
    private static final String getDynaticNewsRecommend = "http://cdn-stwifi.playbobo.com/doc/news_adv.htm";//获取动态新闻推广
    private static final String getFindIconInfos =  "http://stwifi.playbobo.com/doc/static/secondary_find_list.html";//获取发现页热点的顶部Icon信息
    private static final String getVideo = "http://stwifi.playbobo.com/doc/static/get_video_list.html";//视频静态接口
    private static final String getDynaticVideo = "http://stwifi.playbobo.com/doc/get_video_list.htm";//视频动态接口
    private static final String getNewJokeInfo = "http://stwifi.playbobo.com/doc/static/get_joke_list_new.html"; //获取新版笑话接口
    private static final String getNewJokeDynaticInfo = "http://stwifi.playbobo.com/doc/get_joke_list_new.htm"; //获取新版笑话动态接口
    private static final String getBeautifulGirl = "http://stwifi.playbobo.com/doc/static/get_beauty_list.html";//美女静态接口
    private static final String reportFeedback = "http://stwifi.playbobo.com/doc/feedback.htm"; //意见反馈接口
    private static final String getUserFeedBackInfo = "http://stwifi.playbobo.com/doc/feedback_list.htm"; //获取用户反馈的留言信息
    private static final String getSpecialAd = "http://stwifi.playbobo.com/doc/static/special_adv.html";//获取特型推荐广告
    private static final String getBannerAd = "http://stwifi.playbobo.com/doc/static/banner_adv.html";//发现页banner广告位
//    private static final String getBannerAd = "http://60.191.52.101:43001/advplat-server/doc/static/banner_adv.html";//发现页banner广告位
    private static final String getHotRecommend = "http://stwifi.playbobo.com/doc/static/hotRecommend_adv2.html";//为您推荐
    private static final String getWelcomeAd = "http://stwifi.playbobo.com/doc/static/welcome_adv.html";//开屏广告
    private static final String getConfigInfo = "http://cdn-stwifi.playbobo.com/doc/static/get_busi_config.html";//配置参数
    private static final String getWifiConnectedNews = "http://stwifi.playbobo.com/doc/static/pic_news_wn.html";//测速成功新闻
    private static final String getOptimizeAdvUrl = "http://stwifi.playbobo.com/doc/static/optimize_adv.html";//测速成功新闻中广告
    private static final String getWifiConnectAd = "http://stwifi.playbobo.com/doc/static/connection_adv.html";//外部信息流新闻中广告
    private static final String getWifiUploadUrl = "http://cdn-stwifi.playbobo.com/doc/report_wifi_info.htm";//wifi上传
    private static final String getSpeedTestUrl = "http://res.ipingke.com/adsw/speed.html";//测速链接地址
    private static final String getWallPaper = "http://res.ipingke.com/wallpaper/wallpaper.html";//壁纸
    private static final String getAppConfig = "http://cdn-stwifi.playbobo.com/doc/static/get_busi_config.html";//应用配置信息
    private static String getWifiInfos = "http://stwifi.playbobo.com/doc/get_wifi_infos.htm";//获取wifi信息
    private static String getOuterNewsInfo = "http://cdn-res.playbobo.com/upload/sy/20170702/10/news.json";//获取信息流新闻
    private static String getInsideInfo = "http://stwifi.playbobo.com/doc/static/";//获取信息流新闻
//    private static String getInsideInfo = "http://60.191.52.101:43001/advplat-server/doc/static/";//获取信息流新闻
    private static final String getPlatformInfo = "http://stwifi.playbobo.com/doc/static/wall_adv.html";//积分墙信息
    public static final String getCompanionNews131= "http://newscdn.wlanbanlv.com/webapi/external/lists?from=zengqiangqi&channelId=131&num=20&page=";
    public static final String getCompanionNews170 = "http://newscdn.wlanbanlv.com/webapi/external/lists?from=zengqiangqi&channelId=170&num=20&page=";
    public static final String getKaijiaNews = "http://new.yigouu.com/api/171228/76_wap.php?perNumber=20&page=";
    public static final String getTTNews = "https://www.ttdailynews.com/api/list.htm?cid=571122&category=1&page=";

    private static final String getAdvReport = "http://cdn-stwifi.playbobo.com/doc/adv_report.htm";//其它广告的展示和点击流水统计
//    private static final String getAdvReport = "http://60.191.52.101:43001/advplat-server/doc/adv_report.htm";//其它广告的展示和点击流水统计
    private static final String getHdAppReport = "http://cdn-stwifi.playbobo.com/doc/hdadvreport.htm";//自有积分墙激活下载统计
    private static final String getChannel = "http://res.ipingke.com/adsw/charge.html";

    /**
     * 发送HTTP POST请求，获取服务器返回的接口数据（带参数）
     *
     * @param url
     * @return
     */
    public static String getResponse(String url) throws IOException {
        LogUtil.i(TAG, "访问接口：" + url + "\n");
        // 创建HTTP连接
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET"); // 使用POST请求，默认GET
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        // 进行连接，提交参数，并获取返回的数据
        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream(); // 获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                result += line + "\n";
            }
            if (bufferReader != null) {
                bufferReader.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (is != null) {
                is.close();
            }
            return result;
        } else {
            return "";
        }
    }

    /**
     * 发送HTTP get请求，获取服务器返回的接口数据（带参数）
     *
     * @param url
     * @param param
     * @return
     */

    public static String getResult(String url, String param, int type) throws IOException {
        LogUtil.i(TAG, "访问接口：" + url + "\n" + param);
        // 创建HTTP连接
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//            conn.setDoInput(true); // 允许输入流，即允许下载，默认为true
//            conn.setDoOutput(true); // Post请求需要允许输出流，即允许上传，默认为false
//            conn.setUseCaches(false); //  Post请求不能使用缓存
        // conn.setRequestMethod("GET"); // 使用POST请求，默认GET
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        // 设置请求参数
        //     byte[] data = param.getBytes("UTF-8");
        //      OutputStream os = conn.getOutputStream();
        //       os.write(data);
        //     os.flush();
        //     os.close();
        // 进行连接，提交参数，并获取返回的数据
        String result = "";

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream(); // 获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                result += line + "\n";
            }
            if (bufferReader != null) {
                bufferReader.close();
            }
            if (bufferReader != null) {
                isr.close();
            }
            if (bufferReader != null) {
                is.close();
            }
            return result;
        } else {
            LogUtil.i(TAG, "ResponseCode:" + conn.getResponseCode());
        }
        return "";
    }

    /**
     * 发送HTTP POST请求，获取服务器返回的接口数据（带参数）
     *
     * @param url
     * @param param
     * @return
     */
    public static String getResult(String url, String param) throws IOException {
        LogUtil.i(TAG, "访问接口：" + url + "\n" + param);
        // 创建HTTP连接
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoInput(true); // 允许输入流，即允许下载，默认为true
        conn.setDoOutput(true); // Post请求需要允许输出流，即允许上传，默认为false
        conn.setUseCaches(false); //  Post请求不能使用缓存
        conn.setRequestMethod("POST"); // 使用POST请求，默认GET
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        // 设置请求参数
        byte[] data = param.getBytes("UTF-8");
        OutputStream os = conn.getOutputStream();
        os.write(data);
        os.flush();
        os.close();
        // 进行连接，提交参数，并获取返回的数据
        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream(); // 获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                result += line + "\n";
            }
            if (bufferReader != null) {
                bufferReader.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (is != null) {
                is.close();
            }
            return result;
        } else {
            LogUtil.i(TAG, "ResponseCode:" + conn.getResponseCode());
        }
        return "";
    }


    /**
     * 获取上网导航
     *
     * @return
     */
    public static String requestSurfingNavigation() throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("imei", AppConfig.IMEI);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getResponse(getSurfingNavigation);
    }

    /**
     * 获得上网新闻推广
     *
     * @return
     */
    public static String requestNewsRecommend() throws IOException {
        return getResult(getNewsRecommend, "", 0);
    }

    public static String requestDynamicNewsRecommend() throws IOException {
        return getResult(getDynaticNewsRecommend, "",0);
    }

    /**
     * 获取发现页热点Icon信息
     *
     * @return
     */
    public static String requestSurfingIconInfos() throws IOException {
        return getResponse(getFindIconInfos);
    }

    /**
     * 获取视频信息
     *
     * @return
     */
    public static String requestVideoInfo() throws IOException {
        return getResult(getVideo, "", 0);
    }

    /**
     * 获取动态视频信息
     *
     * @return
     */
    public static String requestVideoDynaticInfo() throws IOException {
        return getResult(getDynaticVideo, "", 0);
    }

    /**
     * 获取新版段子信息
     *
     * @return
     */
    public static String requestNewJokeInfo() throws IOException {
        return getResponse(getNewJokeInfo);
    }

    /**
     * 获取新版段子信息
     *
     * @return
     */
    public static String requestNewJokeDynaticInfo() throws IOException {
        return getResult(getNewJokeDynaticInfo, "", 0);
    }

    /**
     * 获取美女信息
     *
     * @return
     */
    public static String requestBeautifulGirlInfo() throws IOException {
        return getResult(getBeautifulGirl, "", 0);
    }

    /**
     * 特型推荐广告
     *
     * @return
     */
    public static String requestSpecialAd() throws IOException {
        return getResult(getSpecialAd, "", 0);
    }






    /**
     * 得到发现广告banner
     *
     * @return
     */
    public static String requestBannerAd() throws IOException {
        String res = getResult(getBannerAd, "", 0);
        return res;
    }

    /**
     * 获得为您推荐
     *
     * @return
     */
    public static String requestHotRecommend() throws IOException {
        return getResult( getHotRecommend, "", 0);
    }

    /**
     * 开屏广告接口
     *
     * @return
     */
    public static String requestWelcomeAd() throws IOException {
        return getResult(getWelcomeAd, "", 0);
    }

    /**
     * 获取配置参数
     *
     * @return
     */
    public static String requestConfigInfo() throws IOException {
        return getResult(getConfigInfo, "", 0);
    }



    /**
     *测速成功新闻接口
     * @return
     * @throws IOException
     */
    public static String requestWifiConnectedNews() throws IOException {
        return getResult(getWifiConnectedNews, "", 0);
    }

    public static String requestOptimizeAdv() throws IOException {
        return getResult(getOptimizeAdvUrl, "", 0);
    }

    /**
     * 连接管理广告
     *
     * @return
     */
    public static String requestWifiConnectAd() throws IOException {
        return getResult(getWifiConnectAd, "", 0);
    }

    /**
     *获取网络测速链接
     * @return
     */
    public static String requestSpeedTestUrl() throws IOException {
        return getResult(getSpeedTestUrl, "", 0);
    }

    /**
     *获取壁纸接口
     * @return
     */
    public static String requestWallPaper(String url) throws IOException {
        return getResult(url, "", 0);
    }

    /**
     *获取配置信息
     * @return
     */
    public static String requestAppConfig() throws IOException {
        return getResult(getAppConfig, "", 0);
    }

    /**
     * 获取外部信息流中的新闻
     * @return
     * @throws IOException
     */
    public static String requestOuterNewsInfo() throws IOException {
        return getResult(getOuterNewsInfo, "", 0);
    }


    public static String requestCompanionNews(String url) throws IOException {
        return getResponse(url);
    }

    public static String requestChannelInfo() throws IOException {
        return getResponse(getChannel);
    }




    /**
     * 广告信息流水
     *
     * @param advId
     * @param opType
     * @param advType
     */
    public static String requestAdvReport(String advId, int opType, int advType) throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("imei", AppConfig.IMEI);
            json.put("advId", advId);
            json.put("opType", opType);
            json.put("advType", advType);
            json.put("channel", AppConfig.CHANNEL);//增加渠道字段
            json.put("versionCode", AppConfig.VERSION_CODE + "");
            json.put("userType", AppConfig.USER_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getResult(getAdvReport, json.toString());
    }

    /**
     * 用于接口上报添加位置字段，用于判断advId==0时错误发生的位置
     *
     * @param advId       广告id
     * @param opType      操作类型
     * @param advType     广告类型
     * @param advPosition 广告位置
     * @return
     */
    public static String requestAdvReport(String advId, int opType, int advType, int advPosition) throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("imei", AppConfig.IMEI);
            json.put("advId", advId);
            json.put("opType", opType);
            json.put("advType", advType);
            json.put("advPosition", advPosition);
            json.put("channel", AppConfig.CHANNEL);//增加渠道字段
            json.put("versionCode", AppConfig.VERSION_CODE + "");
            json.put("userType", AppConfig.USER_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getResult(getAdvReport, json.toString());

    }

    /**
     * 自有积分应用操作上报
     *
     * @param pkgName
     * @param opType
     * @param advType
     * @return
     */
    public static String requestWallAppReport(long id, String pkgName, int opType, int advType, int advPosition) throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("imei", AppConfig.IMEI);
            json.put("pkgName", pkgName);
            json.put("opType", opType);
            json.put("advType", advType);
            long time = System.currentTimeMillis();
            json.put("timeStamp", time);
            json.put("channel", AppConfig.CHANNEL);
            json.put("versionCode", AppConfig.VERSION_CODE + "");
            json.put("token", MD5.md5(AppConfig.IMEI + time + "5f27dde10bfa8adadfd122521b0a01ef"));
            json.put("userType", AppConfig.USER_TYPE);
            json.put("advPosition", advPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getResult(getHdAppReport, json.toString());
    }

    /**
     * wifi upload
     *
     * @param mac
     * @param pwd
     * @param ssid
     * @param lo
     * @param la
     * @param source 21:快速猜， 22：本地密码库破解， 23：root手机密码， 24：众联密码库
     * @return
     */
    public static String requestWifiUploadUrl(String mac, String pwd, String ssid, String lo, String la, String source) throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("mac", mac);
            json.put("password", pwd);
            json.put("ssid", ssid);
            json.put("longitude", lo);
            json.put("latitude", la);
            json.put("source", source);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getResult(getWifiUploadUrl, json.toString());
    }



    /**
     * 检查获取的数据是否正确
     *
     * @param result
     * @return
     */
    public static boolean checkResult(String result) {
        if (result == null || result.equals("")) {
            return false;
        }
        try {
            JSONObject json = new JSONObject(result);
            if (json.optInt("RC") == 1 || json.optInt("rc") == 1) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
