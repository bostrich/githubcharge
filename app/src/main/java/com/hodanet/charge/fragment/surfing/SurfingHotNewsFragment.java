package com.hodanet.charge.fragment.surfing;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hodanet.charge.R;
import com.hodanet.charge.adapter.hot.SurfingHotNewsAdapter;
import com.hodanet.charge.config.AppConfig;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.DeviceConfig;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.hot.NewsInfo;
import com.hodanet.charge.utils.NetSpeedUtil;
import com.hodanet.charge.utils.ParseUtil;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.ToastUtil;
import com.hodanet.charge.view.refreshview.RefreshRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SurfingHotNewsFragment extends BaseFragment implements View.OnClickListener {

    public static final int MES_GET_NEWS_OK = 1;

    public static final int MES_GET_NEWS_FAIL = 2;

    public static final String ID = "id";

    public static volatile int companionPageNum;
    public static volatile int kaijiaPageNum;
    public static volatile int ttPageNum;

    private String rowkey  = "";

    private String mId = "";

    private ViewSwitcher mViewSwitcherNewsLoading;
    private ViewSwitcher mViewSwitcherNewsError;
    private OnFragmentInteractionListener mListener;
    private RefreshRecyclerView mRefreshRecyclerView;
    private TextView mTvReLoad;
    private List<NewsInfo> mNewsInfo = new ArrayList<>();
    private List<StandardInfo> mIconInfos;
    private SurfingHotNewsAdapter mNewsAdapter;
    private ImageView img_loading_rotation;
    private List<NewsInfo> listAll = new ArrayList<>();
    private TextView tv_refresh_tip;

    private boolean isFirstVisibleToUser = true;

    private boolean mIsLoadData;


    private Handler mDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MES_GET_NEWS_OK:
//                    mNewsInfo = (List<NewsInfo>) msg.obj;
                    listAll.clear();
                    listAll.addAll(mNewsInfo);
                    List<NewsInfo> list_tem = (List<NewsInfo>) msg.obj;
                    //移除顶部banner item
                    Iterator<NewsInfo> ite_all = listAll.iterator();
                    while (ite_all != null && ite_all.hasNext()) {
                        NewsInfo next = ite_all.next();
                        if (next.infoType == SurfingHotNewsAdapter.NEWS_TYPE_TOP_BANNER) {
                            ite_all.remove();
                        }
                    }
//                    Iterator<NewsInfo> iterator = list_tem.iterator();
//                    while(iterator.hasNext()){
//                        NewsInfo next = iterator.next();
//                        if(next.type == 1 || next.type == 3 ){
//                            for (int i = 0; i < listAll.size(); i++) {
//                                NewsInfo newsInfo = listAll.get(i);
//                                if(newsInfo.id == next.id){
//                                    iterator.remove();
//                                    break;
//                                }
//                            }
//                        }
//                    }


//                    Iterator<NewsInfo> ite_add = list_tem.iterator();
//                    while(ite_add.hasNext()){
//                        NewsInfo next = ite_add.next();
//                        listAll.add(0, next);
//                    }
                    for (int i = list_tem.size() -1 ; i >= 0; i--) {
                        listAll.add(0, list_tem.get(i));
                    }


                    mNewsInfo.clear();
                    mNewsInfo.addAll(listAll);

                    if (mNewsInfo != null && mNewsInfo.size() > 0) {
                        mIsLoadData = true;
                        displayNewsDataOkViw();
                        mNewsAdapter = new SurfingHotNewsAdapter(getContext(), mNewsInfo, mIconInfos);
                        mRefreshRecyclerView.setAdapter(mNewsAdapter);
                        if(mIconInfos != null && mIconInfos.size() > 0){
                            NewsInfo info = new NewsInfo();
                            info.infoType = SurfingHotNewsAdapter.NEWS_TYPE_TOP_BANNER;
                            mNewsInfo.add(0, info);
                        }
                        mRefreshRecyclerView.reset();
                        //tip动画
                        tv_refresh_tip.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams layoutParams = tv_refresh_tip.getLayoutParams();
                        layoutParams.height = (int)(DeviceConfig.SCREEN_DENSITY * 40 +0.5F);
                        tv_refresh_tip.setLayoutParams(layoutParams);
                        tv_refresh_tip.setText(String.format("已为您更新%d条数据", list_tem.size()));
                        tv_refresh_tip.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getContext() != null && tv_refresh_tip != null) {
                                    int height = ScreenUtil.dipTopx(getContext().getApplicationContext(), 40);
                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
                                    valueAnimator.setDuration(500);
                                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            ViewGroup.LayoutParams layoutParams = tv_refresh_tip.getLayoutParams();
                                            layoutParams.height = (int) animation.getAnimatedValue();
                                            tv_refresh_tip.setLayoutParams(layoutParams);
                                            if (animation.getAnimatedFraction() >= 1.0) {
                                                tv_refresh_tip.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    valueAnimator.start();
                                }
                            }
                        }, 1500);
                    } else {
                        mDataHandler.sendEmptyMessage(MES_GET_NEWS_FAIL);
                    }
                    break;
                case MES_GET_NEWS_FAIL:
                    mRefreshRecyclerView.reset();
                    displayNewsErrorView();
                    break;
                default:
                    break;
            }
        }
    };

    public SurfingHotNewsFragment() {
    }

    public static SurfingHotNewsFragment newInstance() {
        SurfingHotNewsFragment fragment = new SurfingHotNewsFragment();
        return fragment;
    }

    public static SurfingHotNewsFragment newInstance(String id) {
        SurfingHotNewsFragment fragment = new SurfingHotNewsFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_surfing_hot_news, container, false);
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

    private void initViews(View contentView) {
        mViewSwitcherNewsError = (ViewSwitcher) contentView.findViewById(R.id.viewSwitcherSurfingHotNewsError);
        mViewSwitcherNewsLoading = (ViewSwitcher) contentView.findViewById(R.id.viewSwitcherSurfingJokeLoading);
        mRefreshRecyclerView = (RefreshRecyclerView) contentView.findViewById(R.id.refreshRecyclerViewNews);
        mRefreshRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshRecyclerView.setLoadingListener(new RefreshLoadListener());
        mTvReLoad = (TextView) contentView.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
        img_loading_rotation = (ImageView) contentView.findViewById(R.id.img_rotation);

        tv_refresh_tip = (TextView) contentView.findViewById(R.id.tv_refresh_tip);
        tv_refresh_tip.setVisibility(View.GONE);
    }


    /**
     * 展示新闻数据加载错误页面
     */
    private void displayNewsErrorView() {
        mViewSwitcherNewsError.setDisplayedChild(1);
    }

    /**
     * 展示新闻数据加载页面
     */
    private void displayNewsLoadingView() {
        mViewSwitcherNewsError.setDisplayedChild(0);
        mViewSwitcherNewsLoading.setDisplayedChild(1);
        img_loading_rotation.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.anim_rotate));
    }

    /**
     * 展示新闻数据加载正常页面
     */
    private void displayNewsDataOkViw() {
        mViewSwitcherNewsError.setDisplayedChild(0);
        mViewSwitcherNewsLoading.setDisplayedChild(0);
        img_loading_rotation.clearAnimation();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
//        if(isFirstVisibleToUser){
//            isVisible = true;
//            loadData();
//            isFirstVisibleToUser = false;
//            LogUtil.e("fragment", "onvisible");
//        }
        MobclickAgent.onPageStart("found_news");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        MobclickAgent.onPageEnd("found_news");
    }

    /**
     * @return 获取更多新闻
     */
    private List<NewsInfo> loadMoreNews() {
        List<NewsInfo> newsInfos = new ArrayList<NewsInfo>();
        try {
            newsInfos = ParseUtil.getNewsRecommendList(false, ChannelConfig.SPLASH, ChannelConfig.NEWSSRC);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsInfos;
    }

    /**
     * 刷新新闻数据
     */
    private List<NewsInfo> refreshNews(boolean isFirstTime) {
        List<NewsInfo> newsInfos = new ArrayList<>();
        if (mIconInfos != null && mIconInfos.size() > 0) {
            mIconInfos.clear();
        }
        try {
            if(ChannelConfig.SPLASH)
            mIconInfos = ParseUtil.getNewsBannerAdInfos(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            newsInfos = ParseUtil.getNewsRecommendList(isFirstTime, ChannelConfig.SPLASH, ChannelConfig.NEWSSRC);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newsInfos;
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isVisible) {
            return;
        } else {
            if (!mIsLoadData) {
                if (NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED) {
                    displayNewsLoadingView();
                    TaskManager.getInstance().executorNewTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NewsInfo> newsInfos = refreshNews(true);
                                Message message = Message.obtain();
                                message.what = MES_GET_NEWS_OK;
                                message.obj = newsInfos;
                                mDataHandler.sendMessage(message);
                            } catch (Exception e) {
                                mDataHandler.sendEmptyMessage(MES_GET_NEWS_FAIL);
                            }
                        }
                    });
                } else {
                    mViewSwitcherNewsError.setDisplayedChild(1);
                }
            }
        }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class RefreshLoadListener implements RefreshRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    if (NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED) {
                        try {
                            List<NewsInfo> newsInfos = refreshNews(false);
                            Message message = Message.obtain();
                            message.what = MES_GET_NEWS_OK;
                            message.obj = newsInfos;
                            mDataHandler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (getActivity() != null) {
                            ToastUtil.toast(getActivity(), "亲，没网了检查一下网络设置吧！");
                            mRefreshRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRefreshRecyclerView.reset();
                                }
                            });
                        }
                    }
                }
            });
        }

        @Override
        public void onLoadMore() {
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    if (NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED) {
                        try {

                            List<NewsInfo> newsInfos = new ArrayList<NewsInfo>();
                            newsInfos = loadMoreNews();
                            mNewsInfo.addAll(newsInfos);
                            mRefreshRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRefreshRecyclerView.loadMoreComplete();
                                }
                            });
                            mRefreshRecyclerView.getAdapter().notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (getActivity() != null) {
                            ToastUtil.toast(getActivity(), "亲，没网了检查一下网络设置吧！");
                            mRefreshRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRefreshRecyclerView.reset();
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
