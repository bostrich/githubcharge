package com.hodanet.charge.adapter.hot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.hodanet.charge.fragment.surfing.SurfingBeautifulGirlsFragment;
import com.hodanet.charge.fragment.surfing.SurfingHotNewsFragment;
import com.hodanet.charge.fragment.surfing.SurfingJokeFragment;
import com.hodanet.charge.fragment.surfing.SurfingVideoFragment;
import com.hodanet.charge.fragment.surfing.SurfingWebFragment;
import com.hodanet.charge.info.hot.TabItemInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class SurfingPageFragmentAdapter extends FragmentStatePagerAdapter {

    private List<TabItemInfo> mTabItemInfos;

    public SurfingPageFragmentAdapter(FragmentManager fm, List<TabItemInfo> tabItemInfos) {
        super(fm);
        this.mTabItemInfos = tabItemInfos;
    }

    @Override
    public Fragment getItem(int position) {
        TabItemInfo tabItemInfo = mTabItemInfos.get(position);
        if (tabItemInfo.getInnerName().equals("news")) {
            return SurfingHotNewsFragment.newInstance(tabItemInfo.getId() + "");
        }
        if (tabItemInfo.getInnerName().equals("video")) {
            return SurfingVideoFragment.newInstance(tabItemInfo.getId() + "");
        }
        if (tabItemInfo.getInnerName().equals("joke")) {
            return SurfingJokeFragment.newInstance(tabItemInfo.getId() + "");
        }
        if (tabItemInfo.getInnerName().equals("girl")) {
            return SurfingBeautifulGirlsFragment.newInstance(tabItemInfo.getId() + "");
        }
        return SurfingWebFragment.newInstance(tabItemInfo.getClickAction(), tabItemInfo.getId() + "");
    }

    @Override
    public int getCount() {
        return mTabItemInfos == null ? 0 : mTabItemInfos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = mTabItemInfos.get(position).getText();
        title = title == null ? "" : title;
        return title;
    }
}
