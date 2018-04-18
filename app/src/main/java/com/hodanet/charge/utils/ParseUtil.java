package com.hodanet.charge.utils;

import android.content.Context;

import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.ConsConfig;
import com.hodanet.charge.fragment.surfing.SurfingHotNewsFragment;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.channel.ChannelChangeInfo;
import com.hodanet.charge.info.channel.ChannelInfo;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.info.channel.ShieldCityInfo;
import com.hodanet.charge.info.hot.NewsInfo;
import com.hodanet.charge.info.hot.TabItemInfo;
import com.hodanet.charge.info.pic.WallpaperClassifyBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by June on 2018/4/9.
 */

public class ParseUtil {

    private static final String TAG = ParseUtil.class.getName();
    public static String rowkey = "";

    /**
     * @return 获取发现页导航内容
     * @throws JSONException
     */
    public static List<TabItemInfo> getSurfingNavigationInfo() throws JSONException, IOException {
        List<TabItemInfo> tabItemInfos = new ArrayList<>();
        String result = HttpUtils.requestSurfingNavigation();
        if (HttpUtils.checkResult(result)) {
            JSONObject json = new JSONObject(result);
            JSONObject data = json.optJSONObject("data");
            JSONArray array = data.optJSONArray("contents");
            if (array != null && array.length() > 0) {
                tabItemInfos.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    TabItemInfo info = new TabItemInfo();
                    info.setId(object.optInt("id"));
                    info.setInnerName(object.optString("innerName"));
                    info.setText(object.optString("name"));
                    info.setClickAction(object.optString("url"));
                    String position = object.optString("position");
                    if (StringUtils.isNotBlank(position)) {
                        String[] groupPosition = position.split("-");
                        if (groupPosition.length == 2) {
                            info.setPosition(Integer.parseInt(groupPosition[1]));
                            if (groupPosition[0].equals("3")) {
                                if (!ChannelConfig.SPLASH) {
                                    if (info.getInnerName().equals("news")) tabItemInfos.add(info);
                                } else {
                                    tabItemInfos.add(info);
                                }

                            }
                        }
                    }
                }
                Collections.sort(tabItemInfos);
            }
        }
        return tabItemInfos;
    }

    /**
     * @param isFirst  是否是第一次加载数据
     * @param isShowAd 是否关闭推广广告
     * @return 新闻列表集合
     * @throws JSONException
     */
    public static List<NewsInfo> getNewsRecommendList(boolean isFirst, boolean isShowAd, String newsSource) throws JSONException, IOException {
        List<NewsInfo> newsInfos = new ArrayList<>();
        if (newsSource.equals(ConsConfig.NEWS_SOURCE_DF)) {
            newsInfos.addAll(getNewsFromDF(isFirst));
        } else if (newsSource.equals(ConsConfig.NEWS_SOURCE_TT)) {
            newsInfos.addAll(getNewsFromTT());
        } else {
            newsInfos.addAll(getNewsFromSelf(isFirst, isShowAd));
        }
        return newsInfos;
    }

    private static List<NewsInfo> getNewsFromDF(boolean isFirst) throws IOException, JSONException {
        List<NewsInfo> newsInfos = new ArrayList<>();
        String response = "";
        if (isFirst || rowkey.equals("")) {
            response = HttpUtils.getResponse(ConsConfig.URL_EAST_NEWS);
        } else {
            response = HttpUtils.getResponse("https://newswifiapi.dftoutiao.com/jsonnew/next?type=toutiao&startkey="
                    + rowkey + "&qid=wifixhwy&ispc=0&num=20");
        }

        JSONObject json = new JSONObject(response);
        JSONArray jsonArray = json.optJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bean = jsonArray.optJSONObject(i);
            NewsInfo info = new NewsInfo();
            info.source = bean.optString("source");
            info.author = bean.optString("source");
            info.advUrl = bean.optString("url");
            info.date = bean.optString("date");
            info.pk = bean.optString("rowkey");
            info.name = bean.optString("topic");
            info.type = Constants.NEWS_SOURCE_DF;
            info.clickUrl = bean.optString("url");

            JSONArray imgs = bean.optJSONArray("miniimg02");
            List<String> imgPaths = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            int size = 0;
            for (int j = 0; j < imgs.length(); j++) {
                JSONObject temp = imgs.optJSONObject(j);
                String img1 = temp.optString("src");
                if (j == 0) info.picUrl = img1;
                sb.append(img1);
                if (j != imgs.length() - 1) {
                    sb.append("|");
                }
                size++;
            }
            info.imgs = sb.toString();
            if (size >= 3) {
                info.infoType = (ConsConfig.NEWS_SHOW_THREE_PIC);
            } else {
                info.infoType = (ConsConfig.NEWS_TYPE_PIC_TEXT);
            }
            newsInfos.add(info);
            if (i == jsonArray.length() - 1) rowkey = bean.optString("rowkey");
        }
        return newsInfos;
    }

    private static List<NewsInfo> getNewsFromTT() throws IOException, JSONException {
        List<NewsInfo> newsInfos = new ArrayList<>();
        String result = "";
        result = HttpUtils.requestCompanionNews(HttpUtils.getTTNews + SurfingHotNewsFragment.ttPageNum++);
        JSONObject json = new JSONObject(result);
        JSONArray objects = json.optJSONArray("data");
        for (int i = 0; i < objects.length(); i++) {
            JSONObject obj = objects.optJSONObject(i);
            NewsInfo newsInfo = new NewsInfo();
            newsInfo.author = obj.optString("author");
            newsInfo.name = obj.optString("title");
            newsInfo.source = obj.optString("author");
            String picUrl = obj.optString("pic1");
            if (picUrl.equals("")) {
                newsInfo.infoType = Constants.ITEM_SHOW_TYPE_SIMPLE_TEXT;
            } else {
                newsInfo.infoType = Constants.ITEM_SHOW_TYPE_SINGLE_IMG;
                newsInfo.picUrl = picUrl;
            }
            newsInfo.type = Constants.NEWS_SOURCE_TT;
            newsInfo.clickUrl = obj.optString("url");
            newsInfo.date = obj.optString("newsTime");
            newsInfo.id = 888;
            newsInfos.add(newsInfo);
        }

        return newsInfos;
    }


    /**
     * 从自有服务器获取新闻
     *
     * @param isFirst
     * @param isShowAd
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private static List<NewsInfo> getNewsFromSelf(boolean isFirst, boolean isShowAd) throws IOException, JSONException {
        List<NewsInfo> newsInfos = new ArrayList<>();
        String result = "";
        if (isFirst) {
            result = HttpUtils.requestNewsRecommend();
        } else {
            result = HttpUtils.requestDynamicNewsRecommend();
        }
        if (HttpUtils.checkResult(result)) {
            JSONObject json = new JSONObject(result).optJSONObject("data");
            if (json != null) {
                JSONArray newsArray = json.optJSONArray("recommend");
                if (newsArray != null && newsArray.length() > 0) {
                    for (int i = 0; i < newsArray.length(); i++) {
                        JSONObject newsObject = newsArray.optJSONObject(i);
                        NewsInfo newsInfo = new NewsInfo();
                        newsInfo.id = newsObject.optInt("id");
                        newsInfo.author = newsObject.optString("author");
                        newsInfo.name = newsObject.optString("title");
                        newsInfo.source = newsObject.optString("source");
                        newsInfo.infoType = newsObject.optInt("infoType");
                        newsInfo.type = newsObject.optInt("type");
                        newsInfo.clickUrl = newsObject.optString("url");
                        newsInfo.date = newsObject.optString("date");
                        newsInfo.infoType = newsObject.optInt("infoType");
                        newsInfo.picUrl = newsObject.optString("picture");
                        newsInfo.position = newsObject.optLong("position");
                        JSONArray imgs = newsObject.optJSONArray("imgs");
                        if ((newsInfo.infoType == 3 || newsInfo.infoType == 2) && imgs != null && imgs.length() > 0) {
                            StringBuffer sb = new StringBuffer();
                            for (int k = 0; k < imgs.length(); k++) {
                                JSONObject imgObject = imgs.optJSONObject(k);
                                String img1 = imgObject.optString("url");
                                sb.append(img1);
                                if (k != imgs.length() - 1) {
                                    sb.append("|");
                                }
                            }
                            newsInfo.imgs = sb.toString();

                        }
                        newsInfo.recommend = 1;
                        if (!isShowAd) {//关闭资讯的广告
                            if (newsInfo.type == 0) {
                                continue;
                            }
                        }

                        if (newsInfo.isDataValid(newsInfo)) {//过滤掉不完整数
                            newsInfos.add(newsInfo);
                        }
                    }
                }
            }
        }
        return newsInfos;
    }


    public static List<StandardInfo> getNewsBannerAdInfos(Context context) throws JSONException, IOException {
        List<StandardInfo> bannerAdList = new ArrayList<>();
        String bannerResult = HttpUtils.requestBannerAd();
        JSONObject json = new JSONObject(bannerResult);
        if (json != null) {
            JSONObject bannerData = json.optJSONObject("data");
            if (bannerData != null) {
                JSONArray bannerAd;
                String deviceCompany = !ChannelConfig.WRAP_CHANNEL.equals("") ?
                        ChannelConfig.WRAP_CHANNEL : AppConfig.getMetaDate(context.getApplicationContext(), "UMENG_CHANNEL");
                if (deviceCompany == null || deviceCompany.equals("")) {
                    bannerAd = bannerData.optJSONArray("bannerAdvs");
                } else {
                    deviceCompany = deviceCompany.toLowerCase();
                    bannerAd = bannerData.optJSONArray(deviceCompany);
                    if (bannerAd == null) {
                        bannerAd = bannerData.optJSONArray("bannerAdvs");
                    }
                }
                if (bannerAd != null && bannerAd.length() > 0) {
                    bannerAdList = new ArrayList<>();
                    for (int i = 0; i < bannerAd.length(); i++) {
                        JSONObject obj = bannerAd.optJSONObject(i);
                        int subType = obj.optInt("subType");
                        if (subType == 41 || subType == 42) {
                            StandardInfo info = new StandardInfo();
                            info.setAdId(obj.optLong("id"));
                            info.setName(obj.optString("advName"));
                            info.setName(obj.optString("advName"));
                            info.setPicUrl(obj.optString("banner"));
                            info.setDesUrl(obj.optString("advUrl"));
                            if (subType == 41) {
                                info.setType(StandardInfo.TYPE_WEB);
                                info.setPosition(StandardInfo.BANNER);
                            } else {
                                info.setType(StandardInfo.TYPE_APK);
                                info.setPosition(StandardInfo.BANNER);
                            }
                            bannerAdList.add(info);
                        }
                    }
                }
            }
        }
        return bannerAdList;
    }


    public static List<WallpaperClassifyBean> getWallpaperClassify(String result) throws JSONException {
        List<WallpaperClassifyBean> list = new ArrayList<>();
        JSONObject obj_all = new JSONObject(result);
        JSONArray objs = obj_all.optJSONArray("picType");
        for (int i = 0; i < objs.length(); i++) {
            JSONObject obj = objs.optJSONObject(i);
            WallpaperClassifyBean bean = new WallpaperClassifyBean();
            bean.setId(obj.optString("id"));
            bean.setName(obj.optString("name"));
            bean.setNewVer(obj.optInt("newVer"));
            bean.setCover(obj.optString("cover"));
            bean.setDsc(obj.optString("dsc"));
            if (ChannelConfig.SPLASH) {
                list.add(bean);
            } else {
                if (!bean.getId().equals("sexy") && !bean.getId().equals("woman") && !bean.getId().equals("summer")) {
                    list.add(bean);
                }
            }
        }
        return list;
    }

    public static List<ChannelInfo> getChnanelInfo(String result) {
        List<ChannelInfo> channelInfos = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray arys = obj.optJSONArray("data");
            for (int i = 0; i < arys.length(); i++) {
                JSONObject temp = arys.optJSONObject(i);
                ChannelInfo info = new ChannelInfo();
                info.setChannel(temp.optString("channel"));
                info.setSplash(temp.optInt("splash"));
                channelInfos.add(info);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return channelInfos;
    }


    /**
     * 获取屏蔽城市信息(在解析中处理异常，让后续继续执行)
     * 接口返回参数：spCityAds: [{cityId: "179",cityName: "杭州",adName: "tencent"}]
     *
     * @param result 接口访问结果
     * @return
     */
    public static List<ShieldCityInfo> getShieldCityInfos(String result) {
        List<ShieldCityInfo> list = new ArrayList<>();
        JSONObject object = null;
        try {
            object = new JSONObject(result);
            JSONArray citys = object.optJSONArray("spCityAd");
            if (citys != null) {
                for (int i = 0; i < citys.length(); i++) {
                    JSONObject obj_info = citys.optJSONObject(i);
                    ShieldCityInfo shieldInfo = new ShieldCityInfo();
                    shieldInfo.setCityName(obj_info.optString("spName"));
                    shieldInfo.setChannel(obj_info.optString("adName"));
                    list.add(shieldInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     *  获取需要变更的渠道信息
     *  spChannelAds: [{spName: "tencent",adName: "tencent"}]
     * @param result
     * @return
     */
    public static List<ChannelChangeInfo> getNeedChangeChannelInfos(String result) {
        List<ChannelChangeInfo> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray objs = obj.optJSONArray("spChannelAd");
            if (objs != null) {
                for (int i = 0; i < objs.length(); i++) {
                    JSONObject changeInfo = objs.optJSONObject(i);
                    ChannelChangeInfo info = new ChannelChangeInfo();
                    info.setNeedChangeChannelName(changeInfo.optString("spName"));
                    info.setChangedChannelName(changeInfo.optString("adName"));
                    list.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 从淘宝接口获取城市信息
     * {code: 0,data: {country: "中国",city: "杭州市",ity_id: "330100",ip: "60.191.52.100"}}
     * @return 返回城市信息
     */
    public static String getCityFromTaobaoInterface(Context context){
        String city = null;
        try {
            String result = HttpUtils.requestCityFromTaobao();
            LogUtil.e(TAG, "淘宝接口返回数据：" + result);
            JSONObject obj = new JSONObject(result);
            JSONObject cityInfo = obj.optJSONObject("data");
            city = cityInfo.optString("city");
        } catch (Exception e) {
            e.printStackTrace();
            city = SpUtil.getStringData(context, SpUtil.TAOBAO_CITY, "");
        }
        return city;
    }



}
