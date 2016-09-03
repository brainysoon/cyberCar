package com.fat246.gasservice;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Created by ken on 16-7-23.
 */
public class MyApplication extends Application {

    //Bmob 密钥
    private static final String BOMB_APPKEY = "20d6303487c60c4c630c3e6a7b4615d3";

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化Bmob
        Bmob.initialize(this, BOMB_APPKEY);
    }
}
