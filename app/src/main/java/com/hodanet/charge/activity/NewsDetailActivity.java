package com.hodanet.charge.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.hodanet.charge.MainActivity;
import com.hodanet.charge.R;
import com.hodanet.charge.config.DeviceConfig;
import com.hodanet.charge.download.DownloadBean;
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.event.DownloadEvent;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.ToastUtil;
import com.hodanet.charge.utils.WebHelper;
import com.syezon.component.bean.UpdateViewInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class NewsDetailActivity extends BaseActivity {

    // 消息
    public static final int MSG_LOAD_URL_OK = 0; // 加载成功
    public static final int MSG_LOAD_URL_FAILED = 1; // 加载失败
    public static final int MSG_DIMISS_LOAD = 2;
    private static final int GET_RECOMMEND_FOR_YOU = 121;
    private static final String TAG = NewsDetailActivity.class.getName();

    private static WebHelper.WebLoadCallBack webLoadCallBack;
    // 控件-标题栏
    private LinearLayout mLayoutBack;
    private TextView tvTitle;
    // 控件-内容栏
    private FrameLayout mLayoutWeb;
    private WebView webView;
    // 控件-加载栏
    private RelativeLayout rlytLoad;
    private View vLoad;
    private TextView tvLoad;
    private boolean isExit = false;
    // 记录数据
    private String mTitle;
    private String mUrl;
    private boolean mShowAd = true;
    private RelativeLayout ad_banner_layout;
    private ImageView ad_banner_icon, ad_banner_cancel, ad_banner_content;
    private TextView ad_banner_title, ad_banner_introduce;
    private Handler mHandler;
    private boolean backToMain;
    private String pkgName = "";
    private String appName = "";
    private String apkUrl = "";

    /**
     * 设置页面加载回调
     */
    public static void setWebLoadCallBack(WebHelper.WebLoadCallBack callBack) {
        webLoadCallBack = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("TITLE");
        mUrl = getIntent().getStringExtra("URL");
        if(intent.hasExtra("SPLASH")){
            backToMain = intent.getBooleanExtra("SPLASH", false);
        }
        if(intent.hasExtra("PKGNAME")){
            pkgName = intent.getStringExtra("PKGNAME");
        }
        if(intent.hasExtra("APPNAME")){
            appName = intent.getStringExtra("APPNAME");
        }
        if(intent.hasExtra("APKURL")){
            apkUrl = intent.getStringExtra("APKURL");
        }
        initHandler();
        initView();

        // 加载页面
        loadUrl(mUrl);
        LogUtil.i(TAG, "url:" + mUrl);
    }



    public void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_LOAD_URL_OK:
                        if (mLayoutWeb.getVisibility() != View.VISIBLE) {
                            mLayoutWeb.setVisibility(View.VISIBLE);
                        }
                        vLoad.clearAnimation();
                        rlytLoad.setVisibility(View.GONE);

                        break;
                    case MSG_LOAD_URL_FAILED:
                        showErrorPage();
                        isExit = true;
                        break;

                    case MSG_DIMISS_LOAD:
                        if (rlytLoad.getVisibility() != View.GONE) {
                            vLoad.clearAnimation();
                            rlytLoad.setVisibility(View.GONE);
                        }
                        if (mLayoutWeb.getVisibility() != View.VISIBLE) {
                            mLayoutWeb.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        };
    }


    private void initView() {
        // 控件-标题栏
        mLayoutBack = (LinearLayout) findViewById(R.id.layout_back);
        mLayoutBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    if(backToMain){
                        startActivity(new Intent(NewsDetailActivity.this, MainActivity.class));
                    }
                    finish();
                }
            }
        });
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (mTitle != null) {
            tvTitle.setText(mTitle);
        }
        // 控件-内容栏
        mLayoutWeb = (FrameLayout) findViewById(R.id.layout_webView);
        initWebSettings();
        // 加载栏
        rlytLoad = (RelativeLayout) findViewById(R.id.rlyt_load);
        rlytLoad.setVisibility(View.VISIBLE);
        vLoad = findViewById(R.id.v_load);
        tvLoad = (TextView) findViewById(R.id.tv_load);

        if(DownloadUtil.checkDownLoad(this, appName)){
            onAppDownloadComplete(pkgName);
        }
    }

    private void initWebSettings() {
        webView = new WebView(this);
        // 设置Client
        mLayoutWeb.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.addJavascriptInterface(new MyJsInterface(), "android");

        WebSettings webSetting = webView.getSettings();
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(5 * 1024 * 1024);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
    }



    /**
     * 展示加载界面
     */
    private void showLoadPage() {
        mLayoutWeb.setVisibility(View.GONE);
        rlytLoad.setVisibility(View.VISIBLE);
    }

    /**
     * 展示错误界面
     */
    private void showErrorPage() {
        ToastUtil.toast(this, "加载失败，请检查你的网络设置");
        final FrameLayout frameLayout = (FrameLayout) getWindow().getDecorView();
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        final View errorView = LayoutInflater.from(this).inflate(R.layout.layout_web_error, null);
        frameLayout.addView(errorView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        errorView.setVisibility(View.VISIBLE);
        mLayoutWeb.setVisibility(View.GONE);
        rlytLoad.setVisibility(View.GONE);
        vLoad.clearAnimation();
        RelativeLayout layout = (RelativeLayout) errorView.findViewById(R.id.layout_clean_total_tip);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.bottomMargin = ScreenUtil.getRealHeight(this) - DeviceConfig.SCREEN_HEIGHT;
        layout.setLayoutParams(params);
        errorView.findViewById(R.id.surfing_nowifi_reload).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView != null && webView.getUrl() != null) {
                    webView.reload();
                    frameLayout.removeView(errorView);
                }
            }
        });
        View viewFill = errorView.findViewById(R.id.view_fill);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewFill.getLayoutParams();
        layoutParams.height = statusBarHeight;
        viewFill.setLayoutParams(layoutParams);
        errorView.findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 加载页面
     *
     * @param url
     */
    private void loadUrl(String url) {
        if (url == null || url.equals("")) {
            mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            ToastUtil.toast(this, "地址为空");
            return;
        }

        showLoadPage();
        LogUtil.i(TAG, "url:" + url);
        // 开启加载动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        vLoad.startAnimation(rotateAnimation);
        // 加载地址内容
        webView.loadUrl(url);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 退出当前页面
     */
    private void exit() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void finish() {
        webLoadCallBack = null;
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            if(backToMain){//判断是否来自于开屏
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
        } catch (Exception e) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            mLayoutWeb.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * Web视图
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if ("alipays".equals(uri.getScheme()) || "weixin".equals(uri.getScheme())) {//支付宝支付或微信支付
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException notFoundEx) {//尝试h5网页支付
                    return true;
                }
            }
            if (url.startsWith("tel:")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            return false;
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String
                failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode == -2) {
                view.stopLoading();
                view.removeAllViews();
                view.clearView();
                mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            try {
                handler.proceed();
                mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        private String mDownloadUrl; // 记录下载地址（解决相同下载被连续回调2次的错误）
        private long mDownloadTime; // 记录下载时间（解决相同下载被连续回调2次的错误）

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        private boolean isSendLoadUrlOk = false;

        @Override
        public void onReceivedTitle(WebView view, String title) {
            tvTitle.setText(title);
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            super.onProgressChanged(webView, progress);
            if (progress >= 50) {
                mHandler.sendEmptyMessage(MSG_DIMISS_LOAD);
            }
            if ((progress > 80 || progress == 100) && !isSendLoadUrlOk) {
                mHandler.sendEmptyMessage(MSG_LOAD_URL_OK);
                if (webLoadCallBack != null) {
                    webLoadCallBack.loadComplete(mUrl);
                }
                isSendLoadUrlOk = true;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setDownloadState(DownloadEvent event){
        switch(event.getState()){
            case DownloadEvent.DOWNLOAD_PROGRESS:
                onAppDownloading(event.getPkgName());
                break;
            case DownloadEvent.DOWNLOAD_FINISH:
                onAppDownloadComplete(event.getPkgName());
                break;
        }
    }

    /**
     * 将下载按钮置为下载中状态
     */
    private void onAppDownloading(String pkg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:downloading('" + pkg+"')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    LogUtil.e("yyyyyyy", "value="+value);
                }
            });
        } else {
            webView.loadUrl("javascript:downloading('" + pkg+"')");
        }
    }

    /**
     * 将下载按钮置为下载完成状态
     */
    private void onAppDownloadComplete(String pkg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:downloadComplete('" + pkg+"')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    LogUtil.e("yyyyyyy", "value="+value);
                }
            });
        } else {
            webView.loadUrl("javascript:downloadComplete('" + pkg+"')");
        }
    }

    private class MyJsInterface {
        @JavascriptInterface
        public void onDownload(String pkg) {
            LogUtil.e("yyyyyyy", "isMainThread=" + Util.isOnMainThread() + "\npkg=" + pkg);
            //进行下载任务
            if(DownloadUtil.checkInstall(NewsDetailActivity.this, pkgName)){
                DownloadUtil.openApp(NewsDetailActivity.this, pkgName);
            }else if(DownloadUtil.checkDownLoad(NewsDetailActivity.this, appName)){
                DownloadUtil.installApk(NewsDetailActivity.this, appName);
            }else{
                DownloadBean bean = new DownloadBean();
                bean.setUrl(apkUrl);
                bean.setAppName(appName);
                bean.setPkgName(pkgName);
                bean.setAdId((int) (Math.random() * 1000 + 100));
                DownloadManager.getInstance(NewsDetailActivity.this).download(bean, DownloadManager.DOWNLOAD_STRATEGY_EVENTBUS
                        , null);
            }

        }
    }
}
