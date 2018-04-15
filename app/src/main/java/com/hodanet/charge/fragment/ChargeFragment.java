package com.hodanet.charge.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.activity.PowerOptimizeActivity;
import com.hodanet.charge.adapter.NewsAdapter;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.ConsConfig;
import com.hodanet.charge.event.BatteryChangeEvent;
import com.hodanet.charge.event.BatteryConnectEvent;
import com.hodanet.charge.event.ShowSlideMenuRedDot;
import com.hodanet.charge.event.ShowSpecialEvent;
import com.hodanet.charge.event.SlideMenuClickEvent;
import com.hodanet.charge.info.BatteryStatus;
import com.hodanet.charge.info.news.BaseNewInfo;
import com.hodanet.charge.info.news.EastNewsInfo;
import com.hodanet.charge.info.report.DailyChargeReport;
import com.hodanet.charge.info.report.FloatChargeReport;
import com.hodanet.charge.info.report.SpecialChargeReport;
import com.hodanet.charge.model.DailyAd;
import com.hodanet.charge.model.FloatAd;
import com.hodanet.charge.model.SpecialAd;
import com.hodanet.charge.utils.BluetoothUtil;
import com.hodanet.charge.utils.BrightnessUtil;
import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.WifiUtil;
import com.hodanet.charge.view.BatteryChargeView2;
import com.hodanet.charge.view.BatteryDscView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */
public class ChargeFragment extends Fragment {


    private static final int GET_NEWS_OK = 1;

    @BindView(R.id.img_slide_menu)
    ImageView imgSlideMenu;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_hour)
    TextView tvHour;
    @BindView(R.id.tv_hour_unit)
    TextView tvHourUnit;
    @BindView(R.id.tv_minute)
    TextView tvMinute;
    @BindView(R.id.tv_minute_unit)
    TextView tvMinuteUnit;
    @BindView(R.id.rl_special)
    RelativeLayout rlSpecial;
    @BindView(R.id.rl_daily)
    RelativeLayout rlDaily;
    @BindView(R.id.rv)
    RecyclerView rv;
    Unbinder unbinder;
    @BindView(R.id.rl_float)
    RelativeLayout rlFloat;
    @BindView(R.id.tv_charge_btn)
    BatteryDscView tvChargeBtn;
    @BindView(R.id.view_ring_dot)
    View viewRingDot;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.ll_news)
    LinearLayout llNews;
    @BindView(R.id.rl_slide_menu)
    RelativeLayout rlSlideMenu;
    @BindView(R.id.battery)
    BatteryChargeView2 battery;

    private FloatAd floatView;
    private SpecialAd specialView;
    private DailyAd dailyView;

    private Handler mHandler;
    private List<BaseNewInfo> list = new ArrayList<>();
    private NewsAdapter newsAdapter;

    private BatteryStatus batteryStatus = new BatteryStatus();
    private int brightness;//屏幕亮度
    private int brightnessMode;//屏幕亮度模式

    public ChargeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charge, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        iniHandler();
        initView();
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (floatView != null) floatView.showView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) floatView.onDestroy();
        if (specialView != null) specialView.onDestroy();
        if (dailyView != null) dailyView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        if (ChannelConfig.SPLASH) {
            floatView = new FloatAd.Builder().setContext(getContext()).setView(rlFloat)
                    .setReportInfo(new FloatChargeReport()).build();

            specialView = new SpecialAd.Builder().setContext(getContext()).setView(rlSpecial)
                    .setReportInfo(new SpecialChargeReport()).build();

            dailyView = new DailyAd.Builder().setContext(getContext()).setView(rlDaily)
                    .setReportInfo(new DailyChargeReport()).build();
        }
        initNews();

        //获取电池信息
        BatteryManager manager = (BatteryManager) getContext().getSystemService(Context.BATTERY_SERVICE);

    }

    private void initView() {
        newsAdapter = new NewsAdapter(getContext(), list);
        rv.setAdapter(newsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        ViewGroup.LayoutParams layoutParams = llTop.getLayoutParams();
        layoutParams.height = ScreenUtil.getScreenHeight(getContext()) - ScreenUtil.getStatusBarHeight(getContext())
                -  ScreenUtil.dipTopx(getContext(), 115);
        llTop.setLayoutParams(layoutParams);

    }


    private void iniHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_NEWS_OK:
                        list.addAll((List<BaseNewInfo>) msg.obj);
                        newsAdapter.notifyDataSetChanged();
                        if(!llNews.isShown()) llNews.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };
    }

    /**
     * 获取新闻信息
     */
    private void initNews() {
        if (ChannelConfig.NEWSSRC.equals(ConsConfig.NEWS_SOURCE_DF)) {
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        String response = HttpUtils.getResponse(ConsConfig.URL_EAST_NEWS);
                        List<BaseNewInfo> news = new ArrayList<>();
                        JSONObject json = new JSONObject(response);
                        JSONArray jsonArray = json.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bean = jsonArray.optJSONObject(i);
                            EastNewsInfo info = new EastNewsInfo();
                            info.setSource(bean.optString("source"));
                            info.setUrl(bean.optString("url"));
                            info.setDate(bean.optString("date"));
                            info.setRowkey(bean.optString("rowkey"));
                            info.setDescription(bean.optString("topic"));
                            info.setTitle(bean.optString("topic"));
                            JSONArray imgs = bean.optJSONArray("miniimg02");
                            List<String> imgPaths = new ArrayList<>();
                            for (int j = 0; j < imgs.length(); j++) {
                                JSONObject temp = imgs.optJSONObject(j);
                                imgPaths.add(temp.optString("src"));
                            }
                            info.setImages(imgPaths);
                            if (imgPaths.size() >= 3) {
                                info.setShowType(ConsConfig.NEWS_SHOW_THREE_PIC);
                            } else {
                                info.setShowType(ConsConfig.NEWS_TYPE_PIC_TEXT);
                            }
                            news.add(info);
                        }
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_NEWS_OK;
                        msg.obj = news;
                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public void changeTab() {
        if (floatView != null) floatView.showView();
        if (specialView != null) specialView.showView();
        if (dailyView != null) dailyView.showView();
    }

    @OnClick({R.id.rl_slide_menu, R.id.tv_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_slide_menu:
                EventBus.getDefault().post(new SlideMenuClickEvent());
                break;
            case R.id.tv_title:
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBatteryChange(BatteryChangeEvent event) {
        batteryStatus.setPowerPercent(event.getPercent());
        switch (event.getStatus()) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                batteryStatus.setCharging(true);
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                batteryStatus.setCharging(false);
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:

                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                batteryStatus.setCharging(true);
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:

                break;
            default:
                break;
        }
        refreshBatteryView();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectedChange(BatteryConnectEvent event) {
        batteryStatus.setCharging(event.isConnected());
        batteryStatus.setConnectType(event.getConncectType());
        refreshBatteryView();
    }

    private void refreshBatteryView() {
        battery.setAccelerate(batteryStatus.isAccelerate());
        battery.setPower(batteryStatus.getPowerPercent());
        battery.setCharging(batteryStatus.isCharging());
        tvChargeBtn.setCharging(batteryStatus.isCharging());
        tvChargeBtn.setAccelerate(batteryStatus.isAccelerate());
        if(batteryStatus.isCharging()){
            if(batteryStatus.getPowerPercent() == 100){
                tvStatus.setText("电已充满,预估可用时间");
                int time = batteryStatus.getBatteryRemainTime(getContext());
                tvHour.setText(time / 60 + "");
                tvMinute.setText(time % 60 + "");
                return;
            }
            if(batteryStatus.isAccelerate()){
                tvStatus.setText("快速充电中，预估节约时间");
                tvChargeBtn.setEnabled(true);
                int time = batteryStatus.getBatteryAccelerateTime();
                tvHour.setText(time / 60 + "");
                tvMinute.setText(time % 60 + "");
            }else{
                tvStatus.setText("正在充电,预估时间");
                tvChargeBtn.setEnabled(true);
                //计算充电时间
                int time = batteryStatus.getChargeRemainTime();
                tvHour.setText(time / 60 + "");
                tvMinute.setText(time % 60 + "");
            }
        }else{
            tvStatus.setText("正在耗电，预估可用时间");
            int time = batteryStatus.getBatteryRemainTime(getContext());
            tvHour.setText(time / 60 + "");
            tvMinute.setText(time % 60 + "");
            tvChargeBtn.setEnabled(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSlideMenuRedDot(ShowSlideMenuRedDot event) {
        if (event.isShow()) {
            viewRingDot.setVisibility(View.VISIBLE);
        } else {
            viewRingDot.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void showSpecial(ShowSpecialEvent event){
        ViewGroup.LayoutParams layoutParams = llTop.getLayoutParams();
        layoutParams.height = ScreenUtil.getScreenHeight(getContext()) - ScreenUtil.getStatusBarHeight(getContext())
                -  ScreenUtil.dipTopx(getContext(), 190);
        llTop.setLayoutParams(layoutParams);
    }


    @OnClick(R.id.tv_charge_btn)
    public void onViewClicked() {
        if(batteryStatus.isCharging()){
            if(batteryStatus.isAccelerate()){
                //TODO 停止充电加速
                batteryStatus.setAccelerate(false);
                refreshBatteryView();
                WifiUtil.openWifi(getContext());
                BluetoothUtil.openBluetooth(getContext());
                BrightnessUtil.saveBrightness(getActivity(), brightness, brightnessMode);

            }else{
                //设置充电加速
                batteryStatus.setAccelerate(true);
                refreshBatteryView();
                //调节屏幕亮度、关闭wifi、关闭蓝牙
                WifiUtil.closeWifi(getContext());
                BluetoothUtil.closeBluetooth(getContext());
                brightness = BrightnessUtil.getSystemBrightness(getContext());
                brightnessMode = BrightnessUtil.getSystemBrightnessMode(getContext());
                BrightnessUtil.saveBrightness(getActivity(), 0, 0);

            }

        }else{
            startActivity(new Intent(getContext(), PowerOptimizeActivity.class));
        }

    }
}
