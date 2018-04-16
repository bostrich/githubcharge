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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hodanet.charge.R;
import com.hodanet.charge.fragment.PicFragment;
import com.hodanet.charge.info.pic.WallpaperInfo;

import java.util.List;

/**
 *
 */

public class WallpaperViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<WallpaperInfo> mList;
    private OnItemClickListener mOnItemClickListener;

    public WallpaperViewPagerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public WallpaperViewPagerAdapter(Context context, List<WallpaperInfo> list, OnItemClickListener onItemClickListener) {
        mContext = context;
        mList = list;
        mOnItemClickListener = onItemClickListener;
    }

    public void setmList(List<WallpaperInfo> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView wallpaperView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_wallpaper_page, container,false);
        String imgUrl = mList.get(position).getPic();
        if (!TextUtils.isEmpty(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = PicFragment.DOMAIN_DEFAULT + imgUrl;
        }

        loadImg(position, wallpaperView, imgUrl);
        container.addView(wallpaperView);

        return wallpaperView;
    }

    private void loadImg(final int position, final ImageView wallpaperView, final String imgUrl) {
        try {
            AnimationDrawable animPlaceholder = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.loading_animated_drawable);
            animPlaceholder.start(); // probably needed

            Glide.with(mContext)
                    .load(imgUrl)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .override(EventConstants.SCREEN_WIDTH,EventConstants.SCREEN_HEIGHT)
                    .placeholder(animPlaceholder)
                    .error(R.mipmap.ic_load_img_error)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            wallpaperView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadImg(position, wallpaperView, imgUrl);
                                }
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            wallpaperView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(position);
                                    }
                                }
                            });
                            return false;
                        }
                    })
                    .into(wallpaperView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object == null) return;
        View wallpaper = (View) object;
        Glide.clear(wallpaper);
        container.removeView(wallpaper);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
