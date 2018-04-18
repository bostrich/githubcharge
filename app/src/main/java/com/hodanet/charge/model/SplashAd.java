package com.hodanet.charge.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.config.DeviceConfig;
import com.hodanet.charge.greendao.GreenDaoManager;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.greendao.gen.StandardInfoDao;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.Tools;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自有开屏广告：每次打开都要去获取开屏广告，获取到下次显示，获取不到不显示
 * Created by June on 2017/4/13.
 */
public class SplashAd {
    private ViewGroup view;
    private Context context;
    private TextView tv;//倒计时视图
    private ImageView img;//splash 图片
    private boolean isClick;//判断广告是否点击
    private View.OnClickListener listener;
    private StandardInfo info;

    public SplashAd(ViewGroup view, Context context) {
        this.view = view;
        this.context = context;
    }

    public SplashAd(Builder builder) {
        this.view = builder.view;
        this.context = builder.context;
        this.listener = builder.listener;
        initView();
    }

    private void initView() {
        img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        view.addView(img);

        tv = new TextView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)(DeviceConfig.SCREEN_SCALE * 70)
                , (int)(DeviceConfig.SCREEN_SCALE * 25));
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(0,(int)(DeviceConfig.SCREEN_SCALE * 25),(int)(DeviceConfig.SCREEN_SCALE * 25), 0);
        tv.setLayoutParams(lp);
        tv.setVisibility(View.GONE);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.bg_logo_count);
        tv.setText("跳过 " + 3);
        if(listener != null){
            tv.setOnClickListener(listener);
        }
        view.addView(tv);

        //从数据库获取数据
        GreenDaoManager.getInstance(context).getSession().clear();
        StandardInfoDao dao = GreenDaoManager.getInstance(context).getSession().getStandardInfoDao();
        List<StandardInfo> ad = dao.queryBuilder().where(StandardInfoDao.Properties.Position.eq(StandardInfo.SPLASH)).build().list();
        LogUtil.e("time",ad.size() + "");
        if(ad.size() > 0 ){
            int order = SpUtil.getSplashOrder(context);
            info = ad.get(order++ % ad.size());
            //加载图片
            File file = Tools.getDownloadFile(context, "ImgCach", "wifi_logo_" + info.getAdId());
            if (file != null && file.length() > 1000) { // 判断是否获取广告图成功
                Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0) {
                    img.setImageBitmap(bmp);
                    tv.setVisibility(View.VISIBLE);
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isClick = true;
                        }
                    });
                    SpUtil.saveSplashOrder(context, order);
                    Stats.eventWithMap(context, Constants.UMENTG_ID_SPLASH
                            , Constants.Event.OUT_SHOW.getActionName(), info.getName());
                    Stats.reportAdv(info.getAdId(), Stats.REPORT_TYPE_EXTERNAL_SHOW, Stats.ADV_TYPE_WELCOME, Stats.LOGO_ADV);
                }
            }else{
                img.setImageResource(R.mipmap.img_splash_top);
                tv.setVisibility(View.GONE);
            }
        }else{
            img.setImageResource(R.mipmap.img_splash_top);
            tv.setVisibility(View.GONE);
        }

    }

    /**
     * 设计倒计时界面
     * @param second
     */
    public void setCountDown(int second){
        if(tv != null && tv.isShown()){
            tv.setText("跳过 " + second);
        }
    }

    /**
     * 倒计时视图消失
     */
    public void countDisappear(){
        if(tv != null && tv.isShown()) tv.setVisibility(View.GONE);
    }

    /**
     * 判断是否点击
     * @return 是否点击
     */

    public boolean isClick(){
        return isClick;
    }


    /**
     * 点击广告
     */
    public void click(){
        //点击事件上报;
        Stats.eventWithMap(context, Constants.UMENTG_ID_SPLASH
                , Constants.Event.CLICK.getActionName(), info.getName());
        Stats.reportAdv(info.getAdId(), Stats.REPORT_TYPE_CLICK, Stats.ADV_TYPE_WELCOME, Stats.LOGO_ADV);
        WebLaunchUtil.launchWeb(context,info.getName(),info.getDesUrl(), true,false, new WebHelper.SimpleWebLoadCallBack(){
            @Override
            public void loadComplete(String url) {
                Stats.eventWithMap(context, Constants.UMENTG_ID_SPLASH
                        , Constants.Event.CONTENT_SHOW.getActionName(), info.getName());
                Stats.reportAdv(info.getAdId(), Stats.REPORT_TYPE_SHOW, Stats.ADV_TYPE_WELCOME, Stats.LOGO_ADV);
            }
        });

    }


    /**
     * 获取自有平台广告
     */
    public static void getSelfAd(final Context context,boolean isShowAd) {
        if(!isShowAd){
            //将数据库中旧数据全部删除
            StandardInfoDao dao = GreenDaoManager.getInstance(context).getSession().getStandardInfoDao();
            dao.queryBuilder().where(StandardInfoDao.Properties.Position.eq(StandardInfo.SPLASH))
                    .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
            return;
        }
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                List<StandardInfo> welcomeAdInfos = new ArrayList<>();
                String welcomead = null;
                try {
                    welcomead = HttpUtils.requestWelcomeAd();
                    JSONObject json = new JSONObject(welcomead);
                    if (json.length() > 0) {
                        JSONObject welcomeData = json.optJSONObject("data");
                        if (welcomeData != null) {
                            JSONArray welcomeAdArray;
                            String deviceCompany = AppConfig.CHANNEL;
                            if (deviceCompany == null || deviceCompany.equals("")) {
                                welcomeAdArray = welcomeData.optJSONArray("welcomeAd");
                            } else {
                                deviceCompany = deviceCompany.toLowerCase();
                                welcomeAdArray = welcomeData.optJSONArray(deviceCompany);
                                if (welcomeAdArray == null) {
                                    welcomeAdArray = welcomeData.optJSONArray("welcomeAd");
                                }
                            }
                            if (welcomeAdArray != null && welcomeAdArray.length() > 0) {
                                for (int i = 0; i < welcomeAdArray.length(); i++) {
                                    JSONObject obj = welcomeAdArray.optJSONObject(i);
                                    StandardInfo info = new StandardInfo();
                                    info.setAdId(obj.optLong("id"));
                                    info.setName(obj.optString("advName"));
                                    info.setPicUrl(obj.optString("picUrl"));
                                    info.setDesUrl(obj.optString("advUrl"));
                                    info.setOrder(obj.optInt("advOrder"));
                                    info.setType(StandardInfo.TYPE_WEB);
                                    info.setPosition(StandardInfo.SPLASH);
                                    welcomeAdInfos.add(info);
                                }
                            }
                            if (welcomeAdInfos.size() == 0) {
                                JSONArray defaultAd = welcomeData.optJSONArray("defaultWelcome");
                                if (defaultAd != null && defaultAd.length() > 0) {
                                    for (int i = 0; i < defaultAd.length(); i++) {
                                        JSONObject obj = defaultAd.optJSONObject(i);
                                        StandardInfo info = new StandardInfo();
                                        info.setAdId(obj.optLong("id"));
                                        info.setName(obj.optString("advName"));
                                        info.setPicUrl(obj.optString("picUrl"));
                                        info.setDesUrl(obj.optString("advUrl"));
                                        info.setOrder(obj.optInt("advOrder"));
                                        info.setType(StandardInfo.TYPE_WEB);
                                        info.setPosition(StandardInfo.SPLASH);
                                        welcomeAdInfos.add(info);
                                    }
                                }
                            }
                            //将数据库中旧数据全部删除
                            StandardInfoDao dao = GreenDaoManager.getInstance(context).getSession().getStandardInfoDao();
                            dao.queryBuilder().where(StandardInfoDao.Properties.Position.eq(StandardInfo.SPLASH))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();

                            if(welcomeAdInfos.size() == 0) return;

                            for (StandardInfo info :welcomeAdInfos) {
                                dao.insertOrReplace(info);
                            }

                            LogUtil.e("data", dao.loadAll().size() +"");
                            int order = SpUtil.getSplashOrder(context);
                            StandardInfo adInfo = welcomeAdInfos.get(order % welcomeAdArray.length());

                            //下载广告图片
                            File logoPicFile = Tools.getDownloadFile(context, "ImgCach", "wifi_logo_" + adInfo.getAdId());
                            boolean isNeedDownPic = false;
                            if (logoPicFile != null && logoPicFile.exists()) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(logoPicFile.getAbsolutePath(), options);
                                if (options.outWidth != 0 || options.outHeight != 0) {

                                } else {

                                    Tools.deleteDownloadFile(context, "ImgCach", "wifi_logo_" + adInfo.getAdId());
                                    isNeedDownPic = true;
                                }
                            } else {
                                isNeedDownPic = true;
                            }

                            String picUrl = adInfo.getPicUrl();
                            if (isNeedDownPic) {
                                for (int i = 0; i < 3; i++) {
                                    File filePic = Tools.downloadFile(context, "ImgCach", "wifi_logo_" + adInfo.getAdId(), picUrl);
                                    if (filePic != null) {//判断文件是否成功创建
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inJustDecodeBounds = true;
                                        BitmapFactory.decodeFile(filePic.getAbsolutePath(), options);
                                        if (options.outWidth != 0 || options.outHeight != 0) {//判断图片能否成功解析
                                            break;
                                        } else {
                                            Tools.deleteDownloadFile(context, "ImgCach", "wifi_logo_" + adInfo.getAdId());
                                        }
                                    } else {
                                        Tools.deleteDownloadFile(context, "ImgCach", "wifi_logo_" + adInfo.getAdId());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //出现异常将数据库中旧数据全部删除
                    StandardInfoDao dao = GreenDaoManager.getInstance(context).getSession().getStandardInfoDao();
                    dao.queryBuilder().where(StandardInfoDao.Properties.Position.eq(StandardInfo.SPLASH))
                            .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
                }
            }
        });
    }


    public static final class Builder {
        private Context context;
        private ViewGroup view;
        private View.OnClickListener listener;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setView(ViewGroup view) {
            this.view = view;
            return this;
        }

        public Builder setListener(View.OnClickListener listener){
            this.listener = listener;
            return this;
        }

        public SplashAd build() {
            return new SplashAd(this);
        }

    }

    public void onDestroy(){
        view.removeAllViews();
        listener = null;
    }
}
