package com.hodanet.charge.activity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.adapter.PowerOptimizeAppsAdapter;
import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.info.RecommendInfo;
import com.hodanet.charge.info.power_optimize.AppInfo;
import com.hodanet.charge.info.report.RecommendOptimizeReportInfo;
import com.hodanet.charge.info.report.RecommendRecoverReportInfo;
import com.hodanet.charge.model.RecommendModelView;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.SpUtil;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.view.IgnoreDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PowerOptimizeActivity extends BaseActivity {

    private static final int GET_APPS = 1;
    private static final String SP_KEY_LAST_CLEAR_TIME = "last_clear_time";


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_rotate)
    ImageView imgRotate;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.ll_dsc)
    LinearLayout llDsc;
    @BindView(R.id.tv_optimize)
    TextView tvOptimize;
    @BindView(R.id.grid)
    GridView grid;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_save_time)
    TextView tvSaveTime;
    @BindView(R.id.img_application)
    ImageView imgApplication;
    @BindView(R.id.tv_finish_save_time)
    TextView tvFinishSaveTime;
    @BindView(R.id.ll_recommend)
    LinearLayout llRecommend;
    @BindView(R.id.ll_result)
    LinearLayout llResult;


    private List<AppInfo> mAppInfos;
    private Handler mHandler;
    private PowerOptimizeAppsAdapter mOneKeyAdapter;

    private IgnoreDialog ignoreDialog;
    private RecommendModelView recommendView;
    private int saveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_optimize);
        ButterKnife.bind(this);

        initView();
        initHandler();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ignoreDialog != null) {
            if (ignoreDialog.isShowing()) {
                ignoreDialog.dismiss();
            }
            ignoreDialog = null;
        }
        if(recommendView != null) recommendView = null;
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_APPS:
                        mAppInfos.clear();
                        mAppInfos.addAll((List<AppInfo>) msg.obj);
                        mOneKeyAdapter.notifyDataSetChanged();
                        tvCount.setText(mAppInfos.size() + "");
                        saveTime = getLengthenTime();
                        showLengthenTime(tvSaveTime, saveTime);

                        tvOptimize.setEnabled(true);
                        tvOptimize.setText("立即省电");
                        break;
                }
            }
        };
    }

    private void initData() {
        if (ChannelConfig.SPLASH) {
            getHotRecommendAd();
        }

        //优化后2小时内进入都显示以优化
        try {
            String result = SpUtil.getStringData(this, SpUtil.OPTIMIZE_DATA, "");
            JSONObject obj = new JSONObject(result);
            long time = obj.optLong("time");
            saveTime = obj.optInt("saveTime");
            if(System.currentTimeMillis() - time < 1000 * 60 * 60 *2){
                showComplete();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TaskManager.getInstance().executorNewTask(new Runnable() {
            @Override
            public void run() {
                List<AppInfo> list = getClearAppInfo(PowerOptimizeActivity.this);
                Message msg = mHandler.obtainMessage();
                msg.what = GET_APPS;
                msg.obj = list;
                mHandler.sendMessage(msg);
            }
        });

    }


    private void getHotRecommendAd() {
        recommendView = new RecommendModelView(this, new RecommendOptimizeReportInfo());
        recommendView.getHotRecommendAd(this, new RecommendModelView.AdLoadSuccessListener() {
            @Override
            public void loadSuccess(View view) {
                if (view != null) {
                    llRecommend.setVisibility(View.VISIBLE);
                    llRecommend.removeAllViews();
                    llRecommend.addView(view);
                }
            }

            @Override
            public void downloadClick(Object obj) {
                if (obj instanceof RecommendInfo) {
                    RecommendInfo info = (RecommendInfo) obj;
                    info.click(PowerOptimizeActivity.this);
                }
            }
        });

    }

    private void initView() {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        imgRotate.startAnimation(rotate);

        mAppInfos = new ArrayList<>();
        mOneKeyAdapter = new PowerOptimizeAppsAdapter(mAppInfos, this);
        grid.setAdapter(mOneKeyAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo = mAppInfos.get(position);
                ignoreDialog = new IgnoreDialog(PowerOptimizeActivity.this, appInfo, new IgnoreDialog.ComfirmListener() {
                    @Override
                    public void click() {
                        getClearAppInfo(PowerOptimizeActivity.this);
                    }
                });
                ignoreDialog.show();
            }
        });

        tvCount.setText(mAppInfos.size() + "");
        tvSaveTime.setText("");

        tvOptimize.setText("扫描中");
        tvOptimize.setEnabled(false);

    }

    @OnClick({R.id.img_back, R.id.tv_optimize})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_optimize:
                clearListItem(mAppInfos.size() - 1);
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("time", System.currentTimeMillis());
                    obj.put("saveTime", saveTime);
                    SpUtil.saveStringData(this, SpUtil.OPTIMIZE_DATA, obj.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }


    /**
     * 清除图标
     */
    private void clearListItem(final int position) {
        if (position < 0) {
            return;
        }
        View view = grid.getChildAt(position);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                if (position == 0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                clearMemory(PowerOptimizeActivity.this);
                                showComplete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearListItem(position - 1);
                    }
                }, 140);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (view != null) {
            view.startAnimation(animation);
        }
    }

    /**
     * 扫描完成显示结果
     */
    private void showComplete() {
        llDsc.setVisibility(View.GONE);
        llResult.setVisibility(View.VISIBLE);
        showLengthenTime(tvFinishSaveTime, saveTime);

    }

    /**
     * 清理内存
     */
    private void clearMemory(Context context) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        clearMemory(this, mAppInfos);

    }

    /**
     * 清理后台进程
     */
    public static void clearMemory(Context context, List<AppInfo> appInfos) {
        try {
            ActivityManager activityManger = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            for (AppInfo appInfo : appInfos) {
                String pkgName = appInfo.getPkgName();
                activityManger.killBackgroundProcesses(pkgName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取需要清理的应用的信息
     */
    public static List<AppInfo> getClearAppInfo(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        List<String> pkgNameList = new ArrayList<>();
        PackageManager pm = context.getApplicationContext().getPackageManager();
        ActivityManager activityManger = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = activityManger.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            String pkgName = service.process.split(":")[0];
            if (pkgName.equals(context.getPackageName())) {
                continue;
            }
            Boolean isProtect = SpUtil.getBooleanData(context, pkgName, false);
            if (isProtect) {
                continue;
            }
            if (pkgNameList.contains(pkgName)) {
                continue;
            }
            pkgNameList.add(pkgName);
            try {
                ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    String name = info.packageName; // 获得应用程序的包名
                    String appLabel = info.loadLabel(pm).toString(); // 获得应用程序的Label
                    Drawable icon = info.loadIcon(pm); // 获得应用程序图标
                    // 创建一个AppInfo对象，并赋值
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppLabel(appLabel);
                    appInfo.setPkgName(name);
                    appInfo.setAppIcon(icon);
                    appInfoList.add(appInfo); // 添加至列表中
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return appInfoList;
    }

    private int getLengthenTime() {
        int size = 0;
        if (mAppInfos != null) {
            size = mAppInfos.size();
        }
        int tmp = 0;
        Random random = new Random();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                int v = random.nextInt(6) + 5;
                tmp += v;
            }
        }
        tmp = tmp * 2;
        return tmp;
    }

    private void showLengthenTime(TextView tv, int savedTime) {
        int hour = savedTime / 60;
        int minute = savedTime % 60;
        String time = "";
        if (hour > 0) time += hour + "小时";
        if(tv != null) tv.setText(time + minute + "分钟");
    }
}
