package com.fat246.cybercar.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fat246.cybercar.beans.UserInfo;

public class MyApplication extends Application {

    //To Hold UserInfo
    private static UserInfo mUserInfo;

    //全局队列
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue= Volley.newRequestQueue(getApplicationContext());
    }

    //设置 用户信息 并将原来的用户信息返回
    public UserInfo setUserInfo(UserInfo mUserInfo) {

        UserInfo temp = MyApplication.mUserInfo;
        MyApplication.mUserInfo = mUserInfo;
        return temp;
    }

    //得到用户信息
    public UserInfo getUserInfo() {

        if (MyApplication.mUserInfo != null) return MyApplication.mUserInfo;
        else return new UserInfo("","");
    }

    //获取队列
    public static RequestQueue getRequestQueue(){

        return MyApplication.mRequestQueue;
    }
}