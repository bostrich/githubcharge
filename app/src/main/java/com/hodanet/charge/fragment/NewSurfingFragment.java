package com.hodanet.charge.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hodanet.charge.R;
import com.hodanet.charge.adapter.hot.SurfingPageFragmentAdapter;
import com.hodanet.charge.info.hot.TabItemInfo;
import com.hodanet.charge.utils.NetSpeedUtil;
import com.hodanet.charge.utils.ParseUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.ToastUtil;
import com.hodanet.charge.utils.WifiConnect;

import java.util.List;


/**
 * 增加判断，预防viewPager 预加载空指针文字
 */
public class NewSurfingFragment extends Fragment implements View.OnClickListener {


    public static final int MSG_GET_NAVIGATION_OK = 1;
    public static final int MSG_GET_NAVIGATION_FAIL = 2;
    private static final String ARG_SHOW_BACK = "is_show_back";
    private boolean mParamShowBack = false;
    private ViewSwitcher mViewSwitcher;
    private ViewSwitcher mViewSwitcherSurfingLoading;
    private TabLayout mTabLayoutSurfing;
    private ViewPager mViewPagerSurfing;
    private List<TabItemInfo> mTabItemInfos;
    private ImageView mImgBack;
    private TextView mTvReLoad;
    private OnFragmentInteractionListener mListener;
    private FragmentManager mFragmentManager;
    private ImageView img_loading_rotation;

    // 功能对象
    private WifiManager mWifiManager; // WiFi管理器
    private WifiConnect mWifiConnect; // WiFi连接器
    private WifiStateReceiver mWifiStateReceiver; // WiFi状态广播接收器

//    private boolean isPrepared;//判断页面是否也完成初始化

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_NAVIGATION_OK:
                    if (mTabItemInfos != null && mTabItemInfos.size() > 0) {
                        displayDataOkViw();
                        SurfingPageFragmentAdapter adapter = new SurfingPageFragmentAdapter(mFragmentManager, mTabItemInfos);
                        mViewPagerSurfing.setAdapter(adapter);
                        mTabLayoutSurfing.setupWithViewPager(mViewPagerSurfing);
                        if (mViewPagerSurfing.getAdapter().getCount() <= 4) {
                            mTabLayoutSurfing.setTabMode(TabLayout.MODE_FIXED);
                        } else {
                            mTabLayoutSurfing.setTabMode(TabLayout.MODE_SCROLLABLE);
                        }
                    } else {
                        mHandler.sendEmptyMessage(MSG_GET_NAVIGATION_FAIL);
                    }
                    break;
                case MSG_GET_NAVIGATION_FAIL:
                    displayErrorView();
                    break;
                default:
                    break;
            }
        }
    };

    public NewSurfingFragment() {
    }

    public static NewSurfingFragment newInstance(boolean isShowBack) {
        NewSurfingFragment fragment = new NewSurfingFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_BACK, isShowBack);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamShowBack = getArguments().getBoolean(ARG_SHOW_BACK);
        }
        if (mParamShowBack) {
            mFragmentManager = getActivity().getSupportFragmentManager();
        } else {
            mFragmentManager = getChildFragmentManager();
        }
        registerWifiStateReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_new_surfing, container, false);
        initViews(contentView);
//        isPrepared = true;
        getContent();
        return contentView;
    }


    /**
     * @param contentView 初始化发现页的视图
     */
    private void initViews(View contentView) {
        mViewSwitcher = (ViewSwitcher) contentView.findViewById(R.id.viewSwitcherSurfing);
        mViewSwitcherSurfingLoading = (ViewSwitcher) contentView.findViewById(R.id.viewSwitcherSurfingLoading);
        mTabLayoutSurfing = (TabLayout) contentView.findViewById(R.id.tabLayoutSurfing);
        mViewPagerSurfing = (ViewPager) contentView.findViewById(R.id.viewPagerSurfing);
        mTvReLoad = (TextView) contentView.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
        mImgBack = (ImageView) contentView.findViewById(R.id.img_Back);
        mImgBack.setOnClickListener(this);
        if (mParamShowBack) {
            mImgBack.setVisibility(View.VISIBLE);
        } else {
            mImgBack.setVisibility(View.GONE);
        }


        img_loading_rotation = (ImageView) contentView.findViewById(R.id.img_rotation);
    }

    /**
     * 动态注册网络状态改变广播
     */
    private void registerWifiStateReceiver() {
        try {
            if (getActivity() != null) {
                mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mWifiConnect = new WifiConnect(mWifiManager);
            }
            if (mWifiStateReceiver == null) {
                mWifiStateReceiver = new WifiStateReceiver();
            }
            IntentFilter filter = new IntentFilter();
//            filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
//            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//            filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            getActivity().registerReceiver(mWifiStateReceiver, filter);
        } catch (Exception e) {

        }
    }

    /**
     * 设置item
     */
    public void setCurrentItem(){
        if(mViewPagerSurfing != null) mViewPagerSurfing.setCurrentItem(0);
    }


    /**
     * 展示数据加载错误页面
     */
    private void displayErrorView() {
        mViewSwitcher.setDisplayedChild(1);
    }

    /**
     * 展示数据加载页面
     */
    private void displayLoadingView() {
        mViewSwitcher.setDisplayedChild(0);
        mViewSwitcherSurfingLoading.setDisplayedChild(1);
        img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
    }

    /**
     * 展示数据加载正常页面
     */
    private void displayDataOkViw() {
        mViewSwitcher.setDisplayedChild(0);
        mViewSwitcherSurfingLoading.setDisplayedChild(0);
        img_loading_rotation.clearAnimation();
    }

    /**
     * 获取内容数据
     */
    private void getContent() {
        if (NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED) {
            displayLoadingView();
            mViewSwitcherSurfingLoading.setDisplayedChild(1);
            img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        mTabItemInfos = ParseUtil.getSurfingNavigationInfo();
                        mHandler.sendEmptyMessage(MSG_GET_NAVIGATION_OK);
                    } catch (Exception e) {
                        mHandler.sendEmptyMessage(MSG_GET_NAVIGATION_FAIL);
                    }
                }
            });
        } else {
            ToastUtil.toast(getActivity(), "亲，没网了检查一下网络设置吧！");
            mViewSwitcher.setDisplayedChild(1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        try {
            if (mWifiStateReceiver != null) {
                getActivity().unregisterReceiver(mWifiStateReceiver);
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReload:
                getContent();
                break;
            case R.id.img_Back:
                if (getActivity() != null) {
                    getActivity().finish();
                }
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private class WifiStateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("action is :" + action);
            if (action == null) {
                return;
            }
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    if (mTabItemInfos == null || mTabItemInfos.size() == 0) {
                        getContent();
                    }
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
        }
    }
}

