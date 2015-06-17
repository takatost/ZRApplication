package com.zr.deliver.frame;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/6/17.
 */
public class CustomEvent<T> implements Parcelable {

    public T customData;

    public String eventTip;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(customData);
        dest.writeString(this.eventTip);
    }

    private CustomEvent(Parcel in) {
        this.customData = (T) in.readValue(customData.getClass().getClassLoader());
        this.eventTip = in.readString();
    }


    public static final Parcelable.Creator<CustomEvent> CREATOR = new Parcelable.Creator<CustomEvent>() {
        public CustomEvent createFromParcel(Parcel source) {
            return new CustomEvent(source);
        }

        public CustomEvent[] newArray(int size) {
            return new CustomEvent[size];
        }
    };


}
