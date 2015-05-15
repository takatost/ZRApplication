package com.zr.sanhua.adpter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.gson.Gson;
import com.zr.sanhua.OrderDetailActivity;
import com.zr.sanhua.OrderProvider;
import com.zr.sanhua.R;
import com.zr.sanhua.model.DeliveryFinish;
import com.zr.sanhua.model.OrderHistoryDelivery;
import com.zr.sanhua.model.StatefulResponse;
import com.zr.sanhua.util.Config;
import com.zr.sanhua.util.GsonTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderAdapter extends RecyclerSwipeAdapter<OrderAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<OrderHistoryDelivery> mDataSet;
    private boolean isDrag = true;

    public OrderAdapter(Context context, ArrayList<OrderHistoryDelivery> objects, boolean isDrag) {
        mContext = context;
        //这一步是非常有必要的，类似RecyclerView在OnMeasrue时候直接报空，即使是空数据也必须传进来
        if (objects == null) {
            mDataSet = new ArrayList<>();
        } else {
            this.mDataSet = objects;
        }
        this.isDrag = isDrag;
    }

    public void notifyOrderDataSetChanged(ArrayList<OrderHistoryDelivery> objects) {
        if (objects != null && objects.size() > 0) {
            this.mDataSet = objects;
            notifyDataSetChanged();
        }
    }

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        //绑定ViewHolder
        final OrderHistoryDelivery item = mDataSet.get(position);
        viewHolder.swipeLayout.setSwipeEnabled(isDrag);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        viewHolder.swipeLayout.setOnClickListener(new SwipeLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Config.ORDER_DETAIL_KEY, item);
                intent.putExtras(bundle);
                intent.setClass(mContext, OrderDetailActivity.class);
                mContext.startActivity(intent);

            }
        });
        viewHolder.deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除动作
                RequestQueue mQueue = Volley.newRequestQueue(mContext);
                DeliveryFinish deliveryFinish = new DeliveryFinish();
                deliveryFinish.deliveryId = mContext.getSharedPreferences(Config.DELIVER_DATA, Context.MODE_PRIVATE)
                        .getInt(Config.DELIVER_ID, Config.DEFAULT_ID);
                deliveryFinish.orderId = mDataSet.get(position).id;
                deliveryFinish.status = 3;
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
                                    mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                                    mDataSet.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, mDataSet.size());
                                    mItemManger.closeAllItems();
                                    //status==3已完成订单，现在本地只会收到一种订单status==2
                                    ContentValues orderValues = new ContentValues();
                                    orderValues.put(OrderProvider.ORDER_STATE, 3);
                                    mContext.getContentResolver().update(OrderProvider.ORDER_URI, orderValues, "order_id=?",
                                            new String[]{item.id + ""});
                                } else {
                                    //处理完成失败
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
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
        });
        viewHolder.orderIdText.setText(mContext.getString(R.string.order_number) + item.id);
        viewHolder.adrText.setText(item.address);
//      mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView goodImg;
        TextView orderIdText;
        TextView adrText;
        Button deleteBt;

        public MyViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            goodImg = (ImageView) itemView.findViewById(R.id.good_img);
            orderIdText = (TextView) itemView.findViewById(R.id.order_id);
            adrText = (TextView) itemView.findViewById(R.id.order_adress);
            deleteBt = (Button) itemView.findViewById(R.id.delete);
        }
    }
}
