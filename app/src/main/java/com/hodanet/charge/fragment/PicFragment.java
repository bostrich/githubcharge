package com.hodanet.charge.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hodanet.charge.R;
import com.hodanet.charge.activity.WallPaperActivity;
import com.hodanet.charge.adapter.PicTypeViewPagerAdapter;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.config.DeviceConfig;
import com.hodanet.charge.info.pic.WallpaperClassifyBean;
import com.hodanet.charge.utils.HttpUtils;
import com.hodanet.charge.utils.NetSpeedUtil;
import com.hodanet.charge.utils.ParseUtil;
import com.hodanet.charge.utils.Stats;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 *
 */
public class PicFragment extends Fragment {

    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.img_rotation)
    ImageView imgRotation;
    @BindView(R.id.tvReload)
    TextView tvReload;
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;
    @BindView(R.id.ll_error)
    LinearLayout llError;
    @BindView(R.id.iv_blur)
    ImageView ivBlur;
    @BindView(R.id.tv_theme)
    TextView tvTheme;
    @BindView(R.id.vp_classify_wallpaper)
    ViewPager vpClassifyWallpaper;
    @BindView(R.id.ll_dots)
    LinearLayout llDots;

    private static final int NORMAL = 3;
    private static final int ERROR = 2;
    private static final int LOADING = 1;

    public static final String INTENT_KEY_THEME_WALLPAPERS = "wallpaper_items";
    public static final String PIC_DEFAULT_URL = "http://res.ipingke.com/wallpaper/wallpaper.html";

    private static final int GET_INFO_OK = 1;
    private static final int GET_INFO_FAILED = 2;

    private JSONObject jsonObject;

    private String domain = "http://res.ipingke.com";
    public static final String DOMAIN_DEFAULT = "http://res.ipingke.com";

    private List<WallpaperClassifyBean> list = new ArrayList<>();

    private static final int dotWidth = (int) (DeviceConfig.SCREEN_DENSITY * 6 + 0.5f);

    private Handler mHandler;
    private PagerAdapter mPicTypeViewPagerAdapter;

    /**
     * 将小圆点的图片用数组表示
     */
    private ImageView[] dotViews;

    public PicFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pic, container, false);
        ButterKnife.bind(this, view);
        initView();
        initHandler();
        initData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


    private void initData() {
        if (NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED) {
            showView(LOADING);
            TaskManager.getInstance().executorNewTask(new Runnable() {
                @Override
                public void run() {
                    String result = "";
                    try {

                        result = HttpUtils.requestWallPaper(PIC_DEFAULT_URL);

                        JSONObject obj = new JSONObject(result);
                        domain = obj.optString("host");
                        jsonObject = obj;
//                        SpUtils.saveWallpaperInfo(getContext(), result);
                        list.clear();
                        list.addAll(ParseUtil.getWallpaperClassify(result));
                        mHandler.sendEmptyMessage(GET_INFO_OK);

                    } catch (IOException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(GET_INFO_FAILED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(GET_INFO_FAILED);
                    }
                }
            });
        } else {
            showView(ERROR);
        }
    }


    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_INFO_OK:
                        showView(NORMAL);
                        setDotsView();
                        mPicTypeViewPagerAdapter.notifyDataSetChanged();
                        if (list.size() > 0) setSelectedView(0);
                        break;
                    case GET_INFO_FAILED:
                        showView(ERROR);
                        break;
                }
            }
        };
    }


    private void initView() {
        showView(LOADING);
        mPicTypeViewPagerAdapter = new PicTypeViewPagerAdapter(getContext(), list, new PicTypeViewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(NetSpeedUtil.checkNetworkType(getContext()) != NetSpeedUtil.TYPE_NET_WORK_DISABLED){
                    try {
                        WallpaperClassifyBean wallpaperPicType = list.get(position);
                        String id = wallpaperPicType.getId();
                        String themeWallpaperString = jsonObject.getString(id);
                        Intent intent = new Intent(getContext(), WallPaperActivity.class);
                        intent.putExtra(INTENT_KEY_THEME_WALLPAPERS, themeWallpaperString);
                        startActivity(intent);
                        Stats.event(getContext(), "wk_pic_category_click");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    ToastUtil.toast(getContext(), "亲，没网了检查一下网络设置吧！");
                }

            }
        });
        vpClassifyWallpaper.setOffscreenPageLimit(2);
        vpClassifyWallpaper.setAdapter(mPicTypeViewPagerAdapter);
        vpClassifyWallpaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedView(position);
                Stats.event(getContext(), "wk_pic_category_show");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 显示主要视图
     *
     * @param type 显示类型
     */
    private void showView(int type) {
        switch (type) {
            case NORMAL:
                rlContent.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
                imgRotation.clearAnimation();
                break;
            case ERROR:
                rlContent.setVisibility(View.GONE);
                rlLoading.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                imgRotation.clearAnimation();
                break;
            case LOADING:
                rlContent.setVisibility(View.GONE);
                rlLoading.setVisibility(View.VISIBLE);
                llError.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                animation.setInterpolator(new LinearInterpolator());
                imgRotation.startAnimation(animation);
                break;
        }

    }

    private void setDotsView() {
        if (list != null && list.size() > 0) {
            dotViews = new ImageView[list.size()];
            // 添加小圆点的图片
            for (int i = 0; i < list.size(); i++) {
                //getContext()空指针异常
                if(getContext() == null) return;
                ImageView dot = new ImageView(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dotWidth, dotWidth);
                layoutParams.setMargins(10, 0, 10, 0);
                dot.setLayoutParams(layoutParams);// 创建一个宽高均为20 的布局
                dotViews[i] = dot;
                // 默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
                if (i == 0) {
                    dotViews[i].setBackgroundResource(R.drawable.shape_selected_dot);
                } else {
                    dotViews[i].setBackgroundResource(R.drawable.shape_unselected_dot);
                }

                llDots.addView(dotViews[i]);
            }
        }
    }

    private void setSelectedView(int position) {
        for (int i = 0; i < dotViews.length; i++) {
            dotViews[position].setBackgroundResource(R.drawable.shape_selected_dot);
            // 不是当前选中的page，其小圆点设置为未选中的状态
            if (position != i) {
                dotViews[i].setBackgroundResource(R.drawable.shape_unselected_dot);
            }
        }


        WallpaperClassifyBean wallpaperPicType = list.get(position % list.size());
        String imgUrl = wallpaperPicType.getCover();
        String name = wallpaperPicType.getName();

        if (!TextUtils.isEmpty(name)) {
            tvTheme.setText(name);
        }

        if (!TextUtils.isEmpty(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = PicTypeViewPagerAdapter.WALLPAPER_HOST + imgUrl;
        }
        try {
            Glide.with(getContext())
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.mipmap.pic_default_blur)
                    .centerCrop()
                    .bitmapTransform(new BlurTransformation(getContext(), 25, 20))
                    .into(ivBlur);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            if(llError != null && llError.isShown()){
                initData();
            }
            Stats.event(getContext(), "wk_main_tab_girl_show");
        }

    }

    @OnClick(R.id.tvReload)
    public void onClick() {
        initData();
    }

}
