package com.hodanet.charge.adapter.hot;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hodanet.charge.R;
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.DailyInfo;
import com.hodanet.charge.info.report.BannerHotReport;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.WebHelper;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by Administrator on 2016/5/3.
 */
public class SurfingHotNewsTopItemAdapter extends PagerAdapter {
    private Context mContext;
    private List<StandardInfo> mItemInfos;


    public SurfingHotNewsTopItemAdapter(Context mContext, List<StandardInfo> iconItemInfos) {
        this.mContext = mContext;
        this.mItemInfos = iconItemInfos;
        for (int i = 0; mItemInfos != null && i < mItemInfos.size(); i++) {
            StandardInfo adInfo = mItemInfos.get(i);
            if (adInfo.getType() == StandardInfo.TYPE_WEB) {
                Stats.reportAdv(adInfo.getAdId(), Stats.REPORT_TYPE_EXTERNAL_SHOW, adInfo.getPosition(), 41);
            } else if (adInfo.getType() == StandardInfo.TYPE_APK) {
                Stats.reportWallAppStats(adInfo.getAdId(), adInfo.getDescription(), Stats.REPORT_TYPE_SHOW, adInfo.getPosition(), 42);
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final StandardInfo adInfo = mItemInfos.get(position);
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(imageView);
        Picasso.with(mContext).load(adInfo.getPicUrl()).placeholder(R.mipmap.img_news_default).error(R.mipmap.img_news_default).config(Bitmap.Config.RGB_565).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adInfo.getPosition() == StandardInfo.RECOMMAND) {
                    Stats.event(mContext, "wk_wifi_info_recommand_click");
                }else if(adInfo.getPosition() == StandardInfo.BANNER){
                    Stats.event(mContext, "wk_found_banner_click");
                }
                if (adInfo.getType() == StandardInfo.TYPE_WEB) {
                    Stats.reportAdv(adInfo.getAdId(), Stats.REPORT_TYPE_CLICK, adInfo.getPosition(), 41);
                    WebHelper.launchWeb(mContext, adInfo.getName(), adInfo.getDesUrl(), false, new WebHelper.SimpleWebLoadCallBack() {
                        @Override
                        public void loadComplete(String url) {
                            Stats.reportAdv(adInfo.getAdId(), Stats.REPORT_TYPE_SHOW, adInfo.getPosition(), 41);
                        }
                    });
                }
                if (adInfo.getType() == StandardInfo.TYPE_APK) {
                    Stats.reportWallAppStats(adInfo.getAdId(), adInfo.getDescription(), Stats.REPORT_TYPE_CLICK, adInfo.getPosition(), 42);
                    DownloadUtil.downloadApk(mContext, StandardInfo.convertInfo(adInfo, new BannerHotReport())
                            , DownloadManager.DOWNLOAD_STRATERY_SERVICE, new NotificationDownloadFeedback(mContext));
                }
            }
        });
        container.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        if (mItemInfos != null && mItemInfos.size() > 0) {
            return mItemInfos.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
