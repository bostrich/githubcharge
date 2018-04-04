package com.hodanet.charge.info.report;


import com.hodanet.charge.info.Constants;

/**
 *
 */

public class IntergralWallCheckPwdReportInfo extends BaseReportInfo{

    public IntergralWallCheckPwdReportInfo(){
        super();
    }

    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_INTERGRAL_WALL);
        setLocation(Constants.UMENG_ID_INTERGRAL_WALL_LOCATION_CHECK_PWD);
        setPosition(Constants.UMENG_ID_INTERGRAL_WALL_POSITION_CHECK_PWD);
        setReportAdType(Constants.REPORT_AD_TYPE_INTERGRAL_WALL);
    }
}
