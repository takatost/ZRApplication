package com.zr.deliver;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zr.deliver.model.OrderDetail;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.util.Config;

import java.util.ArrayList;
import java.util.List;

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
        TextView totalText = (TextView) footerView.findViewById(R.id.total_text);

        StringBuffer buffer = new StringBuffer();
        String totalPrice = getTotalPrice(goodList);
        String totalNum = getTotalNum(goodList);
        buffer.append("共").append(totalNum).append("件").append(",合计").append("￥").append(totalPrice);
        totalText.setText(buffer.toString());


        GoodAdapter adapter = new GoodAdapter(this, goodList);
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

        Log.e("TAG", "当前订单id=" + order.id);
        Cursor c = getContentResolver().query(OrderProvider.GOOD_URI,
                OrderProvider.GOOD_PROJECTION, "order_id=?",
                new String[]{order.id + ""}, null);
        ArrayList<OrderDetail> goodList = null;
        if (c != null && c.getCount() > 0) {
            goodList = new ArrayList<>();
            c.moveToFirst();
            do {
                OrderDetail goods = new OrderDetail();
                goods.goodsid = c.getInt(c
                        .getColumnIndex(OrderProvider.GOOD_ID));
                goods.price = c.getFloat(c
                        .getColumnIndex(OrderProvider.GOOD_PRICE));
                goods.buynum = c.getInt(c
                        .getColumnIndex(OrderProvider.GOOD_NUM));
                goods.icon = c.getString(c
                        .getColumnIndex(OrderProvider.GOOD_ICON));

                Log.e("TAG", "图片地址" + goods.icon);

                goods.goodsname = c.getString(c
                        .getColumnIndex(OrderProvider.GOOD_NAME));
                goodList.add(goods);
            } while (c.moveToNext());
            c.close();
        }
        return goodList;

    }


    private String getTotalPrice(List<OrderDetail> lsc) {
        // TODO Auto-generated method stub
        if (lsc == null || lsc.size() == 0) {
            return 0 + "";
        } else {
            float totalprice = 0;
            for (OrderDetail order : lsc) {
                totalprice += order.price * order.buynum;
            }
            return totalprice + "";
        }

    }

    private String getTotalNum(List<OrderDetail> lsc) {
        // TODO Auto-generated method stub
        if (lsc == null || lsc.size() == 0) {
            return 0 + "";
        } else {
            int totalNum = 0;
            for (OrderDetail order : lsc) {
                totalNum += order.buynum;
            }
            return totalNum + "";
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class GoodAdapter extends BaseAdapter {

        private ArrayList<OrderDetail> goodsList;
        private ImageLoader imageLoader;

        public GoodAdapter(Context context, ArrayList<OrderDetail> dataList) {
            if (dataList == null) {
                goodsList = new ArrayList<>();
            } else {
                this.goodsList = dataList;
            }
            imageLoader = new ImageLoader(Volley.newRequestQueue(context), new BitmapCache());

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
                goodsImg.setImageUrl(good.icon, imageLoader);

            }
            return convertView;
        }
    }

    //由于暂时只有这个一地方有图片下载先做成内部类，如果多个地方使用必须写到外面通用
    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }

}
