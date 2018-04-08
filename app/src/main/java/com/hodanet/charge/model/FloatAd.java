package com.hodanet.charge.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hodanet.charge.R;
import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.CustomInfo;
import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.HotRedClickTime;
import com.hodanet.charge.greendao.gen.HotRedClickTimeDao;
import com.hodanet.charge.info.BaseInfo;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.info.FloatInfo;
import com.hodanet.charge.info.RingInfo;
import com.hodanet.charge.info.report.BaseReportInfo;
import com.hodanet.charge.utils.CustomUtil;
import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.TaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class FloatAd {

    private static final int GET_INFOS_OK = 1;
    private Handler mHandler;
    private FloatInfo info;
    private ImageView imgFloat;
    private ViewGroup viewParent;
    private Context context;
    private BaseReportInfo reportInfo;
    private List<FloatInfo> rings = new ArrayList<>();
    private int position;


    public FloatAd(Builder builder){
        this.context = builder.context;
        this.viewParent = builder.view;
        this.reportInfo = builder.reportInfo;
        initView();
        initHandler();
        initData();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case GET_INFOS_OK:
                        List<FloatInfo> ringInfos = (List<FloatInfo>) msg.obj;
                        rings.clear();
                        for (FloatInfo info : ringInfos) {
                            info.setReportInfo(reportInfo);
                            rings.add(info);
                        }
                        showView();
                        break;
                }
            }
        };
    }

    /**
     * 初始化视图
     */
    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_float, null);
        imgFloat = (ImageView) view.findViewById(R.id.img_float);
        viewParent.addView(view);
    }

    private void initData() {
        if(ChannelConfig.SPLASH){
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    String ringAdInfo = null;
                    try {
                        ringAdInfo = HttpUtils.requestBannerAd();
                        JSONObject json = new JSONObject(ringAdInfo);
                        List<FloatInfo> floatInfos = new ArrayList<>();
                        if (json != null) {
                            JSONObject ringData = json.optJSONObject("data");
                            if (ringData != null) {
                                JSONArray ringAds;
                                String deviceCompany = AppConfig.getMetaDate(context.getApplicationContext(), "UMENG_CHANNEL");
                                if (deviceCompany == null || deviceCompany.equals("")) {
                                    ringAds = ringData.optJSONArray("bannerAdvs");
                                } else {
                                    ringAds = ringData.optJSONArray(deviceCompany);
                                    if (ringAds == null) {
                                        ringAds = ringData.optJSONArray("bannerAdvs");
                                    }
                                }
                                if (ringAds != null && ringAds.length() > 0) {
                                    for (int i = 0; i < ringAds.length(); i++) {
                                        JSONObject obj = ringAds.optJSONObject(i);
                                        CustomInfo targetedAdInfo = new CustomInfo();
                                        targetedAdInfo.gender = obj.optInt("gender");
                                        targetedAdInfo.interest = obj.optInt("interest");
                                        targetedAdInfo.netType = obj.optInt("netType");
                                        targetedAdInfo.device = obj.optInt("device");
                                        targetedAdInfo.osVer = obj.optInt("osVer");
                                        targetedAdInfo.resolution = obj.optInt("resolution");
                                        targetedAdInfo.operator = obj.optInt("operator");
                                        targetedAdInfo.area = obj.optLong("area");
                                        if (CustomUtil.checkCustom(context.getApplicationContext(), targetedAdInfo)) {
                                            FloatInfo adInfo = new FloatInfo();
                                            adInfo.setId(obj.optInt("id"));
                                            adInfo.setName(obj.optString("advName"));
                                            adInfo.setPkgName(obj.optString("pkgName"));
                                            adInfo.setUrl(obj.optString("advUrl"));
                                            int type = obj.optInt("subType");
                                            if(type == 61){
                                                adInfo.setInfoType(Constants.INFO_TYPE_WEB);
                                            }else if(type == 62){
                                                adInfo.setInfoType(Constants.INFO_TYPE_APP);
                                            }else{
                                                continue;
                                            }
                                            adInfo.setIconUrl(obj.optString("picUrl"));
                                            adInfo.setPkgSize(obj.optLong("pkgSize"));
                                            floatInfos.add(adInfo);
                                        }
                                    }
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = GET_INFOS_OK;
                                    msg.obj = floatInfos;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            viewParent.setVisibility(View.GONE);
        }
    }


    /**
     * 展示
     */
    public void showView() {

        if(rings.size() > 0 && ChannelConfig.SPLASH){
            info = rings.get(position++ % rings.size());
            Glide.with(context).load(info.getIconUrl()).into(imgFloat);
            info.report(context, Constants.Event.OUT_SHOW);
            viewParent.setVisibility(View.VISIBLE);
            viewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.click(context);
                }
            });

        }else{
            viewParent.setVisibility(View.GONE);
        }
    }

    public void onDestroy(){
        if(mHandler != null) mHandler.removeCallbacksAndMessages(null);
        viewParent.removeAllViews();
        viewParent.setVisibility(View.GONE);
    }


    public static final class Builder {
        private Context context;
        private ViewGroup view;
        private BaseReportInfo reportInfo;

        public FloatAd.Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public FloatAd.Builder setView(ViewGroup view) {
            this.view = view;
            return this;
        }

        public FloatAd.Builder setReportInfo(BaseReportInfo reportInfo){
            this.reportInfo = reportInfo;
            return this;
        }

        public FloatAd build() {
            return new FloatAd(this);
        }

    }
}
