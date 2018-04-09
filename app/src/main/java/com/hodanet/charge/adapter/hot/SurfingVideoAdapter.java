package com.hodanet.charge.adapter.hot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.activity.VideoActivity;
import com.hodanet.charge.info.hot.VideoInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by June on 2016/8/10.
 */
public class SurfingVideoAdapter extends RecyclerView.Adapter<SurfingVideoAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoInfo> mList;

    public SurfingVideoAdapter(Context context, List<VideoInfo> videoInfos) {
        this.context = context;
        this.mList = videoInfos;
    }

    @Override
    public SurfingVideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(SurfingVideoAdapter.VideoViewHolder holder, final int position) {
        final VideoInfo videoInfo = mList.get(position);
        holder.text.setText(mList.get(position).getName());
        Picasso.with(context).load(mList.get(position).getPic()).into(holder.image);
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, VideoActivity.class).putExtra(VideoActivity.URL_ADDRESS, videoInfo.getUrl()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView text;
        ImageView image;

        public VideoViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            text = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
