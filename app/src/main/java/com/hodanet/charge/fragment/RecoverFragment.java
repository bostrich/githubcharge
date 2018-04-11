package com.hodanet.charge.fragment;


import android.animation.ValueAnimator;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.event.BatteryChangeEvent;
import com.hodanet.charge.info.RecommendInfo;
import com.hodanet.charge.info.report.RecommendRecoverReportInfo;
import com.hodanet.charge.model.RecommendModelView;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.view.BatteryHorizontalView;

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


    private static final String TAG = RecoverFragment.class.getName();
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_charge_btn)
    TextView tvChargeBtn;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.ll_recommend)
    LinearLayout llRecommend;
    Unbinder unbinder;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.tv_voltage)
    TextView tvVoltage;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.img_rotate_inner)
    ImageView imgRotateInner;
    @BindView(R.id.img_rotate_outer)
    ImageView imgRotateOuter;
    @BindView(R.id.img_circle_percent)
    ImageView imgCirclePercent;
    @BindView(R.id.img_circle_temp)
    ImageView imgCircleTemp;
    @BindView(R.id.img_circle_voltage)
    ImageView imgCircleVoltage;
    @BindView(R.id.img_circle_status)
    ImageView imgCircleStatus;
    @BindView(R.id.battery)
    BatteryHorizontalView battery;
    @BindView(R.id.tv_score)
    TextView tvScore;

    private int score;//电池得分

    private RecommendModelView recommendView;


    private Animation anim_inner;
    private Animation anim_outer;

    private Handler mHandler;


    public RecoverFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_recover, container, false);
        unbinder = ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);

        initView();
        initHandler();
        initData();
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (recommendView != null) recommendView = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    private void initView() {
        ViewGroup.LayoutParams layoutParams = llContent.getLayoutParams();
        layoutParams.height = ScreenUtil.dipTopx(getContext(), 450);
        llContent.setLayoutParams(layoutParams);

        Animation animation_percent = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_percent.setDuration(1000);
        animation_percent.setInterpolator(new LinearInterpolator());
        imgCirclePercent.startAnimation(animation_percent);

        Animation animation_temp = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_temp.setDuration(1200);
        animation_temp.setInterpolator(new LinearInterpolator());
        imgCircleTemp.startAnimation(animation_temp);

        Animation animation_voltage = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_voltage.setDuration(1500);
        animation_voltage.setInterpolator(new LinearInterpolator());
        imgCircleVoltage.startAnimation(animation_voltage);

        Animation animation_status = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_status.setDuration(1800);
        animation_status.setInterpolator(new LinearInterpolator());
        imgCircleStatus.startAnimation(animation_status);


    }

    private void initData() {
        if (ChannelConfig.SPLASH) {
            getHotRecommendAd();
        }
    }


    private void getHotRecommendAd() {
        recommendView = new RecommendModelView(getContext(), new RecommendRecoverReportInfo());
        recommendView.getHotRecommendAd(getContext(), new RecommendModelView.AdLoadSuccessListener() {
            @Override
            public void loadSuccess(View view) {
                if (view != null) {
                    ViewGroup.LayoutParams layoutParams = llContent.getLayoutParams();
                    layoutParams.height = ScreenUtil.dipTopx(getContext(), 400);
                    llContent.setLayoutParams(layoutParams);
                    llRecommend.setVisibility(View.VISIBLE);
                    llRecommend.removeAllViews();
                    llRecommend.addView(view);
                }
            }

            @Override
            public void downloadClick(Object obj) {
                if (obj instanceof RecommendInfo) {
                    RecommendInfo info = (RecommendInfo) obj;
                    info.click(getContext());
                }
            }
        });

    }

    @OnClick(R.id.tv_charge_btn)
    public void onViewClicked() {
        anim_inner = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        anim_inner.setDuration(2000);
        anim_inner.setInterpolator(new LinearInterpolator());
        imgRotateInner.startAnimation(anim_inner);

        anim_outer = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        anim_outer.setDuration(2000);
        anim_outer.setInterpolator(new LinearInterpolator());
        imgRotateOuter.startAnimation(anim_outer);


        ValueAnimator animator = ValueAnimator.ofInt(0, 200);
        animator.setDuration(5000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int percent = (int) animation.getAnimatedValue();
                LogUtil.e(TAG, "数值：" + percent);
                int stage  = (int) (score * (Math.abs(percent - 100) / 100.0));
                battery.setPercent(stage);
                tvScore.setText(stage + "");
                if(percent == 200){
                    imgRotateInner.clearAnimation();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgRotateOuter.clearAnimation();
                        }
                    }, 1000);
                }

            }
        });

        animator.start();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBatteryChange(BatteryChangeEvent event) {
        tvPercent.setText(event.getBatteryPercent());
        tvTemp.setText(event.getBatteryTem());
        tvVoltage.setText(event.getBatteryVoltage());
        battery.setPercent(event.getPercent());
        score = event.getPercent();
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
