package com.zr.sanhua.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class DymanRecord {
    @SerializedName("i")
    Integer id;
    @SerializedName("d")
    Integer deliverymanId;
    @SerializedName("v")
    String deviceId;
    @SerializedName("r")
    Timestamp recordTime;
    @SerializedName("l")
    double longitude;
    @SerializedName("w")
    double latitude;
    @SerializedName("a")
    String address;
    @SerializedName("s")
    Integer status;

//	public Integer getId() {
//		return id;
//	}
//	public void setId(Integer id) {
//		this.id = id;
//	}
//	public Integer getDeliverymanId() {
//		return deliverymanId;
//	}
//	public void setDeliverymanId(Integer deliverymanId) {
//		this.deliverymanId = deliverymanId;
//	}
//	public Integer getStatus() {
//		return status;
//	}
//	public void setStatus(Integer status) {
//		this.status = status;
//	}
//	public String getDeviceId() {
//		return deviceId;
//	}
//	public void setDeviceId(String deviceId) {
//		this.deviceId = deviceId;
//	}
//	public Timestamp getRecordTime() {
//		return recordTime;
//	}
//	public void setRecordTime(Timestamp recordTime) {
//		this.recordTime = recordTime;
//	}
//	public double getLongitude() {
//		return longitude;
//	}
//	public void setLongitude(double longitude) {
//		this.longitude = longitude;
//	}
//	public double getLatitude() {
//		return latitude;
//	}
//	public void setLatitude(double latitude) {
//		this.latitude = latitude;
//	}
//	public String getAddress() {
//		return address;
//	}
//	public void setAddress(String address) {
//		this.address = address;
//	}


}
