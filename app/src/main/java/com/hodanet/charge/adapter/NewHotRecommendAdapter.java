package com.hodanet.charge.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hodanet.charge.R;
import com.hodanet.charge.info.RecommendInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 */

public class NewHotRecommendAdapter extends PagerAdapter{
    private List<RecommendInfo> mList;
    private Context mContext;

    public NewHotRecommendAdapter(List<RecommendInfo> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final RecommendInfo ad = mList.get(position % mList.size());
        View view = View.inflate(mContext, R.layout.adapter_ad, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.click(mContext);
            }
        });
        Picasso.with(mContext).load(ad.getBigPictureUrl()).placeholder(R.mipmap.img_news_default).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
