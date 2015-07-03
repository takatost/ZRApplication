package com.zr.deliver.biz;

/**
 * Created by Administrator on 2015/6/30.
 */
public interface IDeliverLoginBiz {

    /**
     * M模块调用接口，向外提供给P模块调用
     * 其实现类封装了业务方法也就是对数据的实际操作
     *
     */

    public void login(String username, String password, OnRequestListener loginListener);

    public String getDefaultName();

    public String getDefaultPassword();

    public void clear();

}
