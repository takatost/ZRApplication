package com.zr.deliver;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zr.deliver.model.OrderDetail;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.util.Config;

import java.util.ArrayList;

/**
 * Created by xia on 2015/5/7.
 */
public class OrderDetailActivity extends Activity {

    private TextView idText, statusText;
    private ListView goodListView;
    private LayoutInflater inflater;
    private OrderHistoryDelivery order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        order = getIntent().getExtras().getParcelable(Config.ORDER_DETAIL_KEY);
        inflater = LayoutInflater.from(this);
        configBar();
        initView();
    }


    public void doClick(View v) {

        if (v.getId() == R.id.call_bt) {
            Uri uri = Uri.parse("tel:" + order.telephone);
            Intent phIntent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(phIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        if (order == null) return;
        ArrayList<OrderDetail> goodList = queryGoodData();
        if (goodList == null || goodList.size() == 0) return;
        goodListView = (ListView) findViewById(R.id.good_listview);
        View headView = inflater.inflate(R.layout.detail_header, null);
        View footerView = inflater.inflate(R.layout.detail_footer, null);
        GoodAdapter adapter = new GoodAdapter(goodList);
        goodListView.addHeaderView(headView);
        goodListView.addFooterView(footerView);
        goodListView.setAdapter(adapter);

        idText = (TextView) findViewById(R.id.id_text);
        idText.setText(getString(R.string.order_number) + order.id);
        statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText(order.status == 2 ? getString(R.string.order_sending) : "");

    }

    private void configBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.order_detail));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
            }
        }
    }


    private ArrayList<OrderDetail> queryGoodData() {
        Cursor c = getContentResolver().query(OrderProvider.GOOD_URI,
                OrderProvider.GOOD_PROJECTION, "order_id=?",
                new String[]{order.id + ""}, null);
        ArrayList<OrderDetail> goodList = null;
        if (c != null && c.getCount() > 0) {
            goodList = new ArrayList<>();
            c.moveToFirst();
            do {
                OrderDetail order = new OrderDetail();
                order.goodsid = c.getInt(c
                        .getColumnIndex(OrderProvider.GOOD_ID));
                order.price = c.getFloat(c
                        .getColumnIndex(OrderProvider.GOOD_PRICE));
                order.buynum = c.getInt(c
                        .getColumnIndex(OrderProvider.GOOD_NUM));
                order.goodsname = c.getString(c
                        .getColumnIndex(OrderProvider.GOOD_NAME));
                goodList.add(order);
            } while (c.moveToNext());
            c.close();
        }
        return goodList;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class GoodAdapter extends BaseAdapter {

        private ArrayList<OrderDetail> goodsList;

        public GoodAdapter(ArrayList<OrderDetail> dataList) {
            if (dataList == null) {
                goodsList = new ArrayList<>();
            } else {
                this.goodsList = dataList;
            }
        }

        @Override
        public int getCount() {
            return goodsList.size();
        }

        @Override
        public Object getItem(int position) {
            return goodsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OrderDetail good = goodsList.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.goods_item, null);
                TextView desText = (TextView) convertView.findViewById(R.id.goods_des_text);
                desText.setText(good.describeContents());
                TextView priceText = (TextView) convertView.findViewById(R.id.price);
                priceText.setText("￥" + good.price);
                TextView numText = (TextView) convertView.findViewById(R.id.num_text);
                numText.setText("x" + good.buynum);
            }
            return convertView;
        }
    }

}
