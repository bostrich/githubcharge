package com.hodanet.charge;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.activity.BaseActivity;
import com.hodanet.charge.activity.SettingActivity;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.event.SlideMenuClickEvent;
import com.hodanet.charge.fragment.ChargeFragment;
import com.hodanet.charge.fragment.FoundFragment;
import com.hodanet.charge.fragment.NewSurfingFragment;
import com.hodanet.charge.fragment.RecoverFragment;
import com.hodanet.charge.info.report.RingSlideMenuInfo;
import com.hodanet.charge.model.RingAd;
import com.hodanet.charge.receiver.BatteryBroadcastReceiver;
import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.ToastUtil;
import com.syezon.component.AdManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    @BindView(R.id.ll_ring)
    LinearLayout llRing;
    @BindView(R.id.rl_setting)
    RelativeLayout rlSetting;
    @BindView(R.id.rl_feedback)
    RelativeLayout rlFeedback;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ChargeFragment chargeFragment;
    private RecoverFragment recoverFragment;
    private NewSurfingFragment surfingFragment;
    private FoundFragment foundFragment;
    private RingAd ring;

    private long exitTime;

    private BatteryBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        chargeFragment = new ChargeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_content_fragment, chargeFragment).show(chargeFragment).commit();
        Stats.event(this, "tab_charge_click");

        initView();
        initBroadcast();

        initData();

        //初始化发现广告信息
        AdManager.getInstance(this).initData();
        AdManager.setSwitch(ChannelConfig.SPLASH);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(chargeFragment != null && chargeFragment.isVisible()) chargeFragment.changeTab();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(ring != null) ring.onDestroy();
        if(receiver != null) unregisterReceiver(receiver);
    }

    private void initBroadcast() {
        receiver = new BatteryBroadcastReceiver();
        //注册系统状态的各种监听
        IntentFilter statusIntentFilter = new IntentFilter();
        //电池电量监听
        statusIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        statusIntentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        statusIntentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        statusIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        statusIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        statusIntentFilter.addAction(Intent.ACTION_POWER_USAGE_SUMMARY);
        statusIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, statusIntentFilter);
    }


    private void initData() {
        //初始化铃音广告
        if(ChannelConfig.SPLASH){
            ring = new RingAd.Builder().setContext(this).setReportInfo(new RingSlideMenuInfo()).setView(llRing).build();
        }
    }

    private void initView() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if(ring != null) ring.showView();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        setTab(R.id.ll_tab_charge);

        long clickTime = SpUtil.getLongData(this, SpUtil.DISCOVERY_CLICK_TIME, 0);
        if(System.currentTimeMillis() - clickTime < 1000 * 60 * 60 * 24){
            vDotDiscovery.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.ll_tab_charge, R.id.ll_tab_recover, R.id.ll_tab_discovery, R.id.ll_tab_hot, R.id.fl_content_fragment, R.id.rl_setting, R.id.rl_feedback, R.id.ll_ring})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_tab_charge:
                tabClick(view.getId());
                Stats.event(this, "tab_charge_click");
                break;
            case R.id.ll_tab_recover:
                tabClick(view.getId());
                Stats.event(this, "tab_recover_click");
                break;
            case R.id.ll_tab_discovery:
                tabClick(view.getId());
                vDotDiscovery.setVisibility(View.GONE);
                SpUtil.saveLongData(this, SpUtil.DISCOVERY_CLICK_TIME, System.currentTimeMillis());
                Stats.event(this, "tab_discovery_click");
                break;
            case R.id.ll_tab_hot:
                tabClick(view.getId());
                Stats.event(this, "tab_hot_click");
                break;
            case R.id.fl_content_fragment:
                break;
            case R.id.ll_ring:
                drawerLayout.closeDrawers();
                if(ring != null) ring.click();
                break;
            case R.id.rl_setting:
                startActivity(new Intent(this, SettingActivity.class));
                drawerLayout.closeDrawers();
                break;
            case R.id.rl_feedback:
                drawerLayout.closeDrawers();
                break;
        }
    }

    private void tabClick(int id) {
        setTab(id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(recoverFragment != null && recoverFragment.isVisible()) transaction.hide(recoverFragment);
        if(chargeFragment != null && chargeFragment.isVisible()) transaction.hide(chargeFragment);
        if(surfingFragment != null && surfingFragment.isVisible()) transaction.hide(surfingFragment);
        if(foundFragment != null && foundFragment.isVisible()) transaction.hide(foundFragment);
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
                    chargeFragment.changeTab();
                    transaction.show(chargeFragment);
                }else{
                    transaction.add(R.id.fl_content_fragment, chargeFragment).show(chargeFragment);
                }
                transaction.commit();
                break;
            case R.id.ll_tab_hot:
                if(surfingFragment == null) surfingFragment = NewSurfingFragment.newInstance(false);
                if(surfingFragment.isAdded()){
                    transaction.show(surfingFragment);
                }else{
                    transaction.add(R.id.fl_content_fragment, surfingFragment).show(surfingFragment);
                }
                transaction.commit();
                break;
             case R.id.ll_tab_discovery:
                 if(foundFragment == null) foundFragment = new FoundFragment();
                 if(foundFragment.isAdded()){
                     transaction.show(foundFragment);
                 }else{
                     transaction.add(R.id.fl_content_fragment, foundFragment).show(foundFragment);
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
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                break;
            case R.id.ll_tab_recover:
                imgTabRecover.setImageResource(R.mipmap.tab_recover_p);
                tvTabRecover.setTextColor(getResources().getColor(R.color.tv_tab_p));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                break;
            case R.id.ll_tab_discovery:
                imgTabDiscovery.setImageResource(R.mipmap.tab_discovery_p);
                tvTabDiscovery.setTextColor(getResources().getColor(R.color.tv_tab_p));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                break;
            case R.id.ll_tab_hot:
                imgTabHot.setImageResource(R.mipmap.tab_hot_p);
                tvTabHot.setTextColor(getResources().getColor(R.color.tv_tab_p));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                break;
        }
    }

    @Subscribe
    public void slideMenuClick(SlideMenuClickEvent event){
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.toast(getApplicationContext(), "再按一次退出程序.");
                exitTime = System.currentTimeMillis();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
