package com.hodanet.charge.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.adapter.NewHotRecommendAdapter;
import com.hodanet.charge.info.RecommandModel;
import com.hodanet.charge.info.RecommendInfo;
import com.hodanet.charge.info.report.BaseReportInfo;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.StringUtils;
import com.hodanet.charge.utils.TaskManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import freemarker.template.utility.StringUtil;

/**
 *
 */

public class RecommendModelView {

    private final int MSG_HOTRECOMMEND_SUCCESS = 1;
    private static final int VIEW_PAGER_HANDLER = 2;

    private Context mContext;
    private BaseReportInfo reportInfo;
    private AdLoadSuccessListener adLoadSuccessListener;
    private List<RecommendInfo> hotList = null;
    private RecommendInfo mHotRecommendInfo = new RecommendInfo();
    private BitmapDrawable drawable_tem;

    private View recommendView;
    private ViewPager mViewPager;
    private NewHotRecommendAdapter mViewPagerAdapter;

    private int BANNER_POSITION = 0;

    private Handler mHandler;

    public RecommendModelView(Context context, BaseReportInfo reportInfo) {
        this.mContext = context;
        this.reportInfo = reportInfo;
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_HOTRECOMMEND_SUCCESS:
                        showAdView();
                        break;
                    case VIEW_PAGER_HANDLER:
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        mHandler.sendEmptyMessageDelayed(VIEW_PAGER_HANDLER, 3300);
                        break;
                }
            }
        };
    }

    public void getHotRecommendAd(final Context context, final AdLoadSuccessListener adLoadSuccessListener) {
        this.adLoadSuccessListener = adLoadSuccessListener;
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    hotList = new ArrayList<RecommendInfo>();
                    //去除已安装的app
                    List<RecommendInfo> infos = new RecommandModel.Builder().build().getInfos(context,reportInfo);
                    for (int i = 0; i < infos.size(); i++) {
                        RecommendInfo info = infos.get(i);
                        if(!DownloadUtil.checkInstall(context, info.getPkgName())){
                            hotList.add(info);
                        }
                    }
                    if (hotList.size() > 0) {
                        mHotRecommendInfo = hotList.get(0);
                        if (StringUtils.isNotBlank(mHotRecommendInfo.getBigPictureUrl())) {
                            try {
                                Bitmap bmp = Picasso.with(context).load(mHotRecommendInfo.getIconUrl()).get();
                                drawable_tem = new BitmapDrawable(context.getResources(), bmp);
                            } catch (Exception e) {

                            }
                        }
                        mHandler.sendEmptyMessage(MSG_HOTRECOMMEND_SUCCESS);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 取消为您推荐轮播
     */
    public void cancelViewPagerHandler() {
        mHandler.removeMessages(VIEW_PAGER_HANDLER);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 展示
     */
    private void showAdView() {
        if (hotList != null) {
            recommendView = LayoutInflater.from(mContext).inflate(R.layout.item_recommend, null);
            mViewPager = (ViewPager) recommendView.findViewById(R.id.view_pager);
            ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
            int width1 = mContext.getResources().getDisplayMetrics().widthPixels;
            layoutParams.height = (int) (width1 * 258.0f / 639);
            mViewPager.setLayoutParams(layoutParams);
            final TextView tvName = (TextView) recommendView.findViewById(R.id.optimize_app_name);
            final TextView tvDownload = (TextView) recommendView.findViewById(R.id.optimize_app_download);
            final TextView tvSlogan = (TextView) recommendView.findViewById(R.id.optimize_app_slogan);
            Button btnBack = (Button) recommendView.findViewById(R.id.back);
            final ImageView icon = (ImageView) recommendView.findViewById(R.id.optimize_app_icon);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            });

            ImageView post = (ImageView) recommendView.findViewById(R.id.optimize_app_pic);
            LinearLayout.LayoutParams postLp = (LinearLayout.LayoutParams) post.getLayoutParams();
            if (drawable_tem != null) {
                int width = drawable_tem.getIntrinsicWidth();
                int height = drawable_tem.getIntrinsicHeight();
                postLp.width = ScreenUtil.getScreenWidth(mContext) - ScreenUtil.dipTopx(mContext, 36);
                postLp.height = postLp.width * height / width;
                post.setLayoutParams(postLp);
                Picasso.with(mContext).load(mHotRecommendInfo.getIconUrl()).into(icon);
                mViewPagerAdapter = new NewHotRecommendAdapter(hotList, mContext);//修改构造方法传入接口便于点击回调
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % hotList.size()));
                //3秒定时
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {

                        BANNER_POSITION = position % hotList.size();
                        RecommendInfo info = hotList.get(position % hotList.size());
                        if (info != null) {
                            tvName.setText(hotList.get(position % hotList.size()).getName());
                            Picasso.with(mContext).load(hotList.get(position % hotList.size()).getIconUrl()).into(icon);
                            tvSlogan.setText(hotList.get(position % hotList.size()).getSlogan());
                            String buttonStr = hotList.get(position % hotList.size()).getButtonName();
                            if (buttonStr == null || buttonStr.length() == 0) {
                                buttonStr = "打开";
                            }
                            tvDownload.setText(buttonStr);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mHandler.sendEmptyMessageDelayed(VIEW_PAGER_HANDLER, 3300);
                post.setImageDrawable(drawable_tem);
            }
            tvName.setText(mHotRecommendInfo.getName());
            tvSlogan.setText(mHotRecommendInfo.getSlogan());
            tvDownload.setText("下载");
            if (DownloadUtil.checkDownLoad(mContext, mHotRecommendInfo.getName())) {
                tvDownload.setText("安装");
            }
            if (mHotRecommendInfo.getButtonName() != null && mHotRecommendInfo.getButtonName().length() != 0) {
                tvDownload.setText(mHotRecommendInfo.getButtonName());
            }
            tvDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hotList.size() > BANNER_POSITION) {
                        RecommendInfo adInfo = hotList.get(BANNER_POSITION);
                        adInfo.click(mContext);
                        if (adLoadSuccessListener != null) {
                            adLoadSuccessListener.downloadClick(adInfo);
                        }
                    }
                }
            });
            if (this.adLoadSuccessListener != null) {
                this.adLoadSuccessListener.loadSuccess(recommendView);
            }
        }
    }

    public interface AdLoadSuccessListener {
        void loadSuccess(View view);
        void downloadClick(Object obj);
    }


}
