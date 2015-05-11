package com.zr.sanhua.model;

import com.google.gson.annotations.SerializedName;


public class StatefulResponse<T> {
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    @SerializedName("s")
    private Integer status;
    @SerializedName("e")
    private String error;
    @SerializedName("v")
    private T value;

//    private String[] streamIds;
//    private List<byte[]> stream;

//	public T getValue() {
//		return value;
//	}
//
//	public Integer getStatus() {
//		return status;
//	}
//
//	public void setStatus(Integer status) {
//		this.status = status;
//	}
//
//	public void setValue(T value) {
//		this.value = value;
//	}
//
//	public String getError() {
//		return error;
//	}
//
//	public void setError(String error) {
//		this.error = error;
//	}
//
//	public String[] getStreamIds() {
//		return streamIds;
//	}
//
//	public void setStreamIds(String[] streamIds) {
//		this.streamIds = streamIds;
//	}
//
//	public List<byte[]> getStream() {
//		return stream;
//	}
//
//	public void setStream(List<byte[]> stream) {
//		this.stream = stream;
//	}
}
