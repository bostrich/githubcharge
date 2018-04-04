package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 *
 */

public class RecommendStrongCrackReportInfo extends BaseReportInfo {


    public RecommendStrongCrackReportInfo(){
        super();
    }

    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RECOMMEND);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_STRONG_CRACK);
        setPosition(Constants.UMENG_ID_RECOMMEND_POSITION_STRONG_CRACK);
        setReportAdType(Constants.REPORT_AD_TYPE_RECOMMEND);
    }
}
