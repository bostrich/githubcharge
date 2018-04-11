package com.hodanet.charge.fragment;


import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.event.BatteryChangeEvent;
import com.hodanet.charge.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */
public class RecoverFragment extends Fragment {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_charge_btn)
    TextView tvChargeBtn;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.rl_recommend)
    RelativeLayout rlRecommend;
    Unbinder unbinder;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.tv_voltage)
    TextView tvVoltage;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    public RecoverFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_recover, container, false);
        unbinder = ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);

        initData();
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {

    }

    private void initData() {

    }

    @OnClick(R.id.tv_charge_btn)
    public void onViewClicked() {


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBatteryChange(BatteryChangeEvent event) {
        tvPercent.setText(event.getBatteryPercent());
        tvTemp.setText(event.getBatteryTem());
        tvVoltage.setText(event.getBatteryVoltage());
        switch (event.getBatteryHealth()) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                tvStatus.setText("优");
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                tvStatus.setText("一般");
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                tvStatus.setText("一般");
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                tvStatus.setText("差");
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                tvStatus.setText("差");

                break;
            case BatteryManager.BATTERY_HEALTH_COLD:
                tvStatus.setText("差");

                break;
            default:
                tvStatus.setText("一般");
                break;
        }


    }
}
