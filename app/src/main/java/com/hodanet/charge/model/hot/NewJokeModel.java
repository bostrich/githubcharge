package com.hodanet.charge.model.hot;



import com.hodanet.charge.config.ChannelConfig;
import com.hodanet.charge.info.hot.JokeAdInfo;
import com.hodanet.charge.minterface.GetNewJokerInfoListener;
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
public class NewJokeModel {
    public static final int INIT_DAT = 10;
    public static final int REFRESH_DATA = 11;
    public static final int LOAD_MORE_DATA = 12;
    private static NewJokeModel joke;

    public static NewJokeModel getInstance() {
        if (joke == null) {
            joke = new NewJokeModel();
        }
        return joke;
    }

    public void getJokeInfos(int type, GetNewJokerInfoListener listener) {
        String result = "";
        try {
            if (type == INIT_DAT) {
                result = HttpUtils.requestNewJokeInfo();
            } else if (type == REFRESH_DATA || type == LOAD_MORE_DATA) {
                result = HttpUtils.requestNewJokeDynaticInfo();
            }
            JSONObject json = null;
            List<JokeAdInfo> mList = new ArrayList<>();
            json = new JSONObject(result);
            JSONObject json2 = json.optJSONObject("data");
            JSONArray jsonArray = json2.optJSONArray("joke");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                if (object != null) {
                    JokeAdInfo jokeAdInfo = new JokeAdInfo();
                    jokeAdInfo.type = object.optInt("type");
                    jokeAdInfo.name = object.optString("name");
                    jokeAdInfo.content = object.optString("content");
                    jokeAdInfo.advNmae = object.optString("advName");
                    jokeAdInfo.id = object.optInt("id");
                    jokeAdInfo.url = object.optString("url");
                    jokeAdInfo.infoType = object.optInt("infoType");
                    jokeAdInfo.picture = object.optString("picture");
                    JSONArray imgArray = object.optJSONArray("imgs");
                    StringBuilder imgString = new StringBuilder();
                    if (imgArray != null && imgArray.length() > 0) {
                        for (int j = 0; j < imgArray.length(); j++) {
                            JSONObject imgObject = imgArray.getJSONObject(j);
                            if (imgObject != null) {
                                imgString.append(imgObject.optString("url"));
                                if (j != imgArray.length() - 1) {
                                    imgString.append("|");
                                }
                            }
                        }
                    }
                    jokeAdInfo.imgs = imgString.toString();
                    if (jokeAdInfo.type == 0 && !ChannelConfig.SPLASH) {
                        continue;
                    }

                    mList.add(jokeAdInfo);
                }

            }
            listener.getInfoSucceed(type, mList);
        } catch (JSONException e) {
            listener.getInfoFailed(e.toString());
        } catch (IOException e) {
            listener.getInfoFailed(e.toString());
        }
    }

}