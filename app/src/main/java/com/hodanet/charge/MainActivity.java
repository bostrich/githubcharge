package com.hodanet.charge;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.activity.BaseActivity;
import com.hodanet.charge.fragment.ChargeFragment;
import com.hodanet.charge.fragment.RecoverFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.img_tab_charge)
    ImageView imgTabCharge;
    @BindView(R.id.tv_tab_charge)
    TextView tvTabCharge;
    @BindView(R.id.ll_tab_charge)
    LinearLayout llTabCharge;
    @BindView(R.id.img_tab_recover)
    ImageView imgTabRecover;
    @BindView(R.id.tv_tab_recover)
    TextView tvTabRecover;
    @BindView(R.id.ll_tab_recover)
    LinearLayout llTabRecover;
    @BindView(R.id.img_tab_discovery)
    ImageView imgTabDiscovery;
    @BindView(R.id.tv_tab_discovery)
    TextView tvTabDiscovery;
    @BindView(R.id.ll_tab_discovery)
    LinearLayout llTabDiscovery;
    @BindView(R.id.v_dot_discovery)
    View vDotDiscovery;
    @BindView(R.id.rl_tab_discovery)
    RelativeLayout rlTabDiscovery;
    @BindView(R.id.img_tab_hot)
    ImageView imgTabHot;
    @BindView(R.id.tv_tab_found)
    TextView tvTabHot;
    @BindView(R.id.ll_tab_hot)
    LinearLayout llTabHot;
    @BindView(R.id.fl_content_fragment)
    FrameLayout flContentFragment;
    @BindView(R.id.img_slide_recommand)
    ImageView imgSlideRecommand;
    @BindView(R.id.view_ring_dot)
    View viewRingDot;
    @BindView(R.id.tv_ring)
    TextView tvRing;
    @BindView(R.id.rl_ring)
    RelativeLayout rlRing;
    @BindView(R.id.rl_setting)
    RelativeLayout rlSetting;
    @BindView(R.id.rl_feedback)
    RelativeLayout rlFeedback;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ChargeFragment chargeFragment;
    private RecoverFragment recoverFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        chargeFragment = new ChargeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_content_fragment, chargeFragment).show(chargeFragment).commit();


    }

    @OnClick({R.id.ll_tab_charge, R.id.ll_tab_recover, R.id.ll_tab_discovery, R.id.ll_tab_hot, R.id.fl_content_fragment, R.id.rl_ring, R.id.rl_setting, R.id.rl_feedback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_tab_charge:
                tabClick(view.getId());
                break;
            case R.id.ll_tab_recover:
                tabClick(view.getId());
                break;
            case R.id.ll_tab_discovery:
                break;
            case R.id.ll_tab_hot:
                break;
            case R.id.fl_content_fragment:
                break;
            case R.id.rl_ring:
                break;
            case R.id.rl_setting:
                break;
            case R.id.rl_feedback:
                break;
        }
    }

    private void tabClick(int id) {
        setTab(id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(recoverFragment != null && recoverFragment.isVisible()) transaction.hide(recoverFragment);
        if(chargeFragment != null && chargeFragment.isVisible()) transaction.hide(chargeFragment);
        switch(id){
            case R.id.ll_tab_recover:
                if(recoverFragment == null) recoverFragment = new RecoverFragment();
                if(recoverFragment.isAdded()){
                    transaction.show(recoverFragment);
                }else{
                    transaction.add(R.id.fl_content_fragment, recoverFragment).show(recoverFragment);
                }
                transaction.commit();
                break;
            case R.id.ll_tab_charge:
                if(chargeFragment == null) chargeFragment = new ChargeFragment();
                if(chargeFragment.isAdded()){
                    transaction.show(chargeFragment);
                }else{
                    transaction.add(R.id.fl_content_fragment, chargeFragment).show(chargeFragment);
                }
                transaction.commit();
                break;

        }


    }

    private void setTab(int id) {
        imgTabCharge.setImageResource(R.mipmap.tab_charge_n);
        imgTabRecover.setImageResource(R.mipmap.tab_recover_n);
        imgTabHot.setImageResource(R.mipmap.tab_hot_n);
        imgTabDiscovery.setImageResource(R.mipmap.tab_discovery_n);
        int color = getResources().getColor(R.color.tv_tab_n);
        tvTabCharge.setTextColor(color);
        tvTabRecover.setTextColor(color);
        tvTabDiscovery.setTextColor(color);
        tvTabHot.setTextColor(color);
        switch(id){
            case R.id.ll_tab_charge:
                imgTabCharge.setImageResource(R.mipmap.tab_charge_p);
                tvTabCharge.setTextColor(getResources().getColor(R.color.tv_tab_p));
                break;
            case R.id.ll_tab_recover:
                imgTabRecover.setImageResource(R.mipmap.tab_recover_p);
                tvTabRecover.setTextColor(getResources().getColor(R.color.tv_tab_p));
                break;
            case R.id.ll_tab_discovery:
                imgTabDiscovery.setImageResource(R.mipmap.tab_discovery_p);
                tvTabDiscovery.setTextColor(getResources().getColor(R.color.tv_tab_p));
                break;
            case R.id.ll_tab_hot:
                imgTabHot.setImageResource(R.mipmap.tab_hot_p);
                tvTabHot.setTextColor(getResources().getColor(R.color.tv_tab_p));
                break;
        }
    }
}
