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
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.DownloadListener;
import com.hodanet.charge.download.feedback.InfoDownloadFeedback;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.BaseInfo;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.info.NewFoundAppInfo;
import com.hodanet.charge.info.SpecialInfo;
import com.hodanet.charge.info.report.BannerHotReport;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;
import com.syezon.component.adapterview.ApkAdapterView;
import com.syezon.component.adapterview.BaseAdapterView;
import com.syezon.component.adview.ApkAd;
import com.syezon.component.bean.FoundBean;
import com.syezon.component.bean.UpdateViewInfo;
import com.syezon.component.view.DownloadProgressButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 */
public class FoundAppFragment extends Fragment implements BaseAdapterView.AdListener, DownloadListener{


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
    public void click(FoundBean foundBean, View view) {
        if(view instanceof DownloadProgressButton){
            DownloadUtil.downloadApk(getContext(), NewFoundAppInfo.convertInfo(foundBean)
                    , DownloadManager.DOWNLOAD_STRATERY_SERVICE, new InfoDownloadFeedback(mHandler, this));

        }else{
            WebLaunchUtil.launchWeb(getContext(), foundBean.getName(), foundBean.getUrl(), false, false, new WebHelper.SimpleWebLoadCallBack(){
                @Override
                public void loadComplete(String url) {

                }
            });
        }
    }

    @Override
    public void show(FoundBean foundBean) {

    }

    /**
     * 下载监听
     * @param pkgName
     */
    @Override
    public void start(String pkgName) {

    }

    @Override
    public void progress(long currentSize, long totalSize, String pkgName) {
        UpdateViewInfo info = new UpdateViewInfo();
        info.setPkgName(pkgName);
        info.setState(DownloadProgressButton.STATE_DOWNLOADING);
        info.setProgress((int) (currentSize * 1.0 / totalSize * 100.0));
        apkAd.updateView(info);
    }

    @Override
    public void end(String pkgName, String apkPath) {
        UpdateViewInfo info = new UpdateViewInfo();
        info.setPkgName(pkgName);
        info.setState(DownloadProgressButton.STATE_FINISH);
        apkAd.updateView(info);
    }

    @Override
    public void error(String error) {

    }
}
