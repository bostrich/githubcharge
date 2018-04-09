package com.hodanet.charge.adapter.hot;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.activity.WebQQActivity;
import com.hodanet.charge.info.hot.AdInfo;
import com.hodanet.charge.info.hot.JokeAdInfo;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.view.VerticalImageSpan;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/26.
 */
public class JokeWithAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int JOKE_TYPE_BIG_PIC = 2;

    public static final int JOKE_TYPE_SMALL_PIC = 3;

    public static final int JOKE_TYPE_SIMPLE_TEXT = 4;

    public static final int JOKE_TYPE_PIC_TEXT = 1;

    public static final int JOKE_TYPE_NORMAL = -1;

    public static final int JOKE_TYPE_APP = -2;


    private Context mContext;
    private List<JokeAdInfo> mJokeInfos;

    private int screenWidth;

    public JokeWithAdAdapter(Context context, List<JokeAdInfo> list) {
        this.mContext = context;
        this.mJokeInfos = list;
        screenWidth = ScreenUtil.getScreenWidth(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case JOKE_TYPE_BIG_PIC:
                return new BigPicItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_joke_news_ad_bigpic, parent, false));
            case JOKE_TYPE_PIC_TEXT:
                return new PicTextItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_joke_news_ad_normal, parent, false));
            case JOKE_TYPE_SIMPLE_TEXT:
                return new SimpleTextItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_joke_news_ad_simple_text, parent, false));
            case JOKE_TYPE_SMALL_PIC:
                return new SmallPicItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_joke_news_ad_hot, parent, false));
            case JOKE_TYPE_APP:
                return new JokeAPPItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_joke_app_ad, parent, false));
            case JOKE_TYPE_NORMAL:
                return new JokeNormalItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_joke_normal_text, parent, false));
            default:
                break;
        }
        return new DefaultItemViewHolder(new View(mContext));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            final JokeAdInfo jokeAdInfo = mJokeInfos.get(position);
            if (holder instanceof BigPicItemViewHolder) {
                ((BigPicItemViewHolder) holder).tvNewsContent.setText(jokeAdInfo.name);
                if (jokeAdInfo.imgs != null) {
                    String[] imgs = jokeAdInfo.imgs.split("\\|");
                    if (imgs != null && imgs.length > 0) {
                        Picasso.with(mContext).load(imgs[0]).placeholder(R.mipmap.img_news_default).into(((BigPicItemViewHolder) holder).imgBigPic);
                    }
                }
                if (jokeAdInfo.type == 0) {
                    ((BigPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.VISIBLE);
                    ((BigPicItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                } else {
                    ((BigPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.GONE);
                    ((BigPicItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                }
                ((BigPicItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, jokeAdInfo);
                    }
                });
            } else if (holder instanceof SmallPicItemViewHolder) {
                ((SmallPicItemViewHolder) holder).tvNewsContent.setText(jokeAdInfo.name);
                if (jokeAdInfo.imgs != null) {
                    String[] imgs = jokeAdInfo.imgs.split("\\|");
                    if (imgs != null && imgs.length > 2) {
                        Picasso.with(mContext).load(imgs[0]).placeholder(R.mipmap.img_news_default).into(((SmallPicItemViewHolder) holder).imgLeft);
                        Picasso.with(mContext).load(imgs[1]).placeholder(R.mipmap.img_news_default).into(((SmallPicItemViewHolder) holder).imgCenter);
                        Picasso.with(mContext).load(imgs[2]).placeholder(R.mipmap.img_news_default).into(((SmallPicItemViewHolder) holder).imgRight);
                    }
                }
                if (jokeAdInfo.type == 0) {
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.VISIBLE);
                    ((SmallPicItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);
                } else {
                    ((SmallPicItemViewHolder) holder).tvNewsTuiGuang.setVisibility(View.GONE);
                    ((SmallPicItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                }
                ((SmallPicItemViewHolder) holder).tvNewsContent.setText(jokeAdInfo.advNmae);
                ((SmallPicItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, jokeAdInfo);
                    }
                });
            } else if (holder instanceof SimpleTextItemViewHolder) {
                ((SimpleTextItemViewHolder) holder).tvNewsContent.setText(jokeAdInfo.advNmae);
                ((SimpleTextItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, jokeAdInfo);
                    }
                });
            } else if (holder instanceof PicTextItemViewHolder) {
                ((PicTextItemViewHolder) holder).tvNewsContent.setText(jokeAdInfo.advNmae);
                Picasso.with(mContext).load(jokeAdInfo.picture).placeholder(R.mipmap.img_news_default).into(((PicTextItemViewHolder) holder).imgPic);
                if (jokeAdInfo.type == 0) {
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.VISIBLE);
                    ((PicTextItemViewHolder) holder).tvAuthor.setVisibility(View.GONE);

                } else {
                    ((PicTextItemViewHolder) holder).tvAuthor.setVisibility(View.VISIBLE);
                    ((PicTextItemViewHolder) holder).tvTuiGuang.setVisibility(View.GONE);
                }
                ((PicTextItemViewHolder) holder).contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToWeb(mContext, jokeAdInfo);
                    }
                });
            } else if (holder instanceof JokeNormalItemViewHolder) {
                String content = "   ☆  " + jokeAdInfo.content;
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
                String rexgString = "☆";
                Pattern pattern = Pattern.compile(rexgString);
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.joke_tip);
                    if (drawable != null) {
                        drawable.setBounds(0, 0, ScreenUtil.dipTopx(mContext, 9), ScreenUtil.dipTopx(mContext, 9));
                    }
                    stringBuilder.setSpan(new VerticalImageSpan(drawable), matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                ((JokeNormalItemViewHolder) holder).jokeContent.setText(stringBuilder);
            } else if (holder instanceof JokeAPPItemViewHolder) {
                Picasso.with(mContext).load(jokeAdInfo.picture).placeholder(R.mipmap.img_news_default).into(((JokeAPPItemViewHolder) holder).imgJokeAppBanner);
                ((JokeAPPItemViewHolder) holder).imgJokeAppBanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdInfo adInfo = new AdInfo();
                        adInfo.pkgName = jokeAdInfo.pkgName;
                        adInfo.apkUrl = jokeAdInfo.url;
                        adInfo.appName = jokeAdInfo.advNmae;
//                        AdDownloadManage.startDownload(mContext, adInfo, -1, -1);
                    }
                });
            } else {
                //没有匹配的 什么也不做
            }

        }
    }

    @Override
    public int getItemCount() {
        return mJokeInfos == null ? 0 : mJokeInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        JokeAdInfo jokeAdInfo = mJokeInfos.get(position);
        switch (jokeAdInfo.type) {
            case 0:
                return jokeAdInfo.infoType;
            case 2:
                return JOKE_TYPE_APP;
            case 1:
                return JOKE_TYPE_NORMAL;
            default:
                return JOKE_TYPE_NORMAL;
        }
    }

    private void goToWeb(Context context, JokeAdInfo jokeAdInfo) {
        if (jokeAdInfo != null) {
            if (jokeAdInfo.type == 0) {
//                Stats.event(context, "SURFING_JOKE_NEWS_AD_CLICK_" + String.valueOf(jokeAdInfo.id));
//                Stats.reportAdv((int) jokeAdInfo.id, Stats.REPORT_TYPE_CLICK, Stats.ADV_TYPE_CONNECT, Stats.WIFICONNECTEDHOTACTIVITY_NEWS);
//                Stats.eventReportAdv(context, (int) jokeAdInfo.id, OptType.OP_TYPE_CLICK, OptType.ADV_TYPE_CONNECT);
            }
            Intent intent = new Intent(mContext, WebQQActivity.class);
            intent.putExtra("TITLE", jokeAdInfo.name);
            intent.putExtra("URL", jokeAdInfo.url);
            intent.putExtra("ADVID", (int) jokeAdInfo.id);
            intent.putExtra("TYPE", WebQQActivity.TYPE_CONNECTED);
            if (jokeAdInfo.type != 0) {
                intent.putExtra(WebQQActivity.WEB_SHOW_AD, true);
            } else {
                intent.putExtra(WebQQActivity.WEB_SHOW_AD, false);
            }
            context.startActivity(intent);
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
            ScreenUtil.adapterScreen(imgLeft, width, (float) 0.75);
            ScreenUtil.adapterScreen(imgCenter, width, (float) 0.75);
            ScreenUtil.adapterScreen(imgRight, width, (float) 0.75);
        }
    }

    class SimpleTextItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;

        public SimpleTextItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
        }
    }

    class PicTextItemViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView tvNewsContent;
        ImageView imgPic;
        TextView tvTuiGuang;
        TextView tvAuthor;

        public PicTextItemViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            tvNewsContent = (TextView) itemView.findViewById(R.id.item_news_content);
            imgPic = (ImageView) itemView.findViewById(R.id.item_news_img);
            tvTuiGuang = (TextView) itemView.findViewById(R.id.item_news_tuiguang);
            tvAuthor = (TextView) itemView.findViewById(R.id.item_news_source);
            int width = (screenWidth - ScreenUtil.dipTopx(mContext, 40)) / 3;
            ScreenUtil.adapterScreen(imgPic, width, (float) 0.75);
        }
    }

    class JokeNormalItemViewHolder extends RecyclerView.ViewHolder {
        TextView jokeContent;

        public JokeNormalItemViewHolder(View itemView) {
            super(itemView);
            jokeContent = (TextView) itemView.findViewById(R.id.content);
        }
    }

    class JokeAPPItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgJokeAppBanner;

        public JokeAPPItemViewHolder(View itemView) {
            super(itemView);
            imgJokeAppBanner = (ImageView) itemView.findViewById(R.id.imgJokeAppBanner);
        }
    }


    class DefaultItemViewHolder extends RecyclerView.ViewHolder {
        public DefaultItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
