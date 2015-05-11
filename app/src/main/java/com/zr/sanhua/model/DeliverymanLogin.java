package com.zr.sanhua.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeliverymanLogin implements Serializable {

    @SerializedName("i")
    public Integer deleverId;
    @SerializedName("p")
    public byte[] pwdAndTime;
    @SerializedName("d")
    public String deviceId;


//	public String getDeviceId() {
//		return deviceId;
//	}
//	public void setDeviceId(String deviceId) {
//		this.deviceId = deviceId;
//	}
//	public Integer getDeleveryId() {
//		return deleveryId;
//	}
//	public void setDeleveryId(Integer deleveryId) {
//		this.deleveryId = deleveryId;
//	}
//	public byte[] getPwdAndTime() {
//		return pwdAndTime;
//	}
//	public void setPwdAndTime(byte[] pwdAndTime) {
//		this.pwdAndTime = pwdAndTime;
//	}


}
