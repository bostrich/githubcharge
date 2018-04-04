package com.hodanet.charge.utils;

import android.content.Context;
import android.content.Intent;

import com.hodanet.charge.activity.NewsDetailActivity;
import com.hodanet.charge.activity.WebQQActivity;
import com.hodanet.charge.info.BaseInfo;


/**
 *
 */

public class WebLaunchUtil {
    /**
     * @param callBack
     */
    public static void launchWeb(Context context, String title, String url, boolean showAd, WebHelper.WebLoadCallBack callBack) {
        NewsDetailActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
//        intent.putExtra(WebActivity.WEB_SHOW_AD, showAd);
        context.startActivity(intent);
    }

    public static void launchWeb(Context context, BaseInfo info, WebHelper.WebLoadCallBack callBack) {
        NewsDetailActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("TITLE", info.getName());
        intent.putExtra("URL", info.getUrl());
        context.startActivity(intent);
    }

    /**
     * @param callBack
     */
    public static void launchWeb(Context context, String title, String url, boolean isBack,boolean showAd, WebHelper.WebLoadCallBack callBack) {
        NewsDetailActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
        intent.putExtra("TYPE", WebQQActivity.TYPE_AD);
        context.startActivity(intent);
    }

    public interface WebLoadCallBack {
        void loadBefore(String url);

        void loadComplete(String url);

        void loadError(String url);
    }

    public static class SimpleWebLoadCallBack implements WebLoadCallBack {
        @Override
        public void loadBefore(String url) {

        }

        @Override
        public void loadComplete(String url) {

        }

        @Override
        public void loadError(String url) {

        }
    }
}
