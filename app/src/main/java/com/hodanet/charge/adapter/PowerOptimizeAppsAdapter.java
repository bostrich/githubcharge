package com.hodanet.charge.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hodanet.charge.R;
import com.hodanet.charge.info.power_optimize.AppInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/12/12.
 */
public class PowerOptimizeAppsAdapter extends MyBaseAdapter<AppInfo> {
    public PowerOptimizeAppsAdapter(List<AppInfo> list, Context mContext) {
        super(list, mContext);
    }

    public PowerOptimizeAppsAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_one_key, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mIvIcon.setImageDrawable(mList.get(position).getAppIcon());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
