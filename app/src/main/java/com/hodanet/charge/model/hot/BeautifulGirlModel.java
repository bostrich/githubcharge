package com.hodanet.charge.model.hot;



import com.hodanet.charge.info.hot.BeautifulGirlInfo;
import com.hodanet.charge.minterface.GetInfoListener;
import com.hodanet.charge.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by June on 2016/8/2.
 */
public class BeautifulGirlModel {
    private static BeautifulGirlModel model;

    public static BeautifulGirlModel getInstance() {
        if (model == null) {
            model = new BeautifulGirlModel();
        }
        return model;
    }

    public void getBeautifulGirlInfos(int type, GetInfoListener listener) throws IOException {
        String result = HttpUtils.requestBeautifulGirlInfo();
        JSONObject json = null;
        List<BeautifulGirlInfo> mList = new ArrayList<>();
        try {
            json = new JSONObject(result);
            JSONObject json2 = json.optJSONObject("data");
            JSONArray jsonArray = json2.optJSONArray("beauty");
            BeautifulGirlInfo info = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                info = new BeautifulGirlInfo();
                info.setName(object.optString("name"));
                info.setPic(object.optString("pic"));
                mList.add(info);
            }
            listener.getInfoSucceed(type, mList);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.getInfoFailed(e.toString());
        }
    }

}
