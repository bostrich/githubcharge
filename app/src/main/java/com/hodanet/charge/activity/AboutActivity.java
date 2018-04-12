package com.hodanet.charge.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.tv_app_name)
    TextView tvAppName;
    @BindView(R.id.tv_app_version_name)
    TextView tvAppVersionName;

    private int clickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initView();
    }


    private void initView() {
        tvAppVersionName.setText(AppConfig.VERSION_NAME);
    }


    @OnClick({R.id.rl_back, R.id.img_icon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_icon:
                clickTime ++;
                if(clickTime >= 4){
                    ToastUtil.toastLong(this, AppConfig.CHANNEL + "  " + AppConfig.VERSION_NAME
                            + "   " + AppConfig.VERSION_CODE);
                }
                break;
        }
    }
}
