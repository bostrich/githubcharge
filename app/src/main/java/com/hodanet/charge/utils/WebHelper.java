package com.hodanet.charge.utils;

import android.content.Context;
import android.content.Intent;

import com.hodanet.charge.activity.NewsDetailActivity;

/**
 *
 */

public class WebHelper {
    /**
     * @param callBack
     */
    public static void launchWeb(Context context, String title, String url, boolean showAd, WebLoadCallBack callBack) {
        NewsDetailActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
//        intent.putExtra(WebQQActivity.WEB_SHOW_AD, showAd);
        context.startActivity(intent);
    }

    public static void launcheNewsWeb(Context context, String title, String url, boolean showAd, WebLoadCallBack callBack){
        NewsDetailActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
        context.startActivity(intent);
    }

    /**
     * @param callBack
     */
    public static void showAdDetail(Context context, String title, String url, WebLoadCallBack callBack) {
        NewsDetailActivity.setWebLoadCallBack(callBack);
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("URL", url);
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
