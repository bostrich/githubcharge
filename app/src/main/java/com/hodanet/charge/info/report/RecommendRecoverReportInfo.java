package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 *
 */

public class RecommendRecoverReportInfo extends BaseReportInfo {

    public RecommendRecoverReportInfo(){
        super();
    }

    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RECOMMEND);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_RECOVER);
        setPosition(Constants.UMENG_ID_RECOMMEND_POSITION_RECOVER);
        setReportAdType(Constants.REPORT_AD_TYPE_RECOMMEND);
    }
}
