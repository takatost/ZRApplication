package com.zr.sanhua;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * 配送端测试功能：
 * 1，网络变成部分改成volley增强稳定性，减少资源消耗
 * 2，地图部分继续使用高德地图，减少bug，增强稳定性
 * 3，listview修改成ReclverView，增加滑动删除，增加删除动画
 * 4，语音功能部分暂时待定
 * 5，订单部分采取轮询策略，获取最新订单，接受订单，完成订单。
 * 6，历史订单功能暂定
 */

public class TestActivity extends ActionBarActivity {

    private TextView myText;

    private Button myBt;

    private ImageView myImg;

    /**
     * volley的json测试地址和图片测试地址
     */

    private String jsonUrl = "http://czdr-copartner.com/BE/phone/drumbeating/drumbeating?id=31";

    private String imgUrl = "http://czdr-copartner.com/be/5803.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myText = (TextView) findViewById(R.id.my_text);
        myText.setText("第一次提交");
        myBt = (Button) findViewById(R.id.my_button);
        myImg = (ImageView) findViewById(R.id.my_img);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        //字符串请求
        StringRequest sRe = new StringRequest(
                jsonUrl,
                new Response.Listener<String>() {

                    public void onResponse(String arg0) {

                        Log.d("TAG", arg0);
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError arg0) {

            }
        });
        mQueue.add(sRe);
        //json数据的请求
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(jsonUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        mQueue.add(jsonObjectRequest);
        //volley请求图片
        ImageRequest imRe = new ImageRequest(
                imgUrl,
                new Response.Listener<Bitmap>() {

                    public void onResponse(Bitmap arg0) {
                        myImg.setImageBitmap(arg0);
                    }
                }, 800, 800, Bitmap.Config.RGB_565, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError arg0) {

            }
        });
        mQueue.add(imRe);

    }

    public void doClick(View v) {

        switch (v.getId()) {

            case R.id.my_button:

                startActivity(new Intent(TestActivity.this, MapActivity.class));

                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
