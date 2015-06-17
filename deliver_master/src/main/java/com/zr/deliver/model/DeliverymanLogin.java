package com.zr.deliver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliverymanLogin implements Parcelable {


    public Integer deliveryId;
    public byte[] pwdAndTime;
    public String deviceId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.deliveryId);
        dest.writeByteArray(this.pwdAndTime);
        dest.writeString(this.deviceId);
    }

    public DeliverymanLogin() {
    }

    private DeliverymanLogin(Parcel in) {
        this.deliveryId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.pwdAndTime = in.createByteArray();
        this.deviceId = in.readString();
    }

    public static final Parcelable.Creator<DeliverymanLogin> CREATOR = new Parcelable.Creator<DeliverymanLogin>() {
        public DeliverymanLogin createFromParcel(Parcel source) {
            return new DeliverymanLogin(source);
        }

        public DeliverymanLogin[] newArray(int size) {
            return new DeliverymanLogin[size];
        }
    };
}
