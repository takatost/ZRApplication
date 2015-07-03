package com.zr.deliver.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.zr.deliver.R;
import com.zr.deliver.model.DeliveryFinish;
import com.zr.deliver.model.OrderHistoryDelivery;
import com.zr.deliver.presenter.OrderPresenter;
import com.zr.deliver.util.Config;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerSwipeAdapter<OrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<OrderHistoryDelivery> mDataSet;
    private boolean isDrag = true;
    private OrderPresenter presenter;

    public OrderAdapter(Context context, OrderPresenter presenter, List<OrderHistoryDelivery> objects, boolean isDrag) {
        mContext = context;
        this.presenter = presenter;
        //这一步是非常有必要的，类似RecyclerView在OnMeasrue时候直接报空，即使是空数据也必须传进来
        if (objects == null) {
            mDataSet = new ArrayList<>();
        } else {
            this.mDataSet = objects;
        }
        this.isDrag = isDrag;
    }

    public void notifyOrderDataSetChanged(List<OrderHistoryDelivery> objects) {
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
                presenter.toDetailActivity(item);

            }
        });
        viewHolder.deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除动作

                DeliveryFinish deliveryFinish = new DeliveryFinish();
                deliveryFinish.deliveryId = mContext.getSharedPreferences(Config.DELIVER_DATA, Context.MODE_PRIVATE)
                        .getInt(Config.DELIVER_ID, Config.DEFAULT_ID);
                deliveryFinish.orderId = mDataSet.get(position).id;
                deliveryFinish.status = 3;
                presenter.finishOrderItem(deliveryFinish);

                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataSet.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataSet.size());
                mItemManger.closeAllItems();
            }
        });

        viewHolder.orderIdText.setText(mContext.getString(R.string.order_number) + item.id);
        viewHolder.adrText.setText(item.address);
        mItemManger.bindView(viewHolder.itemView, position);
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
