package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 *
 */

public class RecommendCommonSecretReportInfo extends BaseReportInfo {

    public RecommendCommonSecretReportInfo(){
        super();
    }


    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RECOMMEND);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_COMMON_SECRET);
        setPosition(Constants.UMENG_ID_RECOMMEND_POSITION_COMMON_SECRET);
        setReportAdType(Constants.REPORT_AD_TYPE_RECOMMEND);
    }
}
