package com.zr.deliver.biz;import com.android.volley.toolbox.ImageLoader;import com.zr.deliver.model.OrderDetail;import java.util.List;/** * Created by Administrator on 2015/7/3. */public interface IOrderDetailBiz {    List<OrderDetail> queryDetailData(int orderId);    ImageLoader createImageLoder();    String calculationTotalPrice(List<OrderDetail> goodsList);    String calculationTotalNum(List<OrderDetail> goodsList);}