package com.zr.deliver;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.zr.deliver.custom.CustomActivity;
import com.zr.deliver.presenter.AttendPresenter;
import com.zr.deliver.util.Config;
import com.zr.deliver.util.PollingUtils;
import com.zr.deliver.view.IDeliverAttendView;

/**
 * Created by xia on 2015/5/5.
 */
public class AttendActivity extends CustomActivity implements LocationSource, AMapLocationListener, IDeliverAttendView {

    private AMap aMap;
    private MapView mapView;
    private Button attendBt;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private String currentAddress;
    private double latitude, longitude;
    private AttendPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attend);
        presenter = new AttendPresenter(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
        initActionBar();
        initView();
    }

    @Override
    public void initView() {
        attendBt = (Button) findViewById(R.id.attend_bt);
        int state = presenter.getDefaultState();
        attendBt.setText(state == 0 ? getString(R.string.sign_in) : getString(R.string.sign_out));
        attendBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.attend_bt) {
            presenter.attend();
        }
    }


    @Override
    public void initActionBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.deliver_sign_in));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
        // 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 此方法需存在
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 此方法需存在
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        presenter.clear();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        /* 获取经纬度 */
        latitude = aMapLocation.getLatitude();
        longitude = aMapLocation.getLongitude();
        //获取当前地址
        Bundle locBundle = aMapLocation.getExtras();
        if (locBundle != null) {
            currentAddress = locBundle.getString("desc");
        }
        //显示
        if (mListener != null) {
            if (0 != aMapLocation.getAMapException().getErrorCode()) {
                return;
            }
            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
        }
        deactivate();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
        }
    }

    @Override
    public void deactivate() {

        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String getAdress() {
        return currentAddress;
    }

    @Override
    public void showLoading() {
        loadingDialog.show();

    }

    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    @Override
    public void doPolling() {
        int state = presenter.getDefaultState();
        if (state == 0) {
            PollingUtils.startPollingService(
                    AttendActivity.this,
                    15 * 1000,
                    AlarmPollService.class,
                    Config.POLLING_ACTION);
        } else {
            PollingUtils.stopPollingService(AttendActivity.this,
                    AlarmPollService.class,
                    Config.POLLING_ACTION);
            //这一步有必要，service只有stop和unbind才能彻底关闭，上面只是关闭alarm服务
            stopService(new Intent(AttendActivity.this, AlarmPollService.class));
        }
        finish();
    }

    @Override
    public void showFailedError(int errorId) {
        //处理登陆失败的异常

    }
}
