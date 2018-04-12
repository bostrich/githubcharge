package com.hodanet.charge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.img_battery)
    ImageView imgBattery;
    @BindView(R.id.rl_battery)
    RelativeLayout rlBattery;
    @BindView(R.id.img_hot)
    ImageView imgHot;
    @BindView(R.id.rl_hot)
    RelativeLayout rlHot;
    @BindView(R.id.img_about)
    ImageView imgAbout;
    @BindView(R.id.rl_about)
    RelativeLayout rlAbout;
    @BindView(R.id.cb_low)
    CheckBox cbLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.img_back, R.id.rl_hot, R.id.rl_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_hot:

                break;
            case R.id.rl_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }
}
