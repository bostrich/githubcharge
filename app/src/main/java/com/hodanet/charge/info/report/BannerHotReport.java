package com.hodanet.charge.info.report;

import com.hodanet.charge.info.Constants;

/**
 * Created by June on 2018/4/8.
 */

public class BannerHotReport extends BaseReportInfo {
    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_BANNER);
        setLocation(Constants.UMENG_ID_FLOAT_LOCATION_HOT);
        setPosition(Constants.UMENG_ID_HOT);
        setReportAdType(Constants.REPORT_AD_TYPE_BANNER);
    }
}
