package com.zr.deliver;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

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
 * Created by Administrator on 2015/5/6.
 */

public class LoginActivity extends Activity {

    private RequestQueue mQueue;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;
    private EditText nameEdit, passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mPreferences = this.getSharedPreferences(Config.DELIVER_DATA, MODE_PRIVATE);
        editor = mPreferences.edit();
        mQueue = Volley.newRequestQueue(this);
        configBar();
        initView();
    }

    private void initView() {
        nameEdit = (EditText) findViewById(R.id.editText);
        nameEdit.setText(mPreferences.getInt(Config.DELIVER_ID, Config.DEFAULT_ID) + "");
        passwordEdit = (EditText) findViewById(R.id.editText2);
        int password = mPreferences.getInt(Config.DELIVER_PASS, -1);
        passwordEdit.setText(password == -1 ? "" : password + "");
        nameEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = nameEdit.getCompoundDrawables()[2];
                if (drawable == null)
                    return false;
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > nameEdit.getWidth() - nameEdit.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    nameEdit.setText("");
                }
                return false;
            }
        });
    }

    public void doClick(View v) {
        if (v.getId() == R.id.button) {
            sendLoginRequest();
        }
    }

    private void configBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.login));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //首页如果需要账号管理之类的菜单从这里添加
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendLoginRequest() {

        String nameStr = nameEdit.getText().toString();
        String passwordStr = passwordEdit.getText().toString();

        if (TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(passwordStr)) {
            return;
        }

        DeliverymanLogin deliverLogin = new DeliverymanLogin();
        final Integer deliverId = Integer.parseInt(nameStr);
        final Integer password = Integer.parseInt(passwordStr);
        deliverLogin.deviceId = Util.getDeviceId(this);
        deliverLogin.deliveryId = deliverId;
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
                        if (sr != null && sr.status == null) {
                            editor.putInt(Config.DELIVER_ID, deliverId);
                            editor.putInt(Config.DELIVER_PASS, password);
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this, OrderActivity.class));
                            finish();
                        } else {
                            //这里处理登陆异常
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "error ->" + error.getMessage(), error);
                //这里处理登陆异常
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
    protected void onDestroy() {
        super.onDestroy();
        mQueue.stop();
    }
}
