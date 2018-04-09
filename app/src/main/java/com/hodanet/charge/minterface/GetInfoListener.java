package com.hodanet.charge.minterface;


import java.util.List;

/**
 *
 */
public interface GetInfoListener<T> {
    void getInfoSucceed(int type, List<T> list);

    void getInfoFailed(String error);
}
