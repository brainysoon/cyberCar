package com.fat246.cybercar.beans;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2016/6/1.
 */
public class MyBmobInstallation extends BmobInstallation {

    private String uid;

    public MyBmobInstallation(Context context) {
        super(context);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
