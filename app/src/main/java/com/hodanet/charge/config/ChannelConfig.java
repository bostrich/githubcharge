package com.hodanet.charge.config;

import android.content.Context;

import com.hodanet.charge.info.channel.ChannelChangeInfo;
import com.hodanet.charge.info.channel.ChannelInfo;
import com.hodanet.charge.info.channel.ShieldCityInfo;
import com.hodanet.charge.model.SplashAd;
import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.ParseUtil;
import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.utils.TaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * 渠道参数配置信息
 */

public class ChannelConfig {

    public static final String URL_EAST_NEWS = "https://newswifiapi.dftoutiao.com/jsonnew/refresh?type=toutiao&qid=wifixhwy&ispc=0&num=20";
    private static final String TAG = ChannelConfig.class.getName();

    public static boolean SPLASH;
    public static String VER;
    public static boolean JMWALL;
    public static String NEWSSRC = "";
    public static String WRAP_CHANNEL = "";

    public static void initChannelConfig(final Context context){
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = HttpUtils.requestChannelInfo();

                    //获取各渠道信息
                    List<ChannelInfo> channelInfos = ParseUtil.getChnanelInfo(result);
                    //获取屏蔽城市信息
                    List<ShieldCityInfo> shieldCityInfos = ParseUtil.getShieldCityInfos(result);
                    //获取渠道变更信息
                    List<ChannelChangeInfo> channelChangeInfos = ParseUtil.getNeedChangeChannelInfos(result);
                    WRAP_CHANNEL = getConfigChannelAccordingService(context, shieldCityInfos, channelChangeInfos, AppConfig.CHANNEL);

                    JSONObject obj = new JSONObject(result);
                    VER = obj.optString("ver");
                    JMWALL = obj.optInt("jmwall") == 1;
                    NEWSSRC  = obj.optString("newssrc");
                    JSONArray ary = obj.optJSONArray("data");
                    boolean isSet = false;
                    boolean defaultValue = false;
                    for (int i = 0; i < ary.length(); i++) {
                        JSONObject channelInfo = ary.optJSONObject(i);
                        String channel = channelInfo.optString("channel");
                        if(channel.equals("tencent")){
                            defaultValue = channelInfo.optInt("splash") == 1;
                        }
                        if(channel.equals(AppConfig.CHANNEL)){
                            SPLASH = channelInfo.optInt("splash") == 1;
//                            SPLASH = true;
                            isSet = true;
                            break;
                        }
                    }
                    if(!isSet) SPLASH = defaultValue;
                } catch (Exception e) {
                    e.printStackTrace();
                    reSetParams(context);
                }finally{
                    SplashAd.getSelfAd(context, SPLASH);
                }
            }
        });
    }

    private static void reSetParams(Context context) {
        SPLASH = false;
        JMWALL = false;
        NEWSSRC = "df";
    }

    /**
     *
     * @param cityInfos     屏蔽城市集合
     * @param changeChannelInfos    渠道变更集合
     * @param default_ad    默认广告集合
     * @return 广告集合名，没有匹配返回默认渠道的广告集合名
     */
    private static String getConfigChannelAccordingService(Context context,List<ShieldCityInfo> cityInfos
            , List<ChannelChangeInfo> changeChannelInfos, String default_ad){

        //检查屏蔽渠道
        if(changeChannelInfos != null && changeChannelInfos.size() > 0){
            for (ChannelChangeInfo info :changeChannelInfos) {
                if(default_ad.equals(info.getNeedChangeChannelName())){
                    String result = info.getChangedChannelName().equals("default")? default_ad:info.getChangedChannelName();
                    return result;
                }
            }
        }

        //没有检测到屏蔽渠道检查屏蔽城市
        if(cityInfos != null && cityInfos.size() > 0) {
            String city = ParseUtil.getCityFromTaobaoInterface(context);
            LogUtil.e(TAG, "淘宝接口返回城市编码：" + city);
            SpUtil.saveStringData(context, SpUtil.TAOBAO_CITY, city);
            if (city != null) {
                for (ShieldCityInfo info : cityInfos) {
                    if (info.getCityName() == null || info.getCityName().equals("")) continue;
                    if (city.contains(info.getCityName())) {
                        String result = info.getChannel();
                        return result;
                    }
                }
            }
        }
        return default_ad;
    }

}
