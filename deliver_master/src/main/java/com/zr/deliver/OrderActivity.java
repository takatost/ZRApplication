package com.zr.deliver;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.zr.deliver.adpter.DividerItemDecoration;
import com.zr.deliver.adpter.OrderAdapter;
import com.zr.deliver.custom.CustomActivity;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.presenter.OrderPresenter;
import com.zr.deliver.util.Config;
import com.zr.deliver.view.IOrderView;

import java.lang.reflect.Method;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class OrderActivity extends CustomActivity implements IOrderView {

    private RecyclerView recyclerView;
    private TextView hintText;
    private OrderAdapter mAdapter;
    private ActionBar actionBar;
    private OrderPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        presenter = new OrderPresenter(this, getOrderTypeState());
        initActionBar();
        initView();
        presenter.loadAllData();
    }

    @Override
    public void initView() {

        hintText = (TextView) findViewById(R.id.hint_text);

        recyclerView = (RecyclerView) findViewById(R.id.order_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

    }


    @Override
    public void initActionBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.newest_order));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.clear();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public int getOrderTypeState() {
        return 2;
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
    public void toDetailActivity(OrderHistoryDelivery orderHistoryDelivery) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Config.ORDER_DETAIL_KEY, orderHistoryDelivery);
        intent.putExtras(bundle);
        intent.setClass(this, OrderDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void bindData(List<OrderHistoryDelivery> data) {

        if (mAdapter == null) {
            mAdapter = new OrderAdapter(this, presenter, data, true);
            mAdapter.setMode(Attributes.Mode.Single);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyOrderDataSetChanged(data);
        }
        if (data != null) {
            actionBar.setTitle(getString(R.string.newest_order) + "(" + data.size() + ")");
            recyclerView.setVisibility(View.VISIBLE);
            hintText.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            hintText.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showFailedError(int errorId) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_attend:
                startActivity(new Intent(this, AttendActivity.class));
                break;
            case R.id.action_see:
                startActivity(new Intent(this, RecordActivity.class));
                break;
            case R.id.action_out:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }


    private void setOverflowIconVisible(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.d("OverflowIconVisible", e.getMessage());
                }
            }
        }
    }

}
