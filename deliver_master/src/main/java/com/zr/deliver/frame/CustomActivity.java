package com.zr.deliver.frame;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zr.deliver.util.Config;

import de.greenrobot.event.EventBus;


public abstract class CustomActivity extends FragmentActivity implements View.OnClickListener {
    //下面四个是基础模块，网络通信，简单的配置文件存储，界面短提示弹出以及loadding提示弹出
    public RequestQueue mQueue;
    public SharedPreferences mPreferences;
    public CustomToast toast;
    public ProgressDialog loadingDialog;

    //启用需用eventBus需要注册，单独拎出来，避免內存消耗
    public EventBus eventBus;
    //公用Log
    public static final String TAG = "CUSTOM";
    //测试跟踪
    public static final boolean DEBUG_STRICT_MODE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //调试模式启用StrictMode跟踪内存使用
        if (DEBUG_STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        mQueue = Volley.newRequestQueue(this);
        mPreferences = getSharedPreferences(Config.DELIVER_DATA, MODE_PRIVATE);
        toast = new CustomToast(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);
        loadingDialog.setMessage("请稍等...");
    }

    @Override
    public void onClick(View v) {

    }

    public void initEventBus() {
        eventBus = EventBus.getDefault();//组件通信模块
        eventBus.register(this);
    }

    public void onEventMainThread(CustomEvent event) {

    }

    public void initView() {
    }

    public void initActionBar() {

    }

    public void bindData() {

    }

    private void clearMemory() {
        mQueue.stop();
        if (eventBus != null) eventBus.unregister(this);
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

}
