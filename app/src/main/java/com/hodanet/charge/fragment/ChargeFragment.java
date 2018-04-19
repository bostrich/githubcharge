package com.hodanet.charge.fragment;


import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
import com.hodanet.charge.info.AccelerateItem;
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
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.WifiUtil;
import com.hodanet.charge.view.AutoTextView;
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
    AutoTextView tvStatus;
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
    @BindView(R.id.tv_acce_dsc)
    TextView tvAcceDsc;
    @BindView(R.id.tv_acce_save)
    TextView tvAcceSave;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
//    @BindView(R.id.tv_auto)
//    AutoTextView tvAuto;

    private FloatAd floatView;
    private SpecialAd specialView;
    private DailyAd dailyView;

    private Handler mHandler;
    private List<BaseNewInfo> list = new ArrayList<>();
    private NewsAdapter newsAdapter;

    public static BatteryStatus batteryStatus = new BatteryStatus();
    private int brightness;//屏幕亮度
    private int brightnessMode;//屏幕亮度模式
    private List<AccelerateItem> listItem = new ArrayList<>();

    private boolean isWifiOpen;
    private boolean isBluetoothOpen;

    public ChargeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
                - ScreenUtil.dipTopx(getContext(), 115);
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
                        if (!llNews.isShown()) llNews.setVisibility(View.VISIBLE);
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
        batteryStatus.setVoltage(event.getBatteryVoltage());
        batteryStatus.setHealth(event.getBatteryHealth());
        batteryStatus.setTemp(event.getBatteryTem());
        switch (event.getStatus()) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                if (batteryStatus.getStatus() < BatteryStatus.BATTERY_CHARGE_NOMAL)
                    batteryStatus.setStatus(BatteryStatus.BATTERY_CHARGE_NOMAL);
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                batteryStatus.setStatus(BatteryStatus.BATTERY_NOCHARGE);
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:

                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                batteryStatus.setStatus(BatteryStatus.BATTERY_CHARGE_NOMAL);
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
        if (event.isConnected()) {
            batteryStatus.setStatus(BatteryStatus.BATTERY_CHARGE_NOMAL);
        } else {
            if(batteryStatus.getStatus() == BatteryStatus.BATTERY_CHARGE_ACCELERATE){
                if (isWifiOpen) {
                    WifiUtil.openWifi(getContext());
                }
                if (isBluetoothOpen) {
                    BluetoothUtil.openBluetooth(getContext());
                }
                if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC || brightness > 0) {
                    BrightnessUtil.saveBrightness(getActivity(), brightness, brightnessMode);
                }
            }
            batteryStatus.setStatus(BatteryStatus.BATTERY_NOCHARGE);

        }
        batteryStatus.setConnectType(event.getConncectType());
        refreshBatteryView();
    }

    private void refreshBatteryView() {
        battery.setPower(batteryStatus.getPowerPercent());
        battery.setState(batteryStatus.getStatus());
        tvChargeBtn.setStatus(batteryStatus.getStatus());
        tvAcceSave.setVisibility(View.GONE);
        llTime.setVisibility(View.VISIBLE);
        if (batteryStatus.getStatus() >= BatteryStatus.BATTERY_CHARGE_NOMAL) {
            if (batteryStatus.getPowerPercent() == 100) {
                tvStatus.setText("电已充满,预估可用时间", false);
                int time = batteryStatus.getBatteryRemainTime(getContext());
                tvHour.setText(time / 60 + "");
                tvMinute.setText(time % 60 + "");
                return;
            }
            if (batteryStatus.getStatus() == BatteryStatus.BATTERY_OPEN_ACCELERATE) {
                tvChargeBtn.setStatus(BatteryStatus.BATTERY_OPEN_ACCELERATE);
                return;
            }
            if (batteryStatus.getStatus() == BatteryStatus.BATTERY_CHARGE_ACCELERATE) {
                tvStatus.setText("快速充电中，预估",false);
                tvChargeBtn.setEnabled(true);
                int time = batteryStatus.getBatteryAccelerateTime();
                tvHour.setText(time / 60 + "");
                tvMinute.setText(time % 60 + "");

                int addPercent = (int) (Math.random() * 6 + 20);
                tvAcceSave.setText("充电速度提高" + addPercent + "%");
                tvAcceSave.setVisibility(View.VISIBLE);
                llTime.setVisibility(View.GONE);
            } else {
                tvStatus.setText("正在充电,预估时间",false);
                tvChargeBtn.setEnabled(true);
                //计算充电时间
                int time = batteryStatus.getChargeRemainTime();
                tvHour.setText(time / 60 + "");
                tvMinute.setText(time % 60 + "");
            }
        } else {
            tvStatus.setText("正在耗电，预估可用时间",false);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showSpecial(ShowSpecialEvent event) {
        ViewGroup.LayoutParams layoutParams = llTop.getLayoutParams();
        layoutParams.height = ScreenUtil.getScreenHeight(getContext()) - ScreenUtil.getStatusBarHeight(getContext())
                - ScreenUtil.dipTopx(getContext(), 190);
        llTop.setLayoutParams(layoutParams);
    }


    @OnClick(R.id.tv_charge_btn)
    public void onViewClicked() {
        if (batteryStatus.getStatus() > BatteryStatus.BATTERY_NOCHARGE && batteryStatus.getPowerPercent() < 100) {
            if (batteryStatus.getStatus() == BatteryStatus.BATTERY_CHARGE_ACCELERATE) {
                Stats.event(getContext(), "charge_accelerate_stop_click");
                batteryStatus.setStatus(BatteryStatus.BATTERY_OPEN_ACCELERATE);
                refreshBatteryView();
                stopAccelerateAnimation();
            } else {
                //设置充电加速
                batteryStatus.setStatus(BatteryStatus.BATTERY_OPEN_ACCELERATE);
                refreshBatteryView();
                Stats.event(getContext(), "charge_accelerate_click");
                //获取wifi，蓝牙和屏幕亮度信息
                isWifiOpen = WifiUtil.isWifiOpen(getContext());
                isBluetoothOpen = BluetoothUtil.isBluetoothOpen(getContext());
                brightness = BrightnessUtil.getSystemBrightness(getContext());
                brightnessMode = BrightnessUtil.getSystemBrightnessMode(getContext());
                listItem.clear();
                listItem.add(new AccelerateItem("适配充电加速方案", AccelerateItem.ITEM_OTHER));
                listItem.add(new AccelerateItem("检测软硬件环境", AccelerateItem.ITEM_OTHER));
                if (isWifiOpen) {
                    listItem.add(new AccelerateItem("关闭wifi", AccelerateItem.ITEM_WIFI));
                }
                if (isBluetoothOpen) {
                    listItem.add(new AccelerateItem("关闭蓝牙", AccelerateItem.ITEM_BLUE));
                }
                if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC || brightness > 0) {
                    listItem.add(new AccelerateItem("调节屏幕亮度", AccelerateItem.ITEM_BRIGHTNESS));
                }
                listItem.add(new AccelerateItem("优化运行环境", AccelerateItem.ITEM_OTHER));

//                startAccelerateAnimation(0);
                startAccelerateAnimation1(0, true);


            }

        } else {
            startActivity(new Intent(getContext(), PowerOptimizeActivity.class));
        }

    }

    private void startAccelerateAnimation1(final int position, final boolean isAccelerare) {

        if (listItem.size() - 1 >= position) {
            AccelerateItem item = listItem.get(position);
            boolean successed = false;
            switch (item.getItem()) {
                case AccelerateItem.ITEM_WIFI:
                    if(isAccelerare){
                        successed = WifiUtil.closeWifi(getContext());
                    }else{
                        successed = WifiUtil.openWifi(getContext());
                    }
                    break;
                case AccelerateItem.ITEM_BLUE:
                    if(isAccelerare){
                        successed = BluetoothUtil.closeBluetooth(getContext());
                    }else{
                        successed = BluetoothUtil.openBluetooth(getContext());
                    }

                    break;
                case AccelerateItem.ITEM_BRIGHTNESS:
                    if(isAccelerare){
                        successed = BrightnessUtil.saveBrightness(getActivity(), 0, 0);
                    }else{
                        successed = BrightnessUtil.saveBrightness(getActivity(), brightness, brightnessMode);
                    }

                    break;
                default:
                    successed = true;
                    break;
            }
            if (!successed) {
                listItem.remove(position);
                startAccelerateAnimation1(position + 1, isAccelerare);
            } else {
                tvStatus.setText(item.getDsc(), true);
//                tvStatus.previous();
                if (position == listItem.size() - 1) {
                    tvChargeBtn.setStatusAnimation(BatteryStatus.BATTERY_OPEN_ACCELERATE, 1000, 100 * (position + 1) / (listItem.size()), isAccelerare);
                } else {
                    tvChargeBtn.setStatusAnimation(BatteryStatus.BATTERY_OPEN_ACCELERATE, 2000, 100 * (position + 1) / (listItem.size()), isAccelerare
                    );
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAccelerateAnimation1(position + 1, isAccelerare);
                    }
                }, 1500);
            }
        }else{
            endAccelerateProgress(isAccelerare);
        }

    }


    /**
     * 停止充电加速
     */
    public void stopAccelerateAnimation(){
        listItem.clear();
        listItem.add(new AccelerateItem("检测软硬件环境", AccelerateItem.ITEM_OTHER));
        listItem.add(new AccelerateItem("恢复充电配置", AccelerateItem.ITEM_OTHER));
        if (isWifiOpen) {
            listItem.add(new AccelerateItem("开启wifi", AccelerateItem.ITEM_WIFI));
        }
        if (isBluetoothOpen) {
            listItem.add(new AccelerateItem("开启蓝牙", AccelerateItem.ITEM_BLUE));
        }
        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC || brightness > 0) {
            listItem.add(new AccelerateItem("调节屏幕亮度", AccelerateItem.ITEM_BRIGHTNESS));
        }
        listItem.add(new AccelerateItem("优化运行环境", AccelerateItem.ITEM_OTHER));

        startAccelerateAnimation1(0,false);

    }

//    private void startAccelerateAnimation(int position) {
//        final int location = position + 1;
//        if (listItem.size() - 1 >= position) {
//            AccelerateItem item = listItem.get(position);
//            boolean successed = false;
//            switch (item.getItem()) {
//                case AccelerateItem.ITEM_WIFI:
//                    successed = WifiUtil.closeWifi(getContext());
//                    break;
//                case AccelerateItem.ITEM_BLUE:
//                    successed = BluetoothUtil.closeBluetooth(getContext());
//                    break;
//                case AccelerateItem.ITEM_BRIGHTNESS:
//                    successed = BrightnessUtil.saveBrightness(getActivity(), 0, 0);
//                    break;
//                default:
//                    successed = true;
//                    break;
//            }
//            if (!successed) {
//                listItem.remove(position);
//                startAccelerateAnimation(position);
//            } else {
//                tvAcceDsc.setVisibility(View.VISIBLE);
//                tvAcceDsc.setText(item.getDsc());
//                int[] locationSrc = new int[2];
//                tvAcceDsc.getLocationOnScreen(locationSrc);
//                int[] locationDsc = new int[2];
//                tvStatus.getLocationOnScreen(locationDsc);
//                if (position == listItem.size() - 1) {
//                    tvChargeBtn.setStatusAnimation(BatteryStatus.BATTERY_OPEN_ACCELERATE, 1000, 100 * (position + 1) / (listItem.size()));
//                } else {
//                    tvChargeBtn.setStatusAnimation(BatteryStatus.BATTERY_OPEN_ACCELERATE, 2000, 100 * (position + 1) / (listItem.size()));
//                }
//
//
//                Animation animation = new TranslateAnimation(0, 0
//                        , 0, locationDsc[1] - locationSrc[1]);
//                animation.setDuration(1500);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                        tvStatus.setVisibility(View.INVISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        startAccelerateAnimation(location);
//                        tvStatus.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//                tvAcceDsc.startAnimation(animation);
//            }
//        } else {
//            endAccelerateProgress();
//        }
//    }


    private void endAccelerateProgress(boolean isAccelerate) {
        if(isAccelerate){
            batteryStatus.setStatus(BatteryStatus.BATTERY_CHARGE_ACCELERATE);
        }else{
            batteryStatus.setStatus(BatteryStatus.BATTERY_CHARGE_NOMAL);
        }

        tvAcceDsc.setVisibility(View.GONE);
        refreshBatteryView();
    }

}
