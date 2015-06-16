package com.zr.deliver;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2015/6/5.
 * 这个抽象acitity里面包括通用模块
 * 1，网络资源
 * 2，Toast友好弹出
 * 3，联网广播和数据加载时的Toast的综合处理
 * 4，view初始化
 */
public abstract class ModulesActivity extends FragmentActivity {

    private RequestQueue mQueue;//网络模块


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQueue = Volley.newRequestQueue(this);


    }


    private void initView() {

    }

    private void initData() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.stop();
    }
}
