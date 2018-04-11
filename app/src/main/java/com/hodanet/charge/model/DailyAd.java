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
import com.hodanet.charge.event.ShowSpecialEvent;
import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.HotRedClickTime;
import com.hodanet.charge.greendao.gen.HotRedClickTimeDao;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.info.SpecialInfo;
import com.hodanet.charge.info.report.BaseReportInfo;
import com.hodanet.charge.utils.CustomUtil;
import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.TaskManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class DailyAd {
    private static final int GET_INFOS_OK = 1;
    private Handler mHandler;
    private SpecialInfo info;
    private ImageView imgSpecial;
    private View viewRedDot;
    private TextView tvSpecialName;
    private TextView tvSpecialSlogan;
    private ViewGroup viewParent;
    private Context context;
    private BaseReportInfo reportInfo;
    private List<SpecialInfo> rings = new ArrayList<>();
    private int position;


    public DailyAd(DailyAd.Builder builder){
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
                        List<SpecialInfo> ringInfos = (List<SpecialInfo>) msg.obj;
                        rings.clear();
                        for (SpecialInfo info : ringInfos) {
                            info.setReportInfo(reportInfo);
                            rings.add(info);
                        }
                        showView();
                        EventBus.getDefault().post(new ShowSpecialEvent());
                        break;
                }
            }
        };
    }

    /**
     * 初始化视图
     */
    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily, null);
        imgSpecial = (ImageView) view.findViewById(R.id.img_special);
        viewRedDot = view.findViewById(R.id.view_red_dot);
        tvSpecialName = (TextView) view.findViewById(R.id.tv_special_name);
        tvSpecialSlogan = (TextView) view.findViewById(R.id.tv_special_slogan);
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
                        List<SpecialInfo> floatInfos = new ArrayList<>();
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
                                            SpecialInfo adInfo = new SpecialInfo();
                                            adInfo.setId(obj.optInt("id"));
                                            adInfo.setName(obj.optString("advName"));
                                            adInfo.setPkgName(obj.optString("pkgName"));
                                            adInfo.setUrl(obj.optString("advUrl"));
                                            adInfo.setSlogan(obj.optString("slogan"));
                                            adInfo.setIconUrl(obj.optString("picUrl"));
                                            adInfo.setPkgSize(obj.optLong("pkgSize"));
                                            int type = obj.optInt("subType");
                                            if(type == 21){
                                                adInfo.setInfoType(Constants.INFO_TYPE_WEB);
                                            }else if(type == 22){
                                                adInfo.setInfoType(Constants.INFO_TYPE_APP);
                                            }else{
                                                continue;
                                            }
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
            Glide.with(context).load(info.getIconUrl()).into(imgSpecial);
            tvSpecialName.setText(info.getName());
            tvSpecialSlogan.setText(info.getSlogan());
            info.report(context, Constants.Event.OUT_SHOW);
            viewRedDot.setVisibility(View.VISIBLE);
            try{
                HotRedClickTimeDao dao = GreenDaoManager.getInstance(context).getSession().getHotRedClickTimeDao();
                List<HotRedClickTime> list = dao.queryBuilder().where(HotRedClickTimeDao.Properties.AdId.eq(info.getId())).build().list();
                if(list != null && list.size() > 0
                        && System.currentTimeMillis() - list.get(0).getCilckTime() < 1000 * 60 * 60 * 24 * 7){
                    viewRedDot.setVisibility(View.GONE);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            viewParent.setVisibility(View.VISIBLE);
            viewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.click(context);
                    try{
                        HotRedClickTimeDao dao = GreenDaoManager.getInstance(context).getSession().getHotRedClickTimeDao();
                        List<HotRedClickTime> list = dao.queryBuilder().where(HotRedClickTimeDao.Properties.AdId.eq(info.getId())).build().list();
                        if(list != null && list.size() > 0){
                            HotRedClickTime hot = list.get(0);
                            hot.setCilckTime(System.currentTimeMillis());
                            dao.update(hot);
                        }else{
                            HotRedClickTime hot = new HotRedClickTime();
                            hot.setAdId(info.getId());
                            hot.setCilckTime(System.currentTimeMillis());
                            dao.insert(hot);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
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

        public DailyAd.Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public DailyAd.Builder setView(ViewGroup view) {
            this.view = view;
            return this;
        }

        public DailyAd.Builder setReportInfo(BaseReportInfo reportInfo){
            this.reportInfo = reportInfo;
            return this;
        }

        public DailyAd build() {
            return new DailyAd(this);
        }

    }
}
