package com.hodanet.charge.fragment.surfing;

import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.reflect.Field;

/**
 *
 */

public abstract class BaseFragment extends Fragment {

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    /**
     * 标志位 判断fragment 是否初始化完毕
     */
    protected boolean isPrepared;


    protected View contentView;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 可见
     */
    protected void onVisible() {
        if (isPrepared) {
            loadData();
        }
    }


    /**
     * 不可见
     */
    protected void onInvisible() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void loadData();
}