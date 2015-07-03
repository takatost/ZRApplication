package com.zr.deliver.biz;

/**
 * Created by Administrator on 2015/6/30.
 */
public interface OnRequestListener {

    /**
     * 接口布置联网请求，失败或者成功回调，
     * 失败返回失败码，现在主要结合volley
     * 提供给P模块调用，其内部实现
     * 类属于M模块，主要是获取数据的逻辑
     */

    void loginSuccess();

    void loginFailed(int errorId);

}
