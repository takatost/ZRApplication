package com.zr.deliver;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.swipe.util.Attributes;
import com.zr.deliver.adpter.DividerItemDecoration;
import com.zr.deliver.adpter.OrderAdapter;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.util.Config;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by xia on 2015/5/7.
 */
public class RecordActivity extends Activity {

    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    protected int deliverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_record);
        deliverId = getSharedPreferences(Config.DELIVER_DATA, MODE_PRIVATE).
                getInt(Config.DELIVER_ID, Config.DEFAULT_ID);
        configBar();
        initView();

    }

    private ArrayList<OrderHistoryDelivery> queryOrderData() {

        Cursor c = getContentResolver().query(OrderProvider.ORDER_URI,
                OrderProvider.ORDER_PROJECTION, "deliver_id=?" + " and " + "order_state=?",
                new String[]{deliverId + "", 3 + ""}, null);
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

    private void initView() {
        ArrayList<OrderHistoryDelivery> orderList = queryOrderData();
        recyclerView = (RecyclerView) findViewById(R.id.record_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        mAdapter = new OrderAdapter(this, orderList, false);
        mAdapter.setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

        /* Listeners */
        recyclerView.setOnScrollListener(onScrollListener);
    }

    private void configBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.newest_order));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_today:
                //
                break;
            case R.id.action_yestoday:
                //

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
