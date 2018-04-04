package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 * Created by June on 2018/1/4.
 */

public class RecommendSlideMenuReportInfo extends BaseReportInfo {


    public RecommendSlideMenuReportInfo(){
        super();
    }

    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RECOMMEND);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_SLIDE_MENU);
        setPosition(Constants.UMENG_ID_RECOMMEND_POSITION_SLIDE_MENU);
        setReportAdType(Constants.REPORT_AD_TYPE_RECOMMEND);
    }
}
