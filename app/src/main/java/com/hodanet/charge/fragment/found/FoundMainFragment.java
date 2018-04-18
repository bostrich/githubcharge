package com.hodanet.charge.fragment.found;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hodanet.charge.R;
import com.hodanet.charge.activity.WebQQActivity;
import com.hodanet.charge.download.DownloadManager;
import com.hodanet.charge.download.feedback.NotificationDownloadFeedback;
import com.hodanet.charge.greendao.StandardInfo;
import com.hodanet.charge.info.report.BannerHotReport;
import com.hodanet.charge.utils.DialogUtil;
import com.hodanet.charge.utils.DownloadUtil;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.Stats;
import com.syezon.component.adapterview.BaseAdapterView;
import com.syezon.component.adapterview.BigAdapterView;
import com.syezon.component.adapterview.MidAdapterView;
import com.syezon.component.adapterview.ScrollBannerAdatperView;
import com.syezon.component.adapterview.SmallAdapterView;
import com.syezon.component.adview.BigAd;
import com.syezon.component.adview.MidAd;
import com.syezon.component.adview.SmallAd;
import com.syezon.component.adview.TopBannerAd;
import com.syezon.component.bean.FoundBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundMainFragment extends Fragment implements BaseAdapterView.AdListener{


    private static final String TAG = FoundMainFragment.class.getName();
    @BindView(R.id.rl_banner)
    RelativeLayout rlBanner;
    @BindView(R.id.rl_small)
    RelativeLayout rlSmall;
    @BindView(R.id.rl_mid)
    RelativeLayout rlMid;
    @BindView(R.id.rl_big)
    RelativeLayout rlBig;
    Unbinder unbinder;

    public FoundMainFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        new TopBannerAd(rlBanner, new ScrollBannerAdatperView(getContext(), this));
        SmallAdapterView view2 = new SmallAdapterView(getContext(), this);
        view2.setLocalData(new BaseAdapterView.AddLocalData() {
            @Override
            public void addLocalData(List<FoundBean> list) {
                FoundBean info2 = new FoundBean();
                info2.setType("activity");
                info2.setPic("" + R.mipmap.girl);
                info2.setUrl("com.hodanet.charge.activity.PicActivity");
                info2.setId("picture");
                info2.setPosition(FoundBean.POSITION_SMALL);
                list.add(0, info2);
            }
        });
        new SmallAd(rlSmall, view2);

        MidAdapterView view3 = new MidAdapterView(getContext(), this);
        new MidAd(rlMid, view3);

        BigAdapterView view4 = new BigAdapterView(getContext(), this);
        new BigAd(rlBig, view4);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void click(final FoundBean foundBean, View view) {
        reportEvent("click", foundBean);
        switch(foundBean.getType()) {
            case "url":
                Intent intent = new Intent(getContext(), WebQQActivity.class);
                intent.putExtra("URL", foundBean.getUrl());
                intent.putExtra(WebQQActivity.WEB_SHOW_AD, false);
                getContext().startActivity(intent);
                break;
            case "apk":
                DialogUtil.showDownloadHint(getContext(), foundBean.getName(), new DialogUtil.ConfirmListener() {
                    @Override
                    public void confirm() {
                        StandardInfo adInfo = new StandardInfo();
                        adInfo.setDesUrl(foundBean.getUrl());
                        adInfo.setDescription(foundBean.getPkgName());
                        adInfo.setName(foundBean.getName());
                        DownloadUtil.downloadApk(getContext(), StandardInfo.convertInfo(adInfo, new BannerHotReport())
                                , DownloadManager.DOWNLOAD_STRATERY_SERVICE, new NotificationDownloadFeedback(getContext()));
                    }

                    @Override
                    public void cancle() {

                    }
                });

                break;
            case "activity":
                try {
                    Class<?> clazz = Class.forName(foundBean.getUrl());
                    startActivity(new Intent(getContext(), clazz));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void show(FoundBean foundBean) {
        LogUtil.e(TAG, "可以展示:" + foundBean.getType());
        reportEvent("show", foundBean);
    }


    /**
     *
     * @param action
     * @param bean
     */
    private void reportEvent(String action, FoundBean bean){
        String suffix1 = "";
        switch(bean.getPosition()){
            case FoundBean.POSITION_SMALL:
                suffix1 = "Small";
                break;
            case FoundBean.POSITION_TOP:
                suffix1 = "Top";
                break;
            case FoundBean.POSITION_BIG:
                suffix1 = "Bottom";
                break;
            default:
                if(bean.getPosition().contains(FoundBean.POSITION_MI)){
                    suffix1 = "Mid";
                }
                break;
        }
        String suffix2 = "";
        switch(action){
            case "show":
                suffix2 = "Show";
                break;
            case "click":
                suffix2 = "Click";
                break;
        }
        Stats.eventToYoumeng(getContext(), "jumei" + suffix1, suffix2, bean.getId());
    }
}
