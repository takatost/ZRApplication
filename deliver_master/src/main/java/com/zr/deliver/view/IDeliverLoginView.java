package com.zr.deliver.view;

import android.content.Context;

/**
 * Created by Administrator on 2015/6/30.
 */
public interface IDeliverLoginView {

    /**
     * V模块对应用户界面元素以及反馈，对外提供给
     * P模块调用
     */
    Context getContext();

    String getUserName();

    String getPassword();

    void showLoading();

    void hideLoading();

    void toMainActivity();

    void showFailedError(int errorId);
}
