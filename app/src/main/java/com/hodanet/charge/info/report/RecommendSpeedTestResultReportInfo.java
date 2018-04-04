package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 *
 */

public class RecommendSpeedTestResultReportInfo extends BaseReportInfo {

    public RecommendSpeedTestResultReportInfo(){
        super();
    }

    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RECOMMEND);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_SPEED_TEST_RESULT);
        setPosition(Constants.UMENG_ID_RECOMMEND_POSITION_SPEED_TEST_RESULT);
        setReportAdType(Constants.REPORT_AD_TYPE_RECOMMEND);
    }
}
