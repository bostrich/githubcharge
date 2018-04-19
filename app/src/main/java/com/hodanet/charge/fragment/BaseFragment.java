package com.hodanet.charge.fragment;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 *
 */

public class BaseFragment extends Fragment{
    public final String TAG = this.getClass().getName();

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(TAG);
    }
}
