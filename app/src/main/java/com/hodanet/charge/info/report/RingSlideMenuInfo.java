package com.hodanet.charge.info.report;

import com.hodanet.charge.info.Constants;

/**
 * Created by June on 2018/4/8.
 */

public class RingSlideMenuInfo extends BaseReportInfo {
    @Override
    public void initParams() {
        setUmengEventId(Constants.UMENG_ID_RING);
        setLocation(Constants.UMENG_ID_RECOMMEND_LOCATION_SLIDE_MENU);
        setPosition(Constants.UMENG_ID_SLIDE_MEMU);
        setReportAdType(Constants.REPORT_AD_TYPE_RING);
    }
}
