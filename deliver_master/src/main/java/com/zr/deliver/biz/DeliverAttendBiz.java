package com.zr.deliver.biz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zr.deliver.model.DymanRecord;
import com.zr.deliver.model.StatefulResponse;
import com.zr.deliver.util.Config;
import com.zr.deliver.util.GsonTools;
import com.zr.deliver.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/1.
 */
public class DeliverAttendBiz implements IDeliverAttendBiz {

    private SharedPreferences mPreferences;
    private RequestQueue mQueue;
    private String deviceId;
    private int state;

    public DeliverAttendBiz(Context context) {
        mPreferences = context.getSharedPreferences(Config.DELIVER_DATA, context.MODE_PRIVATE);
        mQueue = Volley.newRequestQueue(context);
        deviceId = Util.getDeviceId(context);
        state = mPreferences.getInt(Config.DELIVER_STATUS, 0);
    }

    @Override
    public void attend(Double latitude, Double longitude, String currentAddress, final OnRequestListener loginListener) {
        DymanRecord dr = new DymanRecord();
        dr.deliverymanId = mPreferences.getInt(Config.DELIVER_ID, -1);
        dr.deviceId = deviceId;
        dr.latitude = latitude;
        dr.longitude = longitude;
        dr.address = currentAddress;
        Gson gson = GsonTools.getGson();
        String jsonStr = gson.toJson(dr);
        JSONObject object = null;
        try {
            object = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", jsonStr);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST,
                state == 0 ? Config.DELIVER_SIGNIN_URL : Config.DELIVER_SIGNOUT_URL, object,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "response -> " + response.toString());
                        StatefulResponse sr = GsonTools.getGson().fromJson(
                                response.toString(), StatefulResponse.class);
                        if (sr != null && sr.status == null) {
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putInt(Config.DELIVER_STATUS, state == 0 ? 1 : 0);
                            editor.commit();
                            loginListener.loginSuccess();
                        } else {
                            loginListener.loginFailed(0);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                //签到签出失败
                loginListener.loginFailed(1);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        mQueue.add(jsonRequest);

    }

    @Override
    public int getDefaultState() {
        return state;
    }

    @Override
    public void clear() {
        mPreferences = null;
        mQueue.stop();
    }
}
