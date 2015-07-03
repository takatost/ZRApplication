package com.zr.deliver;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.toolbox.NetworkImageView;
import com.zr.deliver.custom.CustomActivity;
import com.zr.deliver.model.OrderDetail;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.presenter.OrderDetailPresenter;
import com.zr.deliver.util.Config;
import com.zr.deliver.view.IOrderDetailView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xia on 2015/5/7.
 */
public class OrderDetailActivity extends CustomActivity implements IOrderDetailView {

    private TextView idText, statusText;
    private ListView goodListView;
    private LayoutInflater inflater;
    private OrderHistoryDelivery order;
    private OrderDetailPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        presenter = new OrderDetailPresenter(this);
        order = getIntent().getExtras().getParcelable(Config.ORDER_DETAIL_KEY);
        inflater = LayoutInflater.from(this);
        configBar();
        presenter.displayData();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
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
    public void showFailedError(int errorId) {

    }

    @Override
    public void bindData(List<OrderDetail> goodsData) {

        if (order == null) return;
        if (goodsData == null || goodsData.size() == 0) return;
        goodListView = (ListView) findViewById(R.id.good_listview);
        View headView = inflater.inflate(R.layout.detail_header, null);
        View footerView = inflater.inflate(R.layout.detail_footer, null);
        TextView totalText = (TextView) footerView.findViewById(R.id.total_text);

        StringBuffer buffer = new StringBuffer();
        String totalPrice = presenter.getTotalPrice(goodsData);
        String totalNum = presenter.getTotalNum(goodsData);
        buffer.append("共").append(totalNum).append("件").append(",合计").append("￥").append(totalPrice);
        totalText.setText(buffer.toString());


        GoodAdapter adapter = new GoodAdapter(this, goodsData);
        goodListView.addHeaderView(headView);
        goodListView.addFooterView(footerView);
        goodListView.setAdapter(adapter);

        idText = (TextView) findViewById(R.id.id_text);
        idText.setText(getString(R.string.order_number) + order.id);
        statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText(order.status == 2 ? getString(R.string.order_sending) : "");

    }

    @Override
    public OrderHistoryDelivery getCurrentOrder() {
        return order;
    }


    class GoodAdapter extends BaseAdapter {

        private List<OrderDetail> goodsList;

        public GoodAdapter(Context context, List<OrderDetail> dataList) {
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
                desText.setText(good.goodsname);
                TextView priceText = (TextView) convertView.findViewById(R.id.price);
                priceText.setText("￥" + good.price);
                TextView numText = (TextView) convertView.findViewById(R.id.num_text);
                numText.setText("x" + good.buynum);
                NetworkImageView goodsImg = (NetworkImageView) convertView.findViewById(R.id.goods_img);
                goodsImg.setImageUrl(good.icon, presenter.getImageLoder());
            }
            return convertView;
        }
    }

}
