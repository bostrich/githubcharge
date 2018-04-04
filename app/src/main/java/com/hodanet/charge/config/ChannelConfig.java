package com.hodanet.charge.config;

import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.TaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 渠道参数配置信息
 */

public class ChannelConfig {

    public static boolean SPLASH;
    public static String VER;
    public static boolean JMWALL;
    public static String NEWSSRC;

    public static void initChannelConfig(){
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = HttpUtils.requestChannelInfo();
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
                            isSet = true;
                            break;
                        }
                    }
                    if(!isSet) SPLASH = defaultValue;
                } catch (Exception e) {
                    e.printStackTrace();
                    SPLASH = false;
                    JMWALL = false;
                    NEWSSRC = "df";
                }
            }
        });
    }

}
