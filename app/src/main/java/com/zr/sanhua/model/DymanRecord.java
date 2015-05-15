package com.zr.sanhua.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DymanRecord implements Parcelable {

    public Integer id;
    public Integer deliverymanId;
    public String deviceId;
    public double longitude;
    public double latitude;
    public String address;
    public Integer status;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.deliverymanId);
        dest.writeString(this.deviceId);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.address);
        dest.writeValue(this.status);
    }

    public DymanRecord() {
    }

    private DymanRecord(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.deliverymanId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.deviceId = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.address = in.readString();
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<DymanRecord> CREATOR = new Parcelable.Creator<DymanRecord>() {
        public DymanRecord createFromParcel(Parcel source) {
            return new DymanRecord(source);
        }

        public DymanRecord[] newArray(int size) {
            return new DymanRecord[size];
        }
    };
}
