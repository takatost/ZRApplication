package com.zr.deliver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryPolling implements Parcelable {

    public Integer dymanId;
    public double longitude;
    public double latitude;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.dymanId);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public DeliveryPolling() {
    }

    private DeliveryPolling(Parcel in) {
        this.dymanId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Parcelable.Creator<DeliveryPolling> CREATOR = new Parcelable.Creator<DeliveryPolling>() {
        public DeliveryPolling createFromParcel(Parcel source) {
            return new DeliveryPolling(source);
        }

        public DeliveryPolling[] newArray(int size) {
            return new DeliveryPolling[size];
        }
    };
}
