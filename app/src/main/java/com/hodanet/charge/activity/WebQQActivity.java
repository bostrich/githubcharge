package com.hodanet.charge.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.config.DeviceConfig;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.ToastUtil;
import com.hodanet.charge.utils.WebHelper;

import java.util.ArrayList;
import java.util.List;


public class WebQQActivity extends BaseActivity {
    private final String TAG = this.getClass().getSimpleName();

    // 定义Web页类型
    public static final int TYPE_NORMAL = 0;    // 普通页
    public static final int TYPE_AD = 1;        // 广告页
    public static final int TYPE_RECOMMEND = 2;    // 每日推荐页
    public static final int TYPE_FIND = 3;    // 发现专区
    public static final int TYPE_CONNECTED = 4; // WiFi连接成功后的推荐
    public static final int TYPE_SPECIALRECOMMEND = 5;//特型推荐
    public static final int TYPE_4GFLOW = 6;//流量快充
    public static final int TYPE_SURFING = 7;//上网导航
    public static final int TYPE_WEATHER = 8;//天气
    public static final int TYPE_DAILYRECOMMEND = 9;//每日推荐
    public static final int TYPE_SURFING_FIND = 10;//上网发现
    public static final int TYPE_SURFING_TUIGUANG = 11;//上网新闻推广
    public static final int TYPE_BANNER = 13; //Banner新闻
    public static final int TYPE_NEWSRECOMMEND_FROM_SPEEDFRAMENT = 12; // 首页新闻轮播
    public static final int TYPE_INSIDE_AD = 14;
    public static final int TYPE_LOTTERY_TURNTABLE = 15;
    public static final int TYPE_LOTTERY_EGG = 16;
    public static final int TYPE_LOTTERY_TIGER = 17;
    public static final int TYPE_LOTTERY_SCRATCH = 18;
    public static final int TYPE_LOTTERY_TURNOVER_CARD = 19;
    public static final int TYPE_INFO_EXIT_AD = 20;
    // 消息
    public static final int MSG_LOAD_URL_OK = 0; // 加载成功
    public static final int MSG_LOAD_URL_FAILED = 1; // 加载失败
    public static final int MSG_DIMISS_LOAD = 2;
    public static final String WEB_SHOW_AD = "showAd";
    private static final int GET_RECOMMEND_FOR_YOU = 121;

    private static WebHelper.WebLoadCallBack webLoadCallBack;
    protected FrameLayout mFullscreenContainer;
    // 控件-标题栏

    private LinearLayout mLayoutBack;
    private TextView tvTitle;
    // 控件-内容栏
    private ViewGroup webGroup;
    private FrameLayout mLayoutWeb;
    private MyWebView webView;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    // 控件-加载栏
    private RelativeLayout rlytLoad;
    private View vLoad;
    private TextView tvLoad;
    private boolean isExit = false;
    // 记录数据
    private int mAdvId;
    private int mType;
    private String mTitle;
    private String mUrl;
    private String mStats; // 统计标签
    private String mDownloadUrl; // 记录下载地址（解决相同下载被连续回调2次的错误）
    private long mDownloadTime; // 记录下载时间（解决相同下载被连续回调2次的错误）
    private List<String> titles = new ArrayList<>();//用于存储webview的title
    private boolean reported = false;
    private boolean destroy = false;//判断点击错误按钮是否退出Activity
    private int advPosition;
    //底部广告
    private boolean showBanner;
    private RelativeLayout ad_banner_layout;
    private ImageView ad_banner_icon, ad_banner_cancel, ad_banner_content;
    private TextView ad_banner_title, ad_banner_introduce;
    private boolean isSendLoadUrlOk = false;
    private boolean backToApp = false;
    private int source;//判断wifi新闻信息流来源用于上报进入新闻来源（1：外部信息流，2：通知）
    private String position = "";

    private TextView tvMore;
    private ImageView imgClose;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                initHandler(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 设置页面加载回调
     */
    public static void setWebLoadCallBack(WebHelper.WebLoadCallBack callBack) {
        webLoadCallBack = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        mType = intent.getIntExtra("TYPE", TYPE_NORMAL);
        mTitle = intent.getStringExtra("TITLE");
        mUrl = getIntent().getStringExtra("URL");
        mStats = getIntent().getStringExtra("STATS");
        destroy = getIntent().getBooleanExtra("destroy", false);
        advPosition = getIntent().getIntExtra("ADVPOSITION", -1);
        showBanner = intent.getBooleanExtra(WEB_SHOW_AD, false);
        if (intent.hasExtra("ADVID")) {
            mAdvId = intent.getIntExtra("ADVID", 0);
        }
        if(intent.hasExtra("BACKTOAPP")){
            backToApp = intent.getBooleanExtra("BACKTOAPP",false);
            source = intent.getIntExtra("source", 0);
            position = intent.getStringExtra("position");
        }
        reported = false;
        // 初始化控件
        initView();
        loadUrl(mUrl);
        LogUtil.i(TAG, "url:" + mUrl);
    }

    private void initRecommendForYouData() {
//        ModelInfo.getRecommendForYou(this, new ModelInfo.GetRecommendForYouListener() {
//            @Override
//            public void getRecommendForYouInfo(List<AdInfo> list) {
//                list_recommend.addAll(list);
//                mHandler.sendEmptyMessage(GET_RECOMMEND_FOR_YOU);
//            }
//        });
    }

    protected void initHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_URL_OK:
                if (mLayoutWeb.getVisibility() != View.VISIBLE) {
                    mLayoutWeb.setVisibility(View.VISIBLE);
                }
                vLoad.clearAnimation();
                rlytLoad.setVisibility(View.GONE);

                break;
            case MSG_LOAD_URL_FAILED:
//                rlytLoad.setVisibility(View.GONE);
//                webView.setVisibility(View.GONE);
//                rl_no_wifi.setVisibility(View.VISIBLE);
                showErrorPage();
                // loadUrl("file:///android_asset/404.html");
                isExit = true;
//                vLoad.clearAnimation();
//                tvLoad.setText("加载失败");
                break;
            case GET_RECOMMEND_FOR_YOU:

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



    protected void initView() {
        // 控件-标题栏

        mLayoutBack = (LinearLayout) findViewById(R.id.layout_back);
        tvMore = (TextView) findViewById(R.id.tv_more);
        imgClose = (ImageView) findViewById(R.id.img_close);
        if(backToApp){
            tvMore.setText("更多");
            imgClose.setVisibility(View.VISIBLE);
            imgClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        mLayoutBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(backToApp){
//                    Intent intent = new Intent(WebQQActivity.this, MainTabActivity.class);
//                    intent.putExtra("JUMPTOFOUND", true);
//                    startActivity(intent);
                }
                exit();
            }
        });

        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (mTitle != null) {
            tvTitle.setText(mTitle);
        }
        // 控件-内容栏
        // webGroup = (ViewGroup) findViewById(R.id.web);
        mLayoutWeb = (FrameLayout) findViewById(R.id.layout_webView);
        webView = new MyWebView(this);
//		webView.clearCache(true);
        //  webGroup.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        // 设置Client
        mLayoutWeb.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webView.setWebViewClient(new MyWebViewClient());
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebChromeClient(new MyWebChromeClient());
        // 各种设置
        WebSettings webSetting = webView.getSettings();
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
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
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setBlockNetworkImage(true);
        // 加载栏
        rlytLoad = (RelativeLayout) findViewById(R.id.rlyt_load);
        rlytLoad.setVisibility(View.VISIBLE);
        vLoad = findViewById(R.id.v_load);
        tvLoad = (TextView) findViewById(R.id.tv_load);

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
        switch (mType) {
            case TYPE_AD:
                // 广告页退出后，返回主界面
//                startActivity(new Intent(this, MainTabActivity.class));
                break;
            case TYPE_NEWSRECOMMEND_FROM_SPEEDFRAMENT:
                break;

        }
        finish();
    }


    /**
     * 设置回退
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(backToApp){
//                Intent intent = new Intent(this, MainTabActivity.class);
//                intent.putExtra("JUMPTOFOUND", true);
//                startActivity(intent);
//                finish();
                return true;
            }
            String url = webView.getUrl();
            if (url == null || url.equals(mUrl)) {
                exit();
            } else if (webView.canGoBack()) {
                if (isExit) {
                    exit();
                }
                webView.goBack(); // goBack()表示返回WebView的上一页面
                //正确显示标题
                if (titles.size() >= 2) {
                    titles.remove(titles.size() - 1);
                    tvTitle.setText(titles.get(titles.size() - 1));
                }
            } else {
                exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    protected void onDestroy() {
        try {
            mLayoutWeb.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * Web视图
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("weixin://")) {//网页内的微信支付
                Intent localIntent = new Intent();
                localIntent.setAction("android.intent.action.VIEW");
                localIntent.setData(Uri.parse(url));
                startActivity(localIntent);
                return true;
            }
            if (url.startsWith("tel:")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.getSettings().setBlockNetworkImage(false);
            //       mHandler.sendEmptyMessage(MSG_LOAD_URL_OK);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode == -2) {
                view.stopLoading();
                view.removeAllViews();
                view.clearView();
                mHandler.sendEmptyMessage(MSG_LOAD_URL_FAILED);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
//			super.onReceivedSslError(view, handler, error);
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

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//            LogUtil.i(TAG, "userAgent: " + userAgent);
//            LogUtil.i(TAG, "contentDisposition: " + contentDisposition);
//            LogUtil.i(TAG, "mimetype: " + mimetype);
//            LogUtil.i(TAG, "contentLength: " + contentLength);
//
//            // 纠错
//            if (url == null || url.equals("")) {
//                Tools.toast(getApplicationContext(), "下载地址为空");
//                return;
//            }
//
//            // 解决相同下载被连续回调2次的错误
//            if (mDownloadUrl != null && mDownloadUrl.equals(url)) {
//                if (System.currentTimeMillis() - mDownloadTime < 2000) {
//                    return;
//                }
//            }
//            mDownloadUrl = url;
//            mDownloadTime = System.currentTimeMillis();
//
//            // 获取下载地址和文件名
//            LogUtil.i(TAG, "url: " + url);
//            String apkName = url.substring(url.lastIndexOf("/") + 1);
//            if (apkName.contains(".apk")) {
//                apkName = apkName.substring(0, apkName.lastIndexOf(".apk"));
//            }
//            LogUtil.i(TAG, "apkName: " + apkName);
//
//            // 启动下载服务
//            if (mType == TYPE_FIND || mType == TYPE_CONNECTED) {
//                // 获取下载信息（如果不存在，则需要保存新的）
//                DownloadDBAdapter db = new DownloadDBAdapter(activity);
//                JSONObject jsonApk = db.getInfo(apkName);
//                if (jsonApk == null) {
//                    db.updateInfo(apkName, url, 0, DownloadState.NEW, "", 0);
//                }
//                db.close();
//                if (mType == TYPE_FIND) {
//                    DownloadUtil.downloadApk(activity, 0, url, apkName, "", "", "", true, Constants.ADV_POSITION_WEB_CONTENT_APP); // 启动下载
//                    startActivity(new Intent(activity, DownloadActivity.class));
//                    finish();
//                } else if (mType == TYPE_CONNECTED) {
//                    DownloadUtil.downloadApk(activity, 0, url, apkName, "", "", WifiConnectedActivity.DOWN_STATS, true, Constants.ADV_POSITION_WEB_CONTENT_APP); // 启动下载
//                    startActivity(new Intent(activity, DownloadActivity.class));
//                }
//            } else { // 其他下载
//                int notifyId = getNotifyId(url);
//                ApkUtil.downloadApk(activity, 0, notifyId, url, apkName, "", "", "", true, Constants.ADV_POSITION_WEB_CONTENT_APP); // 启动下载
//            }
        }
    }

    private class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context context) {
            super(context);
            setBackgroundColor(0xff000000);
        }
    }

    private class MyWebView extends WebView implements View.OnTouchListener {

        //自定义长按监听
        OnLongClickListener mLongClickListener = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                LogUtil.i(TAG, "DemoWebView-onLongClick()");
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
//                if (getX5WebViewExtension() != null) {
//                    LogUtil.i(TAG, "X5 Core:" + WebView.getQQBrowserVersion());
//                } else {
//                    LogUtil.i(TAG, "Sys Core");
//                }
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


    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mTitle != null && !mTitle.equals("")) {
                tvTitle.setText(mTitle);
                mTitle = "";
            } else {
                tvTitle.setText(title);
            }

            titles.add(title);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomViewCallback = callback;
            FrameLayout decor = (FrameLayout) getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(getApplicationContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mFullscreenContainer.addView(view, lp);
            decor.addView(mFullscreenContainer, lp);
            mCustomView = view;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }
            FrameLayout decor = (FrameLayout) getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer.removeAllViews();
            mFullscreenContainer = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mCustomView = null;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mCustomViewCallback.onCustomViewHidden();
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            super.onProgressChanged(webView, progress);
            if (progress >= 3) {
                mHandler.sendEmptyMessage(MSG_DIMISS_LOAD);
            }
            if (progress > 80 && !isSendLoadUrlOk) {
                mHandler.sendEmptyMessage(MSG_LOAD_URL_OK);
                if (webLoadCallBack != null) {
                    webLoadCallBack.loadComplete(mUrl);
                }
                if(backToApp){
                    if(source ==1)
                        Stats.event(WebQQActivity.this,"wk_outer_wifi_new_show", "弹出框:" + position);
                    if(source == 2)
                        Stats.event(WebQQActivity.this,"wk_outer_wifi_new_show", "通知:" + position);
                }
                isSendLoadUrlOk = true;
            }
        }
    }
}
