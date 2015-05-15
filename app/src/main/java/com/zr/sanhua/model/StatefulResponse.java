package com.zr.sanhua.model;

public class StatefulResponse<T> {
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public Integer status;
    public String error;
    public T value;

}
