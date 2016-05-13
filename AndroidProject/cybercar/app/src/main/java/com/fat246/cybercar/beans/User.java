package com.fat246.cybercar.beans;

import java.io.File;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/5/13.
 */
public class User extends BmobObject {

    //一些 标准的key
    public String User_Tel;
    public String User_Password;
    public String User_NickName;
    public Boolean User_Sex;
    public String User_Birthday;
    public File User_Avator;
}
