package com.zr.sanhua.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryFinish implements Parcelable {

    public int orderId;
    public int deliveryId;
    public int status;
    public int cancelId;
    public String description;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.orderId);
        dest.writeInt(this.deliveryId);
        dest.writeInt(this.status);
        dest.writeInt(this.cancelId);
        dest.writeString(this.description);
    }

    public DeliveryFinish() {
    }

    private DeliveryFinish(Parcel in) {
        this.orderId = in.readInt();
        this.deliveryId = in.readInt();
        this.status = in.readInt();
        this.cancelId = in.readInt();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<DeliveryFinish> CREATOR = new Parcelable.Creator<DeliveryFinish>() {
        public DeliveryFinish createFromParcel(Parcel source) {
            return new DeliveryFinish(source);
        }

        public DeliveryFinish[] newArray(int size) {
            return new DeliveryFinish[size];
        }
    };
}
