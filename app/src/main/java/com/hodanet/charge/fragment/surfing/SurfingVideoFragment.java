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
import com.hodanet.charge.adapter.hot.SurfingVideoAdapter;
import com.hodanet.charge.info.hot.VideoInfo;
import com.hodanet.charge.minterface.GetInfoListener;
import com.hodanet.charge.model.hot.VideoModel;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.view.refreshview.RefreshRecyclerView;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by June on 2016/8/2.
 */
public class SurfingVideoFragment extends BaseFragment implements View.OnClickListener {

    public static final String ID = "id";
    public static final int INIT_DAT = 10;
    public static final int REFRESH_DATA = 11;
    public static final int LOAD_MORE_DATA = 12;
    private static final int GET_VIDEOINFO_SUCCEED = 1;
    private static final int GET_VIDEOINFO_FAILED = 2;
    private static final int ADD_DATA = 12;
    private static SurfingVideoFragment fragment;
    private String mId = "";
    private ViewSwitcher mViewSwitcherVideoError;
    private ViewSwitcher mViewSwitcherVideoLoading;
    private RefreshRecyclerView mRefreshRecyclerViewVideo;
    private TextView mTvReLoad;
    private TextView joke_update_tips;
    private ImageView img_loading_rotation;

    private SurfingVideoAdapter mSurfingVideoAdapter;
    private List<VideoInfo> mList = new ArrayList<>();
    private boolean mIsLoadData = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_VIDEOINFO_SUCCEED:
                    List list = (List) msg.obj;
                    int type = msg.arg1;
                    if (type == INIT_DAT || type == REFRESH_DATA) {
                        mList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            mList.add((VideoInfo) list.get(i));
                        }
                        mSurfingVideoAdapter.notifyDataSetChanged();
                        mIsLoadData = true;
                    }
                    if (type == LOAD_MORE_DATA) {
                        mList.addAll(list);
                        mSurfingVideoAdapter.notifyDataSetChanged();
                    }
                    displayVideoDataOkViw();
                    mSurfingVideoAdapter.notifyDataSetChanged();
                    mRefreshRecyclerViewVideo.reset();
                    break;
                case GET_VIDEOINFO_FAILED:
                    String error = (String) msg.obj;
                    mRefreshRecyclerViewVideo.reset();
                    LogUtil.e("joke", error);
                    displayVideoErrorView();
                    break;
            }
        }
    };

    public static SurfingVideoFragment getInstance() {
        if (fragment == null) {
            fragment = new SurfingVideoFragment();
        }
        return fragment;
    }

    public static SurfingVideoFragment newInstance() {
        SurfingVideoFragment fragment = new SurfingVideoFragment();
        return fragment;
    }

    public static SurfingVideoFragment newInstance(String id) {
        SurfingVideoFragment fragment = new SurfingVideoFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_video, container, false);
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
        mViewSwitcherVideoError = (ViewSwitcher) v.findViewById(R.id.viewSwitcherSurfingVideo);
        mViewSwitcherVideoLoading = (ViewSwitcher) v.findViewById(R.id.viewSwitcherSurfingVideoLoading);
        mTvReLoad = (TextView) v.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
        mRefreshRecyclerViewVideo = (RefreshRecyclerView) v.findViewById(R.id.refreshRecyclerViewVideo);
        mRefreshRecyclerViewVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        mRefreshRecyclerViewVideo.setLoadingListener(new VideoRefreshLoadListener());
        joke_update_tips = (TextView) v.findViewById(R.id.joke_update_tips);
        joke_update_tips.setVisibility(View.GONE);
        mSurfingVideoAdapter = new SurfingVideoAdapter(getContext(), mList);
        mRefreshRecyclerViewVideo.setAdapter(mSurfingVideoAdapter);
        img_loading_rotation = (ImageView) contentView.findViewById(R.id.img_rotation);
    }


    /**
     * 展示视频数据加载错误页面
     */
    private void displayVideoErrorView() {
        mViewSwitcherVideoError.setDisplayedChild(1);
    }

    /**
     * 展示视频数据加载页面
     */
    private void displayVideoLoadingView() {
        mViewSwitcherVideoError.setDisplayedChild(0);
        mViewSwitcherVideoLoading.setDisplayedChild(1);
        img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
    }

    /**
     * 展示视频数据加载正常页面
     */
    private void displayVideoDataOkViw() {
        mViewSwitcherVideoError.setDisplayedChild(0);
        mViewSwitcherVideoLoading.setDisplayedChild(0);
        img_loading_rotation.clearAnimation();
    }

    private void getData(final int type) {
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    VideoModel.getInstance().getVideoInfos(type, new GetInfoListener() {
                        @Override
                        public void getInfoSucceed(int type, List list) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = GET_VIDEOINFO_SUCCEED;
                            msg.arg1 = type;
                            msg.obj = list;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void getInfoFailed(String error) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = GET_VIDEOINFO_FAILED;
                            msg.obj = error;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReload:
                displayVideoLoadingView();
                getData(INIT_DAT);
                break;
            default:
                break;
        }
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isVisible) {
            return;
        } else {
            if (!mIsLoadData) {
                displayVideoLoadingView();
                getData(INIT_DAT);
            } else {
//                mRefreshRecyclerViewVideo.setRefreshing(true);
            }
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
//        Stats.event(getActivity(), "SURFING_NAVIGATION_CLICK_" + mId);
//        MobclickAgent.onPageStart("found_video");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
//        MobclickAgent.onPageEnd("found_video");
    }

    private class VideoRefreshLoadListener implements RefreshRecyclerView.LoadingListener {

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
