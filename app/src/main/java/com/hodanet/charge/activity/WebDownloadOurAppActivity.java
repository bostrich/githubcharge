package com.hodanet.charge.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.hodanet.charge.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class WebDownloadOurAppActivity extends BaseActivity {

//    @BindView(R.id.tv_news_title)
//    TextView mTvNewsTitle;
//    @BindView(R.id.iv_back)
//    ImageView mIvBack;
//    @BindView(R.id.webview)
//    WebView mWebView;
//    @BindView(R.id.progressBar)
//    ProgressBar mProgressBar;
//    @BindView(R.id.rl_load_error)
//    RelativeLayout mRlLoadError;
//
//    private Context mContext;
//    private int mDownloadState;
//    private String mDownloadPkg;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext=this;
//        setContentView(R.layout.activity_web_with_top);
//        ButterKnife.bind(this);
//        init();
//        setListener();
//
//        Intent intent = getIntent();
//        String newsTitle = intent.getStringExtra(Constants.INTENT_KEY_WEB_TITLE);
//        if (!TextUtils.isEmpty(newsTitle)) {
//            mTvNewsTitle.setText(newsTitle);
//        }
//
//        mDownloadState = intent.getIntExtra(Constants.INTENT_KEY_OUR_APP_DOWNLOAD_STATE, -1);
//        mDownloadPkg = intent.getStringExtra(Constants.INTENT_KEY_OUR_APP_DOWNLOAD_PKG);
//
//        String url = intent.getStringExtra(Constants.INTENT_KEY_WEB_URL);
//        if (!TextUtils.isEmpty(url)) {
//            mWebView.setVisibility(View.VISIBLE);
//            mRlLoadError.setVisibility(View.GONE);
//            mWebView.loadUrl(url);
//        }
//    }
//
//    private void setListener() {
//        mIvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                onBackPressed();
//                finish();
//            }
//        });
//
//        RxBus.getInstance().toObservable(DownloadOurAppEvent.class)
//                .compose(this.<DownloadOurAppEvent>bindToLifecycle())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DownloadOurAppEvent>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("DownloadOurAppEvent", "onError: ", e);
//                    }
//
//                    @Override
//                    public void onNext(DownloadOurAppEvent downloadOurAppEvent) {
//                        int downloadState = downloadOurAppEvent.getDownloadState();
//                        String pkgName = downloadOurAppEvent.getPkgName();
//                        if (downloadState == 1) {
//                            onAppDownloading(pkgName);
//                        } else if (downloadState == 2) {
//                            onAppDownloadComplete(pkgName);
//                        }
//                    }
//                });
//    }
//
//    private void init() {
//        String suffix = SkinManager.getInstance().getSuffix();
//        if (TextUtils.equals(suffix,"blue")) {
//            setStatusBarResourceColor(R.color.custom_actionbar_background_blue);
//        } else {
//            setStatusBarResourceColor(R.color.custom_actionbar_background);
//        }
//
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setSupportMultipleWindows(false);//The default is false
////        webSettings.setDefaultTextEncodingName("GBK");
//        webSettings.setLoadsImagesAutomatically(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
//        webSettings.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
//        webSettings.setSupportZoom(true);//是否可以缩放，默认true
//        webSettings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        webSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//        webSettings.setAppCacheEnabled(true);//是否使用缓存
//        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
//        webSettings.setAppCachePath(appCachePath);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setDomStorageEnabled(true);//DOM Storage
//        webSettings.setDisplayZoomControls(false);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//
//        mWebView.addJavascriptInterface(new MyJsInterface(), "android");
//
//        mWebView.setWebViewClient(new WebViewClient() {
//            //网页加载开始时调用，显示加载提示旋转进度条
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                mProgressBar.setVisibility(View.VISIBLE);
//            }
//
//            //网页加载完成时调用，隐藏加载提示旋转进度条
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                mProgressBar.setVisibility(View.GONE);
//                String title = view.getTitle();
//                if (!TextUtils.isEmpty(title)) {
//                    if(title.contains(".com/")){//res.ipingke.com/adsw/rd/ring.html?ch=jmm     https://buy.bianxianmao.com/common/awardSkip.h.....
//                        return;
//                    }
//                    mTvNewsTitle.setText(title);
//                } else {
//                    mTvNewsTitle.setText("");
//                }
//
//                if (mDownloadState == 1) {
//                    onAppDownloading(mDownloadPkg);
//                } else if (mDownloadState == 2) {
//                    onAppDownloadComplete(mDownloadPkg);
//                }
//            }
//
//            //网页加载失败时调用，隐藏加载提示旋转进度条
//            @Override
//            public void onReceivedError(WebView view, int errorCode,
//                                        String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
//                mProgressBar.setVisibility(View.GONE);
//                mTvNewsTitle.setText("加载错误");
//                mWebView.setVisibility(View.GONE);
//                mRlLoadError.setVisibility(View.VISIBLE);
//                ToastUtils.showUniqueToast(mContext, "加载失败");
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // 处理自定义scheme
//                if (url.startsWith("http:") || url.startsWith("https:")) {
//                    return false;
//                }
//
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                    finish();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                return true;
//            }
//
//        });
//
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                return super.onJsAlert(view, url, message, result);
//            }
//
//            @Override
//            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
////                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
//                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                transport.setWebView(mWebView);
//                resultMsg.sendToTarget();
//                return true;
//            }
//
//            @Override
//            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//                callback.invoke(origin, true, false);//注意，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
//                super.onGeolocationPermissionsShowPrompt(origin, callback);
//            }
//        });
//
//        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return true;
//            }
//        });
//
//        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
//    }
//
//    private class MyWebViewDownLoadListener implements DownloadListener {
//
//        @Override
//        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//            Intent intent = new Intent(mContext, DownloadApkService.class);
//            intent.putExtra("URL", url);
//            startService(intent);
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mWebView != null) {
//            ViewGroup parent = (ViewGroup) mWebView.getParent();
//            if (parent != null) {
//                parent.removeView(mWebView);
//            }
//            mWebView.removeAllViews();
//            mWebView.destroy();
//            mWebView = null;
//        }
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        if (mWebView.canGoBack()) {
//            mWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//
//    /**
//     * 将下载按钮置为下载中状态
//     */
//    private void onAppDownloading(String pkg) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.evaluateJavascript("javascript:downloading('" + pkg+"')", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    //此处为 js 返回的结果
//                    LogUtil.e("yyyyyyy", "value="+value);
//                }
//            });
//        } else {
//            mWebView.loadUrl("javascript:downloading('" + pkg+"')");
//        }
//    }
//
//    /**
//     * 将下载按钮置为下载完成状态
//     */
//    private void onAppDownloadComplete(String pkg) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.evaluateJavascript("javascript:downloadComplete('" + pkg+"')", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    //此处为 js 返回的结果
//                    LogUtil.e("yyyyyyy", "value="+value);
//                }
//            });
//        } else {
//            mWebView.loadUrl("javascript:downloadComplete('" + pkg+"')");
//        }
//    }
//
//    private class MyJsInterface {
//        @JavascriptInterface
//        public void onDownload(String pkg) {
//            LogUtil.e("yyyyyyy", "isMainThread=" + Util.isOnMainThread() + "\npkg=" + pkg);
//            RxBus.getInstance().post(new DownloadStartEvent(pkg));
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////
////                }
////            });
//        }
//    }
}
