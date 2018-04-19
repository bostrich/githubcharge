package com.hodanet.charge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hodanet.charge.MainActivity;
import com.hodanet.charge.R;
import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.DeviceConfig;
import com.hodanet.charge.model.SplashAd;
import com.umeng.commonsdk.UMConfigure;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    private static final int JUMP = 1;

    @BindView(R.id.rl_top)
    RelativeLayout rlTop;

    private SplashAd splash;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        UMConfigure.setLogEnabled(true);
        initView();
        //初始化设备及APP信息
        DeviceConfig.getDeviceConfig(this);
        AppConfig.getAppConfig(this);
        //初始化配置信息
        ChannelConfig.initChannelConfig(this.getApplicationContext());
        initHandler();

        Message msg = mHandler.obtainMessage();
        msg.what = JUMP;
        msg.arg1 = 3;
        mHandler.sendMessageDelayed(msg, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splash.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    private void initHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case JUMP:
                        int count = msg.arg1;
                        if(--count > -1){
                            if(splash != null){
                                splash.setCountDown(count);
                            }
                            Message message = mHandler.obtainMessage();
                            message.what = JUMP;
                            message.arg1 = count;
                            mHandler.sendMessageDelayed(message,1000);
                        }else{
                            //倒计时视图消失
                            if(splash != null) splash.countDisappear();
                            if(splash != null && splash.isClick()){
                                //点击广告跳转上报
                                splash.click();
                                finish();
                            }else{
                                startActivity(new Intent(getApplication(), MainActivity.class));
                                finish();
                            }

                        }
                        break;
                }
            }
        };
    }

    private void initView() {
        splash = new SplashAd.Builder().setContext(this).setView(rlTop).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
            }
        }).build();
    }
}
