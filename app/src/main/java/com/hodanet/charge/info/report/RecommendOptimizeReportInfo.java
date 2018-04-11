package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 *
 */

public class RecommendOptimizeReportInfo extends BaseReportInfo {

    public RecommendOptimizeReportInfo(){
        super();
    }

    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RECOMMEND);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_OPTIMIZE);
        setPosition(Constants.UMENG_ID_RECOMMEND_POSITION_OPTIMIZE);
        setReportAdType(Constants.REPORT_AD_TYPE_RECOMMEND);
    }
}
