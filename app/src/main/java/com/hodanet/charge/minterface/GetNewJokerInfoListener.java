package com.hodanet.charge.minterface;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */

public interface GetNewJokerInfoListener<T> {
    void getInfoSucceed(int type, List<T> list);

    void getInfoFailed(String error);
}
