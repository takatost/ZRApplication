package com.zr.deliver;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.zr.deliver.adpter.DividerItemDecoration;
import com.zr.deliver.adpter.OrderAdapter;
import com.zr.deliver.custom.CustomActivity;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.presenter.OrderPresenter;
import com.zr.deliver.util.Config;
import com.zr.deliver.view.IOrderView;

import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by xia on 2015/5/7.
 */
public class RecordActivity extends CustomActivity implements IOrderView {

    private RecyclerView recyclerView;
    private TextView hintText;
    private OrderAdapter mAdapter;
    private OrderPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_record);
        presenter = new OrderPresenter(this, getOrderTypeState());
        initActionBar();
        initView();
        presenter.loadAllData();

    }


    @Override
    public void initView() {

        hintText = (TextView) findViewById(R.id.hint_text);

        recyclerView = (RecyclerView) findViewById(R.id.record_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

    }

    @Override
    public void initActionBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.order_record));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
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
            case R.id.action_clear:

                //
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public int getOrderTypeState() {
        return 3;
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

        mAdapter = new OrderAdapter(this, presenter, data, true);
        mAdapter.setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void showFailedError(int errorId) {

    }
}
