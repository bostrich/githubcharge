package com.hodanet.charge.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.adapter.found.FoundPagerAdapter;
import com.hodanet.charge.config.ChannelConfig;
import com.syezon.component.AdManager;
import com.syezon.component.adview.BaseAd;
import com.syezon.component.bean.FoundBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends Fragment {


    private static final int GET_INFO = 1;
    private static final int GET_INFO_FAILED = 2;

    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.img_rotation)
    ImageView imgRotation;
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;
    Unbinder unbinder;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.tvReload)
    TextView tvReload;
    @BindView(R.id.ll_error)
    LinearLayout llError;

    private Handler mHandler;
    private List<FoundBean> list = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private PagerAdapter adapter;

    public FoundFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        initHandler();
        initData();
        return view;
    }


    private void initData() {
        AdManager.getInstance().initData(new BaseAd.GetInfoListener<FoundBean>() {
            @Override
            public void getInfoSucceed(List<FoundBean> list) {
                Message msg = mHandler.obtainMessage();
                msg.what = GET_INFO;
                msg.obj = list;
                mHandler.sendMessage(msg);
            }

            @Override
            public void getInfoFailed() {
                mHandler.sendEmptyMessage(GET_INFO_FAILED);
            }
        }, "");

    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_INFO:
                        list.clear();
                        list.addAll((List<FoundBean>) msg.obj);
//                        if(list.size() > 0){
                        rlLoading.setVisibility(View.GONE);
                        llError.setVisibility(View.GONE);
                        imgRotation.clearAnimation();
                        rlContent.setVisibility(View.VISIBLE);
                        setViews();
//                        }
                        break;
                    case GET_INFO_FAILED:
                        llError.setVisibility(View.VISIBLE);
                        rlLoading.setVisibility(View.GONE);
                        rlContent.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    /**
     * 获取结果后设置视图
     */
    private void setViews() {
        boolean hasApp = false;
        String tabApp = "";
        titles.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPosition().equals(FoundBean.POSITION_APPS)) {
                hasApp = true;
                tabApp = list.get(i).getTag();
                break;
            }
        }
        if (hasApp && ChannelConfig.JMWALL) {//显示优质应用
            titles.add("发现");
            titles.add(tabApp);
        } else {
            titles.add("发现");
        }
        tab.setupWithViewPager(vp);
        adapter = new FoundPagerAdapter(getActivity().getSupportFragmentManager(), titles);
        vp.setAdapter(adapter);

    }

    private void initView() {
        rlContent.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);
        rlLoading.setVisibility(View.VISIBLE);
        tab.setSelectedTabIndicatorHeight(0);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
        animation.setInterpolator(new LinearInterpolator());
        imgRotation.startAnimation(animation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tvReload)
    public void onViewClicked() {
        initView();
        initData();
    }
}
