package com.hodanet.charge.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hodanet.charge.R;
import com.hodanet.charge.info.pic.WallpaperClassifyBean;

import java.util.List;

/**
 *
 */

public class PicTypeViewPagerAdapter extends PagerAdapter {
    public static final String WALLPAPER_HOST = "http://res.ipingke.com";
    private Context mContext;
    private List<WallpaperClassifyBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public PicTypeViewPagerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public PicTypeViewPagerAdapter(Context mContext, List<WallpaperClassifyBean> mList, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.mList = mList;
        this.mOnItemClickListener = onItemClickListener;
    }


    public void setmList(List<WallpaperClassifyBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        String imgUrl = mList.get(position).getCover();
        if (!TextUtils.isEmpty(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = WALLPAPER_HOST + imgUrl;
        }
        ImageView view = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.pic_type, container,false);
        try {
            AnimationDrawable animPlaceholder = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.loading_animated_drawable);
            animPlaceholder.start(); // probably needed

            Glide.with(mContext)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(animPlaceholder)
                    .error(R.mipmap.ic_load_img_error)
                    .crossFade()
                    .into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object == null) return;
        View view = (View) object;
        Glide.clear(view);
        container.removeView(view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
