package com.hodanet.charge.model.hot;


import com.hodanet.charge.info.hot.VideoInfo;
import com.hodanet.charge.minterface.GetInfoListener;
import com.hodanet.charge.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class VideoModel {

    public static final int INIT_DAT = 10;

    public static final int REFRESH_DATA = 11;
    public static final int LOAD_MORE_DATA = 12;

    private static VideoModel videoModel;

    public static VideoModel getInstance() {
        if (videoModel == null) {
            videoModel = new VideoModel();
        }
        return videoModel;
    }

    public void getVideoInfos(int type, GetInfoListener listener) throws IOException {
        String result = "";
        if (type == INIT_DAT) {
            result = HttpUtils.requestVideoInfo();
        } else if (type == REFRESH_DATA || type == LOAD_MORE_DATA) {
            result = HttpUtils.requestVideoDynaticInfo();
        }
        JSONObject json = null;
        List<VideoInfo> mList = new ArrayList<>();
        try {
            json = new JSONObject(result);
            JSONObject json2 = json.optJSONObject("data");
            JSONArray jsonArray = json2.optJSONArray("video");
            VideoInfo info = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                info = new VideoInfo();
                info.setName(object.optString("name"));
                info.setPic(object.optString("pic"));
                info.setUrl(object.optString("url"));
                mList.add(info);
            }
            listener.getInfoSucceed(type, mList);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.getInfoFailed(e.toString());
        }
    }

}
