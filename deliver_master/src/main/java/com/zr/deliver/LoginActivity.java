package com.zr.deliver;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zr.deliver.custom.CustomActivity;
import com.zr.deliver.presenter.LoginPresenter;
import com.zr.deliver.view.IDeliverLoginView;

/**
 * Created by Administrator on 2015/5/6.
 */

public class LoginActivity extends CustomActivity implements IDeliverLoginView {

    /**
     * V模块的实现类，操作界面元素
     */

    private EditText nameEdit, passwordEdit;
    private Button loginBt;
    private LoginPresenter deliverLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        deliverLoginPresenter = new LoginPresenter(this);
        initActionBar();
        initView();
    }

    @Override
    public void initView() {
        //名字
        nameEdit = (EditText) findViewById(R.id.editText);
        String defaultName = deliverLoginPresenter.getDefaultName();
        nameEdit.setText(defaultName);
        nameEdit.setSelection(defaultName.length());
        //密码
        passwordEdit = (EditText) findViewById(R.id.editText2);
        passwordEdit.setText(deliverLoginPresenter.getDefaultPassword());
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

        loginBt = (Button) findViewById(R.id.button);
        loginBt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            deliverLoginPresenter.login();
        }
    }

    @Override
    public void initActionBar() {

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        deliverLoginPresenter.clear();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getUserName() {
        return nameEdit.getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordEdit.getText().toString();
    }

    @Override
    public void showLoading() {
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
    }

    @Override
    public void toMainActivity() {
        startActivity(new Intent(this, OrderActivity.class));
    }

    @Override
    public void showFailedError(int errorId) {
        //根据反馈的错误id界面做出相应

    }
}
