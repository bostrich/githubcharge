package com.hodanet.charge.info;

import android.content.Context;

import com.hodanet.charge.info.report.BannerHotReport;
import com.syezon.component.bean.FoundBean;

/**
 * Created by June on 2018/4/11.
 */

public class NewFoundAppInfo extends BaseInfo {



    @Override
    public void click(Context context) {

    }

    public static NewFoundAppInfo convertInfo(FoundBean bean){
        NewFoundAppInfo info = new NewFoundAppInfo();
        info.setInfoType(1);
        info.setId(1);
        info.setUrl(bean.getUrl());
        info.setName(bean.getName());
        info.setReportInfo(new BannerHotReport());
        return info;
    }
}
