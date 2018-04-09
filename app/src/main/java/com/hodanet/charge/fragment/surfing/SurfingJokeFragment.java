package com.hodanet.charge.fragment.surfing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hodanet.charge.R;
import com.hodanet.charge.adapter.hot.JokeWithAdAdapter;
import com.hodanet.charge.info.hot.JokeAdInfo;
import com.hodanet.charge.minterface.GetNewJokerInfoListener;
import com.hodanet.charge.model.hot.NewJokeModel;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.view.refreshview.RefreshRecyclerView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by June on 2016/8/2.
 */
public class SurfingJokeFragment extends BaseFragment implements View.OnClickListener {

    public static final String ID = "id";
    public static final int INIT_DAT = 10;
    public static final int REFRESH_DATA = 11;
    public static final int LOAD_MORE_DATA = 12;
    private static final int MES_GET_JOKE_INFO_OK = 1;
    private static final int MES_GET_JOKE_INFO_FAIL = 2;
    private static SurfingJokeFragment fragment;
    private String mId = "";
    private ViewSwitcher mViewSwitcherJokeLoadError;
    private ViewSwitcher mViewSwitcherJokeLoading;
    private RefreshRecyclerView mRefreshRecyclerView;
    private TextView mTvReLoad;
    private JokeWithAdAdapter jokeWithAdAdapter;
    private List<JokeAdInfo> mList = new ArrayList<>();
    private ImageView img_loading_rotation;

    private boolean mIsLoadData = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MES_GET_JOKE_INFO_OK:
                    mViewSwitcherJokeLoadError.setDisplayedChild(0);
                    mViewSwitcherJokeLoading.setDisplayedChild(0);
                    img_loading_rotation.clearAnimation();
                    List list = (List) msg.obj;
                    int type = msg.arg1;
                    if (type == INIT_DAT || type == REFRESH_DATA) {
                        mList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            mList.add((JokeAdInfo) list.get(i));
                        }
                        jokeWithAdAdapter = new JokeWithAdAdapter(getContext(), mList);
                        mRefreshRecyclerView.setAdapter(jokeWithAdAdapter);
                        mRefreshRecyclerView.refreshComplete();
                        mIsLoadData = true;
                    } else if (type == LOAD_MORE_DATA) {
                        List loadMoreData = (List) msg.obj;
                        if (mList != null && loadMoreData != null) {
                            mList.addAll(loadMoreData);
                            mRefreshRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                        mRefreshRecyclerView.loadMoreComplete();
                    }
                    break;
                case MES_GET_JOKE_INFO_FAIL:
                    mRefreshRecyclerView.reset();
                    mViewSwitcherJokeLoadError.setDisplayedChild(1);
                    break;
            }
        }
    };

    public static SurfingJokeFragment getInstance() {
        if (fragment == null) {
            fragment = new SurfingJokeFragment();
        }
        return fragment;
    }

    public static SurfingJokeFragment newInstance(String id) {
        SurfingJokeFragment fragment = new SurfingJokeFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }


    public static SurfingJokeFragment newInstance() {
        SurfingJokeFragment fragment = new SurfingJokeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ID);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
//        Stats.event(getActivity(), "SURFING_NAVIGATION_CLICK_" + mId);
        MobclickAgent.onPageStart("found_joke");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_new_joke, container, false);
            initViews(contentView);
            isPrepared = true;
            loadData();
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }


    private void initViews(View v) {
        mViewSwitcherJokeLoadError = (ViewSwitcher) v.findViewById(R.id.viewSwitcherSurfingJoke);
        mViewSwitcherJokeLoading = (ViewSwitcher) v.findViewById(R.id.viewSwitcherSurfingJokeLoading);
        mRefreshRecyclerView = (RefreshRecyclerView) v.findViewById(R.id.refreshRecyclerViewJoke);
        mRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRefreshRecyclerView.setLoadingListener(new JokeRefreshLoadListener());
        mTvReLoad = (TextView) v.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
        img_loading_rotation = (ImageView) v.findViewById(R.id.img_rotation);
    }

    private void getData(final int type) {
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                NewJokeModel.getInstance().getJokeInfos(type, new GetNewJokerInfoListener() {
                    @Override
                    public void getInfoSucceed(int type, List list) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = MES_GET_JOKE_INFO_OK;
                        msg.arg1 = type;
                        msg.obj = list;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void getInfoFailed(String error) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = MES_GET_JOKE_INFO_FAIL;
                        msg.obj = error;
                        mHandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isVisible) {
            return;
        } else {
            if (!mIsLoadData) {
                mViewSwitcherJokeLoadError.setDisplayedChild(0);
                mViewSwitcherJokeLoading.setDisplayedChild(1);
                img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
                getData(INIT_DAT);
            } else {
//                mRefreshRecyclerView.setRefreshing(true);
            }
        }
    }

    @Override
    protected void onInvisible() {
        MobclickAgent.onPageEnd("found_joke");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReload:
                loadData();
                break;
            default:
                break;
        }
    }

    private class JokeRefreshLoadListener implements RefreshRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            getData(REFRESH_DATA);
        }

        @Override
        public void onLoadMore() {
            getData(LOAD_MORE_DATA);
        }
    }
}
