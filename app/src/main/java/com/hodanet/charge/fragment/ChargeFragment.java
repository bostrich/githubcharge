package com.hodanet.charge.fragment;


import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.event.SlideMenuClickEvent;
import com.hodanet.charge.info.report.DailyChargeReport;
import com.hodanet.charge.info.report.FloatChargeReport;
import com.hodanet.charge.info.report.SpecialChargeReport;
import com.hodanet.charge.model.DailyAd;
import com.hodanet.charge.model.FloatAd;
import com.hodanet.charge.model.SpecialAd;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */
public class ChargeFragment extends Fragment {


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
    @BindView(R.id.tv_charge_btn)
    TextView tvChargeBtn;
    @BindView(R.id.rl_special)
    RelativeLayout rlSpecial;
    @BindView(R.id.rl_daily)
    RelativeLayout rlDaily;
    @BindView(R.id.rv)
    RecyclerView rv;
    Unbinder unbinder;
    @BindView(R.id.rl_float)
    RelativeLayout rlFloat;

    private FloatAd floatView;
    private SpecialAd specialView;
    private DailyAd dailyView;

    public ChargeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charge, container, false);
        unbinder = ButterKnife.bind(this, view);

        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(floatView != null) floatView.showView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatView != null) floatView.onDestroy();
        if(specialView != null) specialView.onDestroy();
        if(dailyView != null) dailyView.onDestroy();
    }

    private void initData() {
        if(ChannelConfig.SPLASH){
            floatView = new FloatAd.Builder().setContext(getContext()).setView(rlFloat)
                    .setReportInfo(new FloatChargeReport()).build();

            specialView = new SpecialAd.Builder().setContext(getContext()).setView(rlSpecial)
                    .setReportInfo(new SpecialChargeReport()).build();

            dailyView = new DailyAd.Builder().setContext(getContext()).setView(rlDaily)
                    .setReportInfo(new DailyChargeReport()).build();
        }
    }

    public void changeTab(){
        if(floatView != null) floatView.showView();
        if(specialView != null) specialView.showView();
        if(dailyView != null) dailyView.showView();
    }

    @OnClick({R.id.img_slide_menu, R.id.tv_title, R.id.tv_charge_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_slide_menu:
                EventBus.getDefault().post(new SlideMenuClickEvent());
                break;
            case R.id.tv_title:
                break;
            case R.id.tv_charge_btn:
                break;
        }
    }


}
