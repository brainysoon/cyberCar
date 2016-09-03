package com.fat246.cybercar.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/5/13.
 */
public class User extends BmobObject {

    //一些 字段
    private String User_Tel;
    private String User_Password;
    private String User_NickName;
    private Boolean User_Sex;
    private String User_Birthday;
    private BmobFile User_Avator;

    public User(String User_Tel, String User_Password,
                String User_NickName, Boolean User_Sex, String User_Birthday,
                BmobFile User_Avator) {

        this.User_Tel = User_Tel;
        this.User_Password = User_Password;
        this.User_NickName = User_NickName;
        this.User_Sex = User_Sex;
        this.User_Birthday = User_Birthday;
        this.User_Avator = User_Avator;

    }

    //get
    public String getUser_Tel() {
        return this.User_Tel;
    }

    public String getUser_Password() {
        return this.User_Password;
    }

    public String getUser_NickName() {
        return this.User_NickName;
    }

    public String getUser_Birthday() {
        return this.User_Birthday;
    }

    public BmobFile getUser_Avator() {
        return this.User_Avator;
    }

    public Boolean getUser_Sex() {
        return this.User_Sex;
    }

    //set
    public void setUser_Password(String User_Password) {
        this.User_Password = User_Password;
    }

    public void setUser_NickName(String User_NickName) {
        this.User_NickName = User_NickName;
    }

    public void setUser_Sex(Boolean User_Sex) {
        this.User_Sex = User_Sex;
    }

    public void setUser_Birthday(String User_Birthday) {
        this.User_Birthday = User_Birthday;
    }

    public void setUser_Avator(BmobFile User_Avator) {
        this.User_Avator = User_Avator;
    }
}
