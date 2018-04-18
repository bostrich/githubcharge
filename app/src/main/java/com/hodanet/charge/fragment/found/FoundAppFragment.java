package com.hodanet.charge.fragment.found;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hodanet.charge.R;
import com.hodanet.charge.download.DownloadBean;
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.DownloadFeedbackImpl;
import com.hodanet.charge.download.feedback.DownloadListener;
import com.hodanet.charge.download.feedback.InfoDownloadFeedback;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.event.DownloadEvent;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.BaseInfo;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.info.NewFoundAppInfo;
import com.hodanet.charge.info.SpecialInfo;
import com.hodanet.charge.info.report.BannerHotReport;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;
import com.syezon.component.adapterview.ApkAdapterView;
import com.syezon.component.adapterview.BaseAdapterView;
import com.syezon.component.adview.ApkAd;
import com.syezon.component.bean.FoundBean;
import com.syezon.component.bean.UpdateViewInfo;
import com.syezon.component.view.DownloadProgressButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 */
public class FoundAppFragment extends Fragment implements BaseAdapterView.AdListener{


    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    Unbinder unbinder;


    private Handler mHandler;
    private ApkAd apkAd;

    public FoundAppFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_app, container, false);
        unbinder = ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);
        initView();
        initHandler();
        return view;
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    private void initView() {
        apkAd = new ApkAd(rlContent, new ApkAdapterView(getContext(), this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void click(FoundBean foundBean, View view) {
        if(view instanceof DownloadProgressButton){
            Stats.eventWithMap(getContext(), "jumeiApp", "download_click", foundBean.getName());
            if(DownloadUtil.checkInstall(getContext(), foundBean.getPkgName())){
                DownloadUtil.openApp(getContext(), foundBean.getPkgName());
            }else if(DownloadUtil.checkDownLoad(getContext(), foundBean.getName())){
                DownloadUtil.installApk(getContext(), foundBean.getName());
            }else{
                DownloadBean bean = new DownloadBean();
                bean.setUrl(foundBean.getApkUrl());
                bean.setAppName(foundBean.getName());
                bean.setPkgName(foundBean.getPkgName());
                bean.setAdId((int) (Math.random() * 1000 + 100));
                DownloadManager.getInstance(getContext()).download(bean, DownloadManager.DOWNLOAD_STRATEGY_EVENTBUS
                        , null);
            }

        }else{
            Stats.eventWithMap(getContext(), "jumeiApp", "web_click", foundBean.getName());
            WebLaunchUtil.launchWeb(getContext(), foundBean.getName(), foundBean.getUrl()
                    , foundBean.getPkgName(), foundBean.getName(),foundBean.getApkUrl(), new WebHelper.SimpleWebLoadCallBack(){
                @Override
                public void loadComplete(String url) {

                }
            });
        }
    }

    @Override
    public void show(FoundBean foundBean) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setDownloadState(DownloadEvent event){
        UpdateViewInfo info = new UpdateViewInfo();
        info.setPkgName(event.getPkgName());
        info.setState(event.getState());
        info.setProgress((int) (100.0 * event.getCurrentSize() / event.getTotalSize()));
        apkAd.updateView(info);
        if(info.getState() == DownloadEvent.DOWNLOAD_FINISH){
            Stats.eventWithMap(getContext(), "jumeiApp", "download_finish", event.getPkgName());
        }
    }

}
