package com.zr.sanhua.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderHistoryDelivery implements Parcelable {

    public Integer id;

    public String telephone;

    public String address;

    public Integer status;

    public Float acost;

    public Float dycost;

    public String comment;

    public List<OrderDetail> lsc;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.telephone);
        dest.writeString(this.address);
        dest.writeValue(this.status);
        dest.writeValue(this.acost);
        dest.writeValue(this.dycost);
        dest.writeString(this.comment);
        dest.writeTypedList(lsc);
    }

    public OrderHistoryDelivery() {
    }

    private OrderHistoryDelivery(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.telephone = in.readString();
        this.address = in.readString();
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.acost = (Float) in.readValue(Float.class.getClassLoader());
        this.dycost = (Float) in.readValue(Float.class.getClassLoader());
        this.comment = in.readString();
        //这里的警告是序列化读取只能读取具体的结构类型，因为服务器给的是List
        this.lsc = in.readArrayList(OrderDetail.class.getClassLoader());
    }

    public static final Parcelable.Creator<OrderHistoryDelivery> CREATOR = new Parcelable.Creator<OrderHistoryDelivery>() {
        public OrderHistoryDelivery createFromParcel(Parcel source) {
            return new OrderHistoryDelivery(source);
        }

        public OrderHistoryDelivery[] newArray(int size) {
            return new OrderHistoryDelivery[size];
        }
    };
}
