package com.hodanet.charge.adapter.hot;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.config.EventConstants;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.Constants;
import com.hodanet.charge.info.hot.NewsInfo;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.WebHelper;
import com.hodanet.charge.utils.WebLaunchUtil;
import com.hodanet.charge.view.InfiniteLoopViewPager;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 */
public class SurfingHotNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NEWS_TYPE_BIG_PIC = 2;

    public static final int NEWS_TYPE_SMALL_PIC = 3;

    public static final int NEWS_TYPE_SIMPLE_TEXT = 4;

    public static final int NEWS_TYPE_PIC_TEXT = 1;

    public static final int NEWS_TYPE_TOP_BANNER = -100000;

    private Context mContext;
    private List<NewsInfo> mNewsInfos;
    private List<StandardInfo> mBannerAdInfos;


    private int screenWidth;

    public SurfingHotNewsAdapter(Context context, List<NewsInfo> list, List<StandardInfo> bannerAdInfos) {
        this.mContext = context;
        this.mNewsInfos = list;
        this.mBannerAdInfos = bannerAdInfos;
        screenWidth = ScreenUtil.getScreenWidth(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case NEWS_TYPE_BIG_PIC:
                return new BigPicItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wifi_news_bigpic, parent, false));
            case NEWS_TYPE_PIC_TEXT:
                return new PicTextItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wifi_news, parent, false));
            case NEWS_TYPE_SIMPLE_TEXT:
                return new SimpleTextItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wifi_news_simple_text, parent, false));
            case NEWS_TYPE_SMALL_PIC:
                return new SmallPicItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wifi_hot_news, parent, false));
            case NEWS_TYPE_TOP_BANNER:
                return new TopItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_surfing_top_view_pager, parent, false));
            default:
                break;
        }
        return new DefaultItemViewHolder(new View(mContext));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int po = position;
        if (holder != null) {
            final NewsInfo newsInfo = mNewsInfos.get(position);
            if (holder instanceof BigPicItemViewHolder) {
                ((BigPicItemViewHolder) holder).tvNewsContent.setText(newsInfo.name);
                if (newsInfo.imgs != null) {
                    String[] imgs = newsInfo.imgs.split("\\|");
                    if (imgs != null && imgs.length > 0) {
                        Picasso.with(mContext).load(imgs[0]).placeholder(R.mipmap.img_news_default).into(((BigPicItemViewHolder) holder).imgBigPic);
                    }
                }
                if (newsInfo.type == 0) {
                    ((BigPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.VISIBLE);
                    ((BigPicItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                } else {
                    ((BigPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.GONE);
                    ((BigPicItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                    ((BigPicItemViewHolder) holder).tvAuthor.setText(newsInfo.author);
                }
                ((BigPicItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo, po);
                    }
                });
            } else if (holder instanceof SmallPicItemViewHolder) {
                ((SmallPicItemViewHolder) holder).tvNewsContent.setText(newsInfo.name);
                if (newsInfo.imgs != null) {
                    String[] imgs = newsInfo.imgs.split("\\|");
                    if (imgs != null && imgs.length > 2) {
                        Picasso.with(mContext).load(imgs[0]).placeholder(R.mipmap.img_news_default).into(((SmallPicItemViewHolder) holder).imgLeft);
                        Picasso.with(mContext).load(imgs[1]).placeholder(R.mipmap.img_news_default).into(((SmallPicItemViewHolder) holder).imgCenter);
                        Picasso.with(mContext).load(imgs[2]).placeholder(R.mipmap.img_news_default).into(((SmallPicItemViewHolder) holder).imgRight);
                    }
                }
                if (newsInfo.type == 0) {
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.VISIBLE);
                } else {
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.GONE);
                }
                if(newsInfo.author.contains("众联广告")){
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.VISIBLE);
                    ((SmallPicItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                }else{
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.GONE);
                    ((SmallPicItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                }
                ((SmallPicItemViewHolder) holder).tvAuthor.setText(newsInfo.author);
                ((SmallPicItemViewHolder) holder).tvDate.setVisibility(View.VISIBLE);
                ((SmallPicItemViewHolder) holder).tvDate.setText(newsInfo.date);
                ((SmallPicItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo,po);
                    }
                });
            } else if (holder instanceof SimpleTextItemViewHolder) {
                ((SimpleTextItemViewHolder) holder).tvNewsContent.setText(newsInfo.name);
                ((SimpleTextItemViewHolder) holder).tvNewsDate.setText(newsInfo.date);
                ((SimpleTextItemViewHolder) holder).tvNewsSource.setText(newsInfo.source);
                ((SimpleTextItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo,po);
                    }
                });
            } else if (holder instanceof PicTextItemViewHolder) {
                ((PicTextItemViewHolder) holder).tvNewsContent.setText(newsInfo.name);
                Picasso.with(mContext).load(newsInfo.picUrl).placeholder(R.mipmap.img_news_default).into(((PicTextItemViewHolder) holder).imgPic);
                if (newsInfo.type == 0) {
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.VISIBLE);
                } else {
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.GONE);
                }
                if(newsInfo.author.contains("众联广告")){
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.VISIBLE);
                    ((PicTextItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                }else{
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.GONE);
                    ((PicTextItemViewHolder) holder).tvAuthor.setText(newsInfo.author);
                    ((PicTextItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                }
                ((PicTextItemViewHolder) holder).tvDate.setText(newsInfo.date);
                ((PicTextItemViewHolder) holder).tvDate.setVisibility(View.VISIBLE);
                ((PicTextItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, newsInfo,po);
                    }
                });
            } else if (holder instanceof TopItemViewHolder) {
                if (((TopItemViewHolder) holder).viewPager.getAdapter() != null) {
                    ((TopItemViewHolder) holder).viewPager.getAdapter().notifyDataSetChanged();
                } else {
                    SurfingHotNewsTopItemAdapter surfingHotNewsTopItemAdapter = new SurfingHotNewsTopItemAdapter(mContext, mBannerAdInfos);
                    InfiniteLoopViewPager.InfiniteLoopViewPagerAdapter adapter = new InfiniteLoopViewPager.InfiniteLoopViewPagerAdapter(surfingHotNewsTopItemAdapter);
                    ((TopItemViewHolder) holder).viewPager.setInfinateAdapter(adapter);
                }
                ((TopItemViewHolder) holder).layoutDot.removeAllViews();
                //创建底部指示位置的导航栏
                final ImageView[] mBottomImages;
                int count = mBannerAdInfos != null ? mBannerAdInfos.size() : 0;
                if (count <= 1) {
                    ((TopItemViewHolder) holder).layoutDot.setVisibility(View.GONE);
                } else {
                    mBottomImages = new ImageView[count];
                    for (int i = 0; i < count; i++) {
                        ImageView imageView = new ImageView(mContext);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(12, 12);
                        params.setMargins(5, 0, 5, 0);
                        imageView.setLayoutParams(params);
                        if (i == 0) {
                            imageView.setBackgroundResource(R.mipmap.ic_vip_banner_n);
                        } else {
                            imageView.setBackgroundResource(R.mipmap.ic_vip_banner_s);

                        }

                        mBottomImages[i] = imageView;
                        //把指示作用的原点图片加入底部的视图中
                        ((TopItemViewHolder) holder).layoutDot.addView(mBottomImages[i]);

                    }
                    ((TopItemViewHolder) holder).viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            int total = mBottomImages.length;
                            for (int j = 0; j < total; j++) {
                                if (j == position % total) {
                                    mBottomImages[j].setBackgroundResource(R.mipmap.ic_vip_banner_n);
                                } else {
                                    mBottomImages[j].setBackgroundResource(R.mipmap.ic_vip_banner_s);
                                }
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return mNewsInfos == null ? 0 : mNewsInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mNewsInfos.get(position).infoType;
    }

    private void goToWeb(Context context,final NewsInfo newsInfo, int position) {
        if (newsInfo != null) {
            final int po;
            if (newsInfo.type == 0) {
                if(position <= 3){
                    po = 1;
                }else if(position <= 6){
                    po = 2;
                }else{
                    po = 3;
                }
                Stats.event(context, "wk_found_promote_position" + po + "_click");
                Stats.reportAdv((int) newsInfo.id, Stats.REPORT_TYPE_CLICK, Stats.ADV_TYPE_CONNECT, Stats.SURFING_NEWS_TUIGUANG);
                WebLaunchUtil.launchWeb(context,newsInfo.name,newsInfo.clickUrl,false, new WebHelper.SimpleWebLoadCallBack(){
                    @Override
                    public void loadComplete(String url) {
                        Stats.event(mContext, "wk_found_promote_position" + po + "_show");
                        Stats.reportAdv((int) newsInfo.id, Stats.REPORT_TYPE_SHOW, Stats.ADV_TYPE_CONNECT, Stats.SURFING_NEWS_TUIGUANG);
                    }
                });

            } else{
                switch(newsInfo.type){
                    case Constants.NEWS_SOURCE_KAIJIA:
                        Stats.eventToYoumeng(context, EventConstants.UMENG_ID_NEWS, EventConstants.EVENT_CLICK
                                , EventConstants.NEWS_SOURCE_KEIJIA);
                        break;
                    case Constants.NEWS_SOURCE_COMPANION:
                        Stats.eventToYoumeng(context, EventConstants.UMENG_ID_NEWS, EventConstants.EVENT_CLICK
                                , EventConstants.NEWS_SOURCE_COMPANION);
                        break;
                    default:
                        break;
                }
//                Stats.event(context, "wk_found_news_click");
                WebHelper.launcheNewsWeb(context,newsInfo.name,newsInfo.clickUrl,false, new WebHelper.SimpleWebLoadCallBack(){
                    @Override
                    public void loadComplete(String url) {
                        Stats.event(mContext, "wk_found_news_show");
                    }
                });
            }

        }

    }


    class BigPicItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgBigPic;
        TextView tvNewsTuiGuang;
        TextView tvAuthor;

        public BigPicItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgBigPic = (ImageView) itemView.findViewById(R.id.item_news_img);
            ScreenUtil.adapterScreen(imgBigPic, screenWidth, (float) 0.42);
            tvNewsTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
        }
    }

    class SmallPicItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgLeft, imgCenter, imgRight;
        TextView tvNewsTuiGuang;
        TextView tvAuthor;
        TextView tvDate;

        public SmallPicItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            int width = (screenWidth - ScreenUtil.dipTopx(mContext, 40)) / 3;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgLeft = (ImageView) itemView.findViewById(R.id.news_image_left);
            imgCenter = (ImageView) itemView.findViewById(R.id.news_image_center);
            imgRight = (ImageView) itemView.findViewById(R.id.news_image_right);
            tvNewsTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
            tvDate = (TextView) itemView.findViewById(R.id.item_news_date);
            ScreenUtil.adapterScreen(imgLeft, width, (float) 0.75);
            ScreenUtil.adapterScreen(imgCenter, width, (float) 0.75);
            ScreenUtil.adapterScreen(imgRight, width, (float) 0.75);
        }
    }

    class SimpleTextItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        TextView tvNewsDate;
        TextView tvNewsSource;

        public SimpleTextItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            tvNewsDate = (TextView) itemView.findViewById(R.id.item_news_date);
            tvNewsSource = (TextView) itemView.findViewById(R.id.item_news_source);
        }
    }

    class PicTextItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgPic;
        TextView tvTuiGuang;
        TextView tvAuthor;
        TextView tvDate;

        public PicTextItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgPic = (ImageView) itemView.findViewById(R.id.item_news_img);
            tvTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
            tvDate = (TextView) itemView.findViewById(R.id.item_news_date);
            int width = (screenWidth - ScreenUtil.dipTopx(mContext, 40)) / 3;
            ScreenUtil.adapterScreen(imgPic, width, (float) 0.75);
        }
    }

    class TopItemViewHolder extends RecyclerView.ViewHolder {
        InfiniteLoopViewPager viewPager;
        LinearLayout layoutDot;

        public TopItemViewHolder(View itemView) {
            super(itemView);
            viewPager = (InfiniteLoopViewPager) itemView.findViewById(R.id.viewPager);
            ScreenUtil.adapterScreen(viewPager, ScreenUtil.getScreenWidth(itemView.getContext()), (float) (200.0 / 720.0));
            layoutDot = (LinearLayout) itemView.findViewById(R.id.layout_dot);

        }
    }

    class DefaultItemViewHolder extends RecyclerView.ViewHolder {
        public DefaultItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
