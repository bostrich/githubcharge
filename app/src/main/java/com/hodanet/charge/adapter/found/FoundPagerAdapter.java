package com.hodanet.charge.adapter.found;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.hodanet.charge.fragment.found.FoundAppFragment;
import com.hodanet.charge.fragment.found.FoundMainFragment;

import java.util.List;

/**
 *
 */

public class FoundPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;


    public FoundPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new FoundMainFragment();
        }else if(position == 1){
            return new FoundAppFragment();
        }
        return null;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String title = titles.get(position);
        title = title == null ? "" : title;
        return title;
    }
}
