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
import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.view.BatteryHorizontalView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 */
public class RecoverFragment extends Fragment {


    private static final String TAG = RecoverFragment.class.getName();
    private static final int CHECK = 1;
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
    private boolean recovered;


    private Animation anim_inner;
    private Animation anim_outer;

    private Handler mHandler;
    private boolean isChecked;//判断是否检测过


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
                switch(msg.what){
                    case CHECK:
                        tvChargeBtn.setEnabled(false);
                        tvChargeBtn.setText("电池检测中...");

                        anim_inner = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                        anim_inner.setDuration(2000);
                        anim_inner.setInterpolator(new LinearInterpolator());
                        imgRotateInner.startAnimation(anim_inner);

                        anim_outer = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                        anim_outer.setDuration(2000);
                        anim_outer.setInterpolator(new LinearInterpolator());
                        imgRotateOuter.startAnimation(anim_outer);

                        battery.setChecking(true);
                        tvScore.setVisibility(View.INVISIBLE);

                        break;
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(CHECK, 500);
    }

    private void initView(){
        ViewGroup.LayoutParams layoutParams = llContent.getLayoutParams();
        layoutParams.height = ScreenUtil.dipTopx(getContext(), 450);
        llContent.setLayoutParams(layoutParams);
        imgCirclePercent.setVisibility(View.GONE);
        imgCircleStatus.setVisibility(View.GONE);
        imgCircleTemp.setVisibility(View.GONE);
        imgCircleVoltage.setVisibility(View.GONE);
    }

    private void rotateBatteryStatus() {

        Animation animation_percent = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_percent.setDuration(5000);
        animation_percent.setInterpolator(new LinearInterpolator());
        imgCirclePercent.startAnimation(animation_percent);
        imgCirclePercent.setVisibility(View.VISIBLE);

        Animation animation_temp = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_temp.setDuration(5300);
        animation_temp.setInterpolator(new LinearInterpolator());
        imgCircleTemp.startAnimation(animation_temp);
        imgCircleTemp.setVisibility(View.VISIBLE);

        Animation animation_voltage = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_voltage.setDuration(5500);
        animation_voltage.setInterpolator(new LinearInterpolator());
        imgCircleVoltage.startAnimation(animation_voltage);
        imgCircleVoltage.setVisibility(View.VISIBLE);

        Animation animation_status = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation_status.setDuration(5150);
        animation_status.setInterpolator(new LinearInterpolator());
        imgCircleStatus.startAnimation(animation_status);
        imgCircleStatus.setVisibility(View.VISIBLE);


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
        recovered = true;
        anim_inner = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        anim_inner.setDuration(2000);
        anim_inner.setInterpolator(new LinearInterpolator());
        imgRotateInner.startAnimation(anim_inner);

        anim_outer = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        anim_outer.setDuration(2000);
        anim_outer.setInterpolator(new LinearInterpolator());
        imgRotateOuter.startAnimation(anim_outer);


        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        final int start = battery.getPercent();
        final int result = getBatteryRecoveryScore();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float percent = (float) animation.getAnimatedValue();
                LogUtil.e(TAG, "数值：" + percent);
                int stage  = start + (int) ((result - start) * percent);
                setScore(stage);
                if(percent == 1){
                    imgRotateInner.clearAnimation();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgRotateOuter.clearAnimation();
                        }
                    }, 1000);
                    tvChargeBtn.setText("已修复");
                    tvChargeBtn.setEnabled(false);
                }

            }
        });

        animator.start();
        SpUtil.saveLongData(getContext(), SpUtil.RECOVER_TIME, System.currentTimeMillis());


    }


    public void setScore(int score){
        if(score < 60){
            tvScore.setTextColor(getResources().getColor(R.color.recover_battery_forground));
        }else if(score < 80){
            tvScore.setTextColor(getResources().getColor(R.color.recover_battery_80));
        }else{
            tvScore.setTextColor(getResources().getColor(R.color.recover_battery_100));
        }
        tvScore.setText(score + "");
        tvScore.setVisibility(View.VISIBLE);
        LogUtil.e(TAG, "score:" + score);
        battery.setPercent(score);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBatteryChange(BatteryChangeEvent event) {
        if(recovered) return;
        reSetView();
        tvPercent.setText(event.getBatteryPercent());
        tvTemp.setText(event.getBatteryTem());
        tvVoltage.setText(event.getBatteryVoltage());
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

    private void reSetView() {
        imgRotateInner.clearAnimation();
        imgRotateOuter.clearAnimation();
        battery.setChecking(false);
        tvChargeBtn.setEnabled(true);
        long recoverTime = SpUtil.getLongData(getContext(), SpUtil.RECOVER_TIME, 0);
        if(System.currentTimeMillis() - recoverTime < 1000 * 60 * 60 * 4){
            tvChargeBtn.setText("已修复");
            tvChargeBtn.setEnabled(false);
        }else{
            tvChargeBtn.setText("立即修复");
        }
        setScore(getBatteryScore());

        rotateBatteryStatus();
    }

    /**
     * 电池得分：基础分50 + 一个10以内的随机数
     * 加上上次修复12小时内 给一个20~30内的随机数，
     * 减去耗电优化24小时内  0~10的随机数
     * @return
     */
    private int getBatteryScore(){
        int score = 50;
        score += (int) (Math.random() * 10);
        long recoverTime = SpUtil.getLongData(getContext(), SpUtil.RECOVER_TIME, 0);
        if(System.currentTimeMillis() - recoverTime < 1000 * 60 * 60 * 12){
            int temp = (int) ((Math.random() * 10) + 20);
            score += temp;
        }
        try {
            String result = SpUtil.getStringData(getContext(), SpUtil.OPTIMIZE_DATA, "");
            JSONObject obj = new JSONObject(result);
            long time = obj.optLong("time");
            if(System.currentTimeMillis() - time < 1000 * 60 * 60 * 24){
                int temp = (int) (Math.random() * 10);
                score -= temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    private int getBatteryRecoveryScore(){
        int result = 0;
        int add = (int) (Math.random() * 5);
        return 90 + add;
    }
}
