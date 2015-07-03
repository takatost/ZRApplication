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
import com.zr.deliver.misc.RSACoder;
import com.zr.deliver.model.DeliverymanLogin;
import com.zr.deliver.model.StatefulResponse;
import com.zr.deliver.util.Config;
import com.zr.deliver.util.GsonTools;
import com.zr.deliver.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/30.
 */
public class DeliverLoginBiz implements IDeliverLoginBiz {

    /**
     * M模块实现类用于操作数据
     */

    private SharedPreferences mPreferences;

    private RequestQueue mQueue;

    private String deviceId;

    public DeliverLoginBiz(Context context) {
        mPreferences = context.getSharedPreferences(Config.DELIVER_DATA, context.MODE_PRIVATE);
        mQueue = Volley.newRequestQueue(context);
        deviceId = Util.getDeviceId(context);
    }

    @Override
    public void login(final String username, final String password, final OnRequestListener loginListener) {
        DeliverymanLogin deliverLogin = new DeliverymanLogin();
        final Integer deliveryId = Integer.parseInt(username);
        final Integer word = Integer.parseInt(password);
        deliverLogin.deviceId = deviceId;
        deliverLogin.deliveryId = deliveryId;
        String time = new Timestamp(System.currentTimeMillis()).toString();
        String pwdAndTime = password + ";" + time;
        byte[] encodedData = null;
        try {
            encodedData = RSACoder.encryptByPublicKey(
                    pwdAndTime.getBytes(), Config.pubKeyStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deliverLogin.pwdAndTime = encodedData;
        Gson gson = GsonTools.getGson();
        String jsonStr = gson.toJson(deliverLogin);
        JSONObject object = null;
        try {
            object = new JSONObject(jsonStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", jsonStr);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, Config.DELIVER_LOGIN_URL, object,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "response -> " + response.toString());
                        StatefulResponse sr = GsonTools.getGson().fromJson(
                                response.toString(), StatefulResponse.class);
                        SharedPreferences.Editor editor = mPreferences.edit();
                        if (sr != null && sr.status == null) {
                            editor.putInt(Config.DELIVER_ID, deliveryId);
                            editor.putInt(Config.DELIVER_PASS, word);
                            editor.commit();
                            loginListener.loginSuccess();
                        } else {
                            loginListener.loginFailed(1);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "error ->" + error.getMessage(), error);
                loginListener.loginFailed(2);
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
    public String getDefaultName() {
        return mPreferences.getInt(Config.DELIVER_ID, Config.DEFAULT_ID) + "";
    }

    @Override
    public String getDefaultPassword() {
        return mPreferences.getInt(Config.DELIVER_PASS, 123456) + "";
    }

    @Override
    public void clear() {
        mPreferences = null;
        mQueue.stop();
    }

}
