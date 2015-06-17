package com.zr.deliver;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.zr.deliver.frame.CustomActivity;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.util.Config;

import java.lang.reflect.Method;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class OrderActivity extends CustomActivity {

    private RecyclerView recyclerView;
    private TextView hintText;
    private OrderAdapter mAdapter;
    private OrderObserver orderObserver;
    private ContentResolver resolver;
    private ActionBar actionBar;
    protected int deliverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        resolver = getContentResolver();
        orderObserver = new OrderObserver(new Handler());
        getContentResolver().registerContentObserver(
                OrderProvider.ORDER_URI, false, orderObserver);
        deliverId = getSharedPreferences(Config.DELIVER_DATA, MODE_PRIVATE).
                getInt(Config.DELIVER_ID, Config.DEFAULT_ID);
        initActionBar();
        initView();

    }

    @Override
    public void initView() {

        hintText = (TextView) findViewById(R.id.hint_text);

        ArrayList<OrderHistoryDelivery> orderList = queryOrderData();
        recyclerView = (RecyclerView) findViewById(R.id.order_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        recyclerView.setOnScrollListener(onScrollListener);
        mAdapter = new OrderAdapter(this, orderList, true);
        mAdapter.setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

        if (orderList != null) {
            actionBar.setTitle(getString(R.string.newest_order) + "(" + orderList.size() + ")");
            recyclerView.setVisibility(View.VISIBLE);
            hintText.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            hintText.setVisibility(View.VISIBLE);
        }

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
        if (orderObserver != null) {
            resolver.unregisterContentObserver(orderObserver);
        }
    }

    private class OrderObserver extends ContentObserver {

        public OrderObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            Log.e("TAG", "更新订单数据");
            hintText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            ArrayList<OrderHistoryDelivery> newdatas = queryOrderData();
            mAdapter.notifyOrderDataSetChanged(newdatas);
            actionBar.setTitle(getResources().getString(R.string.newest_order) + "(" + newdatas.size() + ")");
        }
    }


    private ArrayList<OrderHistoryDelivery> queryOrderData() {

        Cursor c = resolver.query(OrderProvider.ORDER_URI,
                OrderProvider.ORDER_PROJECTION, "deliver_id=?" + " and " + "order_state=?",
                new String[]{deliverId + "", 2 + ""}, null);
        ArrayList<OrderHistoryDelivery> orderList = null;
        if (c != null && c.getCount() > 0) {
            orderList = new ArrayList<>();
            c.moveToFirst();
            do {
                OrderHistoryDelivery order = new OrderHistoryDelivery();
                order.address = c.getString(c
                        .getColumnIndex(OrderProvider.ADRESS));
                order.telephone = c.getString(c
                        .getColumnIndex(OrderProvider.PHONE));
                order.id = c.getInt(c
                        .getColumnIndex(OrderProvider.ORDER_ID));
                order.status = c.getInt(c
                        .getColumnIndex(OrderProvider.ORDER_STATE));
                order.acost = c.getFloat(c
                        .getColumnIndex(OrderProvider.TOTAL_PRICE));
                order.dycost = c.getFloat(c
                        .getColumnIndex(OrderProvider.DELIVER_PRICE));
                order.comment = c.getString(c
                        .getColumnIndex(OrderProvider.REMARKS));
                orderList.add(order);
            } while (c.moveToNext());
            c.close();
        }
        return orderList;
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

    /**
     * Substitute for our onScrollListener for RecyclerView
     */
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("ListView", "onScrollStateChanged");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Could hide open views here if you wanted. //
        }
    };
}
