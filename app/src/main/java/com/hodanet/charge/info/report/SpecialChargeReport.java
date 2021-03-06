package com.hodanet.charge.info.report;

import com.hodanet.charge.info.Constants;

/**
 * Created by June on 2018/4/8.
 */

public class SpecialChargeReport extends BaseReportInfo {
    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_SPECIAL);
        setLocation(Constants.UMENG_ID_FLOAT_LOCATION_CHARGE);
        setPosition(Constants.UMENG_ID_CHARGE);
        setReportAdType(Constants.REPORT_AD_TYPE_SPECIAL);
    }
}
