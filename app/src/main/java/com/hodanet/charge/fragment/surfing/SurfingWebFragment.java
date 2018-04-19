package com.hodanet.charge.fragment.surfing;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hodanet.charge.R;
import com.hodanet.charge.activity.WebQQActivity;
import com.hodanet.charge.utils.ToastUtil;
import com.hodanet.charge.utils.Tools;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;

public class SurfingWebFragment extends BaseFragment implements View.OnClickListener {

    public static final int MES_URL_FAIL = 2;
    public static final int MES_DISMISS_LOAD = 3;
    private static final String ARG_URL = "url";
    private static final String ID = "id";
    private String mParamUrl;
    private String mId = "";

    private ViewSwitcher mViewSwitcherSurfingWebError;
    private ViewSwitcher mViewSwitcherSurfingWebLoading;
    private ImageView img_loading_rotation;
    private TextView mTvReLoad;

    private WebView mWebView;
    private FrameLayout mLayoutWebView;

    private boolean isLoadData;

    private Handler mDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MES_URL_FAIL:
                    displaySurfingWebErrorView();
                    break;
                case MES_DISMISS_LOAD:
                    displaySurfingWebDataOkViw();
                    break;
            }
        }
    };

    public SurfingWebFragment() {
    }

    public static SurfingWebFragment newInstance(String paramUrl, String id) {
        SurfingWebFragment fragment = new SurfingWebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, paramUrl);
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamUrl = getArguments().getString(ARG_URL);
            mId = getArguments().getString(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_surfing_web, null);
            initView(contentView);
            isPrepared = true;
            loadData();
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }

    private void initView(View view) {
        mViewSwitcherSurfingWebError = (ViewSwitcher) view.findViewById(R.id.viewSwitcherSurfingWeb);
        mViewSwitcherSurfingWebLoading = (ViewSwitcher) view.findViewById(R.id.viewSwitcherSurfingWebLoading);
        mTvReLoad = (TextView) view.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
        mLayoutWebView = (FrameLayout) view.findViewById(R.id.layout_webView);
        mWebView = new MyWebView(getActivity());
        mLayoutWebView.addView(mWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
            }

            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {

            }

            @Override
            public void onHideCustomView() {

            }

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                super.onProgressChanged(webView, progress);
                if (progress >= 60) {
                    isLoadData = true;
                }
                if (progress > 3) {
                    mDataHandler.sendEmptyMessage(MES_DISMISS_LOAD);
                }
            }
        });
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(getActivity().getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(getActivity().getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(getActivity().getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setBlockNetworkImage(true);

        img_loading_rotation = (ImageView) contentView.findViewById(R.id.img_rotation);

    }


    /**
     * 加载页面
     *
     * @param url
     */
    private void loadUrl(String url) {
        if (url == null || url.equals("")) {
            mDataHandler.sendEmptyMessage(MES_URL_FAIL);
            ToastUtil.toast(getActivity(), "地址为空");
            return;
        }
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }


    /**
     * 展示网页数据加载错误页面
     */
    private void displaySurfingWebErrorView() {
        mViewSwitcherSurfingWebError.setDisplayedChild(1);
    }

    /**
     * 展示网页数据加载页面
     */
    private void displaySurfingWebLoadingView() {
        mViewSwitcherSurfingWebError.setDisplayedChild(0);
        mViewSwitcherSurfingWebLoading.setDisplayedChild(1);
        img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
    }

    /**
     * 展示网页数据加载正常页面
     */
    private void displaySurfingWebDataOkViw() {
        mViewSwitcherSurfingWebError.setDisplayedChild(0);
        mViewSwitcherSurfingWebLoading.setDisplayedChild(0);
        img_loading_rotation.clearAnimation();
    }


    @Override
    protected void loadData() {
        if (!isPrepared || !isVisible) {
            return;
        } else {
            if (!isLoadData) {
                displaySurfingWebLoadingView();
                loadUrl(mParamUrl);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReload:
                loadData();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
//        MobclickAgent.onPageStart("found_web");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
//        MobclickAgent.onPageEnd("found_web");
    }

    /**
     * Web视图
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            WebView.HitTestResult hit = view.getHitTestResult();
            if (!TextUtils.isEmpty(url) && hit != null && hit.getExtra() == null) {
                view.loadUrl(url);
                return true;
            }
            if (url.startsWith("weixin://")) {//网页内的微信支付
                Intent localIntent = new Intent();
                localIntent.setAction("android.intent.action.VIEW");
                localIntent.setData(Uri.parse(url));
                getActivity().startActivity(localIntent);
                return true;
            } else {
                if (hit != null) {
                    Intent intent = new Intent(getActivity(), WebQQActivity.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("destroy", true);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.out_to_left, R.anim.in_from_right);
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String
                failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            try {
                handler.proceed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载，1.判断地址是否正确，2.判断是否连续进行同一下载，
     * 获取文件名和下载地址，进行下载
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            // 纠错
            if (url == null || url.equals("")) {
                ToastUtil.toast(getActivity(), "下载地址为空");
                return;
            }

            // 获取下载地址和文件名
            String apkName = url.substring(url.lastIndexOf("/") + 1);
            if (apkName.contains(".apk")) {
                apkName = apkName.substring(0, apkName.lastIndexOf(".apk"));
            }
            // 启动下载服务
            int notifyId = getNotifyId(url);
//            ApkUtil.downloadApk(getActivity(), 0, notifyId, url, apkName, "", "", "", true, Constants.ADV_POSITION_WEB_CONTENT_APP); // 启动下载
        }

        /**
         * 获取下载通知id,将下载的ID添加到SharePreference中去，便于判断
         *
         * @param url
         */
        private int getNotifyId(String url) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int notifyId = sp.getInt("DOWNLOAD_NOTIFY_ID_" + url, 0);
            if (notifyId == 0) { // 如果不存在，则创建并保存
//                notifyId = DownloadNotification.createNewId(getActivity());
                sp.edit().putInt("DOWNLOAD_NOTIFY_ID_" + url, notifyId).commit();
            }
            return notifyId;
        }
    }

    private class MyWebView extends WebView implements View.OnTouchListener {


        //自定义长按监听
        View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return false; // 返回false，则会继续传递长按事件到内核处理; 返回true，则停止传递。
            }
        };

        public MyWebView(Context context) {
            super(context);
            try {
                setOnTouchListener(this);
                //=================设置长按监听方法，两种方法==================
                //方法一：对所有版本都适用（暂时建议使用此方法）
                this.setOnLongClickListener(mLongClickListener);
                //方法二：只对5.3及以上版本的x5内核适用；5.2及以下版本，这种方法会覆盖内核的长按监听，导致长按后，内核无相应。
                //this.getView().setOnLongClickListener(mLongClickListener);
                //=================设置长按监听方法 end=======================
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean ret = super.drawChild(canvas, child, drawingTime);
            try {
                if (getX5WebViewExtension() != null) {
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event != null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                }
            }
            return false;
        }
    }
}
