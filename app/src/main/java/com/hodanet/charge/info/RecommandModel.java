package com.hodanet.charge.info;

import android.content.Context;


import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.CustomInfo;
import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.greendao.gen.StandardInfoDao;
import com.hodanet.charge.info.report.BaseReportInfo;
import com.hodanet.charge.utils.CustomUtil;
import com.hodanet.charge.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class RecommandModel {

    public List<StandardInfo> getInfos(Context context) throws IOException, JSONException {
        List<StandardInfo> hotRecommendAdInfos = new ArrayList<>();
        String result = HttpUtils.requestHotRecommend();
        if (HttpUtils.checkResult(result)) {
            JSONObject json = new JSONObject(result).optJSONObject("data");
            if (json != null) {
                JSONArray appArray;
                String deviceCompany = AppConfig.getMetaDate(context, "UMENG_CHANNEL");
                if (deviceCompany == null || deviceCompany.equals("")) {
                    appArray = json.optJSONArray("hotRecommends");
                } else {
                    deviceCompany = deviceCompany.toLowerCase();
                    appArray = json.optJSONArray(deviceCompany);
                    if (appArray == null) {
                        appArray = json.optJSONArray("hotRecommends");
                    }
                }
                if (appArray != null && appArray.length() > 0) {
                    for (int i = 0; i < appArray.length(); i++) {
                        JSONObject obj = appArray.optJSONObject(i);
                        StandardInfo info = new StandardInfo();
                        info.setAdId(obj.optInt("id"));
                        info.setIcon(obj.optString("picUrl"));
                        info.setName(obj.optString("advName"));
                        info.setDescription(obj.optString("pkgName"));
                        info.setDesUrl(obj.optString("advUrl"));
                        info.setPicUrl(obj.optString("picture"));
                        info.setSlogan(obj.optString("slogan"));
                        info.setDesc1(obj.optString("button"));
                        info.setType(StandardInfo.TYPE_APK);
                        info.setPosition(StandardInfo.RECOMMAND);
                        hotRecommendAdInfos.add(info);
                    }
                }

                //无匹配项，选取默认的数据
                if (hotRecommendAdInfos.size() == 0) {
                    JSONArray defaultAppArray = json.optJSONArray("defaultHot");
                    if (defaultAppArray != null && defaultAppArray.length() > 0) {
                        for (int i = 0; i < defaultAppArray.length(); i++) {
                            JSONObject obj = defaultAppArray.optJSONObject(i);
                            StandardInfo info = new StandardInfo();
                            info.setAdId(obj.optInt("id"));
                            info.setIcon(obj.optString("picUrl"));
                            info.setName(obj.optString("advName"));
                            info.setDescription(obj.optString("pkgName"));
                            info.setDesUrl(obj.optString("advUrl"));
                            info.setPicUrl(obj.optString("picture"));
                            info.setType(StandardInfo.TYPE_APK);
                            info.setPosition(StandardInfo.RECOMMAND);
                            hotRecommendAdInfos.add(info);
                        }
                    }
                }
            }
        }

        StandardInfoDao dao = GreenDaoManager.getInstance(context).getSession().getStandardInfoDao();
        for (StandardInfo info:hotRecommendAdInfos) {
            dao.insertOrReplace(info);
        }
        return hotRecommendAdInfos;
    }

    public List<RecommendInfo> getInfos(Context context, BaseReportInfo reportInfo) throws IOException, JSONException {
        List<RecommendInfo> hotRecommendAdInfos = new ArrayList<>();
        String result = HttpUtils.requestHotRecommend();
        if (HttpUtils.checkResult(result)) {
            JSONObject json = new JSONObject(result).optJSONObject("data");
            if (json != null) {
                JSONArray appArray;
                String deviceCompany = !ChannelConfig.WRAP_CHANNEL.equals("") ?
                        ChannelConfig.WRAP_CHANNEL : AppConfig.getMetaDate(context.getApplicationContext(), "UMENG_CHANNEL");
                if (deviceCompany == null || deviceCompany.equals("")) {
                    appArray = json.optJSONArray("hotRecommends");
                } else {
                    deviceCompany = deviceCompany.toLowerCase();
                    appArray = json.optJSONArray(deviceCompany);
                    if (appArray == null) {
                        appArray = json.optJSONArray("hotRecommends");
                    }
                }
                if (appArray != null && appArray.length() > 0) {
                    for (int i = 0; i < appArray.length(); i++) {
                        JSONObject obj = appArray.optJSONObject(i);
                        if(checkCondition(context, obj)){
                            RecommendInfo info = new RecommendInfo();
                            info.setReportInfo(reportInfo);
                            info.setId(obj.optInt("id"));
                            info.setIconUrl(obj.optString("picUrl"));
                            info.setName(obj.optString("advName"));
                            info.setPkgName(obj.optString("pkgName"));
                            info.setUrl(obj.optString("advUrl"));
                            info.setBigPictureUrl(obj.optString("picture"));
                            info.setSlogan(obj.optString("slogan"));
                            info.setButtonName(obj.optString("button"));
                            if(info.getPkgName().equals("1")){
                                info.setInfoType(Constants.INFO_TYPE_WEB);
                            }else info.setInfoType(Constants.INFO_TYPE_APP);
                            hotRecommendAdInfos.add(info);
                        }
                    }
                }

//               if (!isFilterAd) {
                //无匹配项，选取默认的数据
                if (hotRecommendAdInfos.size() == 0) {
                    JSONArray defaultAppArray = json.optJSONArray("defaultHot");
                    if (defaultAppArray != null && defaultAppArray.length() > 0) {
                        for (int i = 0; i < defaultAppArray.length(); i++) {
                            JSONObject obj = defaultAppArray.optJSONObject(i);
                            if(checkCondition(context, obj)){
                                RecommendInfo info = new RecommendInfo();
                                info.setReportInfo(reportInfo);
                                info.setId(obj.optInt("id"));
                                info.setIconUrl(obj.optString("picUrl"));
                                info.setName(obj.optString("advName"));
                                info.setPkgName(obj.optString("pkgName"));
                                info.setUrl(obj.optString("advUrl"));
                                info.setBigPictureUrl(obj.optString("picture"));
                                info.setSlogan(obj.optString("slogan"));
                                info.setButtonName(obj.optString("button"));
                                if(info.getPkgName().equals("1")){
                                    info.setInfoType(Constants.INFO_TYPE_WEB);
                                }else info.setInfoType(Constants.INFO_TYPE_APP);
                                hotRecommendAdInfos.add(info);
                            }
                        }
                    }
                }
            }
        }
        return hotRecommendAdInfos;
    }

    /**
     * 检查条件是否符合
     * @param obj
     * @return
     */
    private boolean checkCondition(Context context, JSONObject obj) {
        CustomInfo customInfo = new CustomInfo();
        customInfo.gender = obj.optInt("gender");
        customInfo.interest = obj.optInt("interest");
        customInfo.netType = obj.optInt("netType");
        customInfo.device = obj.optInt("device");
        customInfo.osVer = obj.optInt("osVer");
        customInfo.resolution = obj.optInt("resolution");
        customInfo.operator = obj.optInt("operator");
        customInfo.area = obj.optLong("area");
        return CustomUtil.checkCustom(context, customInfo);
    }


    public static class Builder{
        public RecommandModel build(){
            return new RecommandModel();
        }
    }
}
