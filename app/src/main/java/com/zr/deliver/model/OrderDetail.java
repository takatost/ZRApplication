package com.zr.deliver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDetail implements Parcelable {

    public long id;

    public int orderid;

    public int goodsid;

    public String goodsname;

    public int buynum;

    public float price;

    public String icon;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.orderid);
        dest.writeInt(this.goodsid);
        dest.writeString(this.goodsname);
        dest.writeInt(this.buynum);
        dest.writeFloat(this.price);
        dest.writeString(this.icon);
    }

    public OrderDetail() {
    }

    private OrderDetail(Parcel in) {
        this.id = in.readLong();
        this.orderid = in.readInt();
        this.goodsid = in.readInt();
        this.goodsname = in.readString();
        this.buynum = in.readInt();
        this.price = in.readFloat();
        this.icon = in.readString();
    }

    public static final Parcelable.Creator<OrderDetail> CREATOR = new Parcelable.Creator<OrderDetail>() {
        public OrderDetail createFromParcel(Parcel source) {
            return new OrderDetail(source);
        }

        public OrderDetail[] newArray(int size) {
            return new OrderDetail[size];
        }
    };
}
