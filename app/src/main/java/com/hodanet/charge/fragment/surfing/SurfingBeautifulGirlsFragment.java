package com.hodanet.charge.fragment.surfing;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.hodanet.charge.R;
import com.hodanet.charge.adapter.hot.BeautifilGirlAdapter;
import com.hodanet.charge.info.hot.BeautifulGirlInfo;
import com.hodanet.charge.minterface.GetInfoListener;
import com.hodanet.charge.model.hot.BeautifulGirlModel;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.MD5;
import com.hodanet.charge.utils.NetSpeedUtil;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.ToastUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by June on 2016/8/2.
 */
public class SurfingBeautifulGirlsFragment extends BaseFragment implements View.OnClickListener {


    public static final String ID = "id";
    private static final int GET_GIRLINFO_SUCCEED = 1;
    private static final int GET_GIRLINFO_FAILED = 2;
    private static SurfingBeautifulGirlsFragment fragment;
    private String mId = "";
    private ViewSwitcher mViewSwitcherGirlError;
    private TextView mTvReLoad;
    private ViewPager mViewPager;
    private BeautifilGirlAdapter adapter;
    private List<BeautifulGirlInfo> mList = new ArrayList<>();
    private RelativeLayout arrow_left;
    private RelativeLayout arrow_right;
    private LinearLayout layout_set;
    private LinearLayout layout_download;
    private WallPaperBroadcast broadcast;
    //    private boolean mIsLoadData;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_GIRLINFO_SUCCEED:
                    List list = (List) msg.obj;
                    mList.clear();
//                    mIsLoadData = true;
                    for (int i = 0; i < list.size(); i++) {
                        mList.add((BeautifulGirlInfo) list.get(i));
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case GET_GIRLINFO_FAILED:
                    String error = (String) msg.obj;
                    LogUtil.e("joke", error);
                    break;
            }
        }
    };

    public static SurfingBeautifulGirlsFragment getInstance() {
        if (fragment == null) {
            fragment = new SurfingBeautifulGirlsFragment();
        }
        return fragment;
    }


    public static SurfingBeautifulGirlsFragment newInstance() {
        SurfingBeautifulGirlsFragment fragment = new SurfingBeautifulGirlsFragment();
        return fragment;
    }

    public static SurfingBeautifulGirlsFragment newInstance(String id) {
        SurfingBeautifulGirlsFragment fragment = new SurfingBeautifulGirlsFragment();
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
        broadcast = new WallPaperBroadcast();
        IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
        getContext().registerReceiver(broadcast, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_beautiful_girls, container, false);
            initViews(contentView);
            isPrepared = true;
            loadData();
        } else {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (parent != null) {
                parent.removeView(contentView);
            }
        }
        return contentView;
    }

    private void initViews(View view) {
        mViewSwitcherGirlError = (ViewSwitcher) view.findViewById(R.id.viewSwitcherSurfingGirl);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        arrow_left = (RelativeLayout) view.findViewById(R.id.arrow_left);
        arrow_left.setOnClickListener(this);
        arrow_left.setVisibility(View.GONE);
        arrow_right = (RelativeLayout) view.findViewById(R.id.arrow_right);
        arrow_right.setOnClickListener(this);
        layout_set = (LinearLayout) view.findViewById(R.id.layout_wallpaper);
        layout_set.setOnClickListener(this);
        layout_download = (LinearLayout) view.findViewById(R.id.layout_download);
        layout_download.setOnClickListener(this);
        adapter = new BeautifilGirlAdapter(getContext(), mList, new BeautifilGirlAdapter.ViewPagerListener() {
            @Override
            public void changePager(int type) {
                if (type == BeautifilGirlAdapter.LEFT) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                } else if (type == BeautifilGirlAdapter.RIGHT) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }
        });
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    arrow_left.setVisibility(View.GONE);
                } else if (position == mList.size() - 1) {
                    arrow_right.setVisibility(View.GONE);
                } else {
                    arrow_left.setVisibility(View.VISIBLE);
                    arrow_right.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        mTvReLoad = (TextView) contentView.findViewById(R.id.tvReload);
        mTvReLoad.setOnClickListener(this);
    }

    private void initData() {
        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                try {
                    BeautifulGirlModel.getInstance().getBeautifulGirlInfos(0, new GetInfoListener() {
                        @Override
                        public void getInfoSucceed(int type, List list) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = GET_GIRLINFO_SUCCEED;
                            msg.arg1 = type;
                            msg.obj = list;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void getInfoFailed(String error) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = GET_GIRLINFO_FAILED;
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
        getContext().unregisterReceiver(broadcast);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_left:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.arrow_right:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            case R.id.layout_wallpaper:
                final WallpaperManager manager = WallpaperManager.getInstance(getContext());
                TaskManager.getInstance().executorNewTask(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap image = null;
                        try {
                            image = Picasso.with(getContext()).load(mList.get(mViewPager.getCurrentItem()).getPic()).get();
                            final Bitmap finalImage = image;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        manager.setBitmap(finalImage);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.layout_download:
                downloadImage(mList.get(mViewPager.getCurrentItem()).getPic());
                break;
            case R.id.tvReload:
                loadData();
                break;

        }
    }

    private void downloadImage(final String _url) {
        String fileName = MD5.md5(_url) + ".jpg";
        final File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        if (file.exists()) {
            ToastUtil.toastLong(getContext(), "已下载");
        } else {
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    Bitmap image = null;
                    try {
                        image = Picasso.with(getContext()).load(_url).get();
                        FileOutputStream out;
                        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), image, MD5.md5(_url), "下载图片");
                        try {
                            out = new FileOutputStream(file);
                            if (image.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                                out.flush();
                                out.close();
                                ToastUtil.toastLong(getContext(), "下载成功");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    @Override
    protected void loadData() {
        if (!isVisible || !isPrepared) {
            return;
        } else {
            if (mList.size() < 1) {
                if (NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED) {
                    mViewSwitcherGirlError.setDisplayedChild(0);
                    initData();
                } else {
                    mViewSwitcherGirlError.setDisplayedChild(1);
                }
            }
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
//        Stats.event(getActivity(), "SURFING_NAVIGATION_CLICK_" + mId);
        if (mList != null && mList.size() > 0) {
            Collections.shuffle(mList);
            if (mViewPager.getAdapter() != null) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        }
        MobclickAgent.onPageStart("found_girl");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        MobclickAgent.onPageEnd("found_girl");
    }

    public void setNext() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    /**
     * 广播接收桌面壁纸改变
     */
    private class WallPaperBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ToastUtil.toastLong(getContext(), "设置成功");
        }
    }
}
