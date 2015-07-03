package com.zr.deliver.biz;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zr.deliver.OrderProvider;
import com.zr.deliver.model.DeliveryFinish;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.model.StatefulResponse;
import com.zr.deliver.util.Config;
import com.zr.deliver.util.GsonTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/3.
 */
public class OrderBiz implements IOrderBiz {

    private HandlerBindData handler;
    private RequestQueue mQueue;
    private OrderObserver orderObserver;
    private ContentResolver resolver;
    protected int deliverId;
    protected int orderState;

    public OrderBiz(Context context, HandlerBindData handler, int orderState) {
        mQueue = Volley.newRequestQueue(context);
        resolver = context.getContentResolver();
        this.handler = handler;
        this.orderState = orderState;
        if (orderState == 2) {
            orderObserver = new OrderObserver(new Handler());
            resolver.registerContentObserver(
                    OrderProvider.ORDER_URI, false, orderObserver);
        }
        deliverId = context.getSharedPreferences(Config.DELIVER_DATA, context.MODE_PRIVATE).
                getInt(Config.DELIVER_ID, Config.DEFAULT_ID);
    }


    @Override
    public List<OrderHistoryDelivery> queryOrderData() {

        Cursor c = resolver.query(OrderProvider.ORDER_URI,
                OrderProvider.ORDER_PROJECTION, "deliver_id=?" + " and " + "order_state=?",
                new String[]{deliverId + "", orderState + ""}, null);
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
    public void deleteOrderItem(final DeliveryFinish deliveryFinish, final OnRequestListener loginListener) {

        Gson gson = GsonTools.getGson();
        String jsonStr = gson.toJson(deliveryFinish);
        JSONObject object = null;
        try {
            object = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", jsonStr);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST,
                Config.DELIVER_ORDER_FINISH_URL, object,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "response -> " + response.toString());
                        StatefulResponse sr = GsonTools.getGson().fromJson(
                                response.toString(), StatefulResponse.class);
                        if (sr != null && sr.status == null) {
                            //status==3已完成订单，现在本地只会收到一种订单status==2
                            ContentValues orderValues = new ContentValues();
                            orderValues.put(OrderProvider.ORDER_STATE, 3);
                            resolver.update(OrderProvider.ORDER_URI, orderValues, "order_id=?",
                                    new String[]{deliveryFinish.orderId + ""});
                            loginListener.loginSuccess();
                        } else {
                            //处理完成失败
                            loginListener.loginFailed(1);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                loginListener.loginFailed(2);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        mQueue.add(jsonRequest);
    }


    @Override
    public void clear() {
        if (orderObserver != null) {
            resolver.unregisterContentObserver(orderObserver);
        }
        mQueue.stop();

    }

    public interface HandlerBindData {

        void notiDataChanged();

    }


    class OrderObserver extends ContentObserver {

        public OrderObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            Log.e("TAG", "更新订单数据");
            handler.notiDataChanged();
        }
    }
}
