package com.fat246.cybercar.beans;

public class UserInfo {

    //用户信息配置文件地址
    private static final String USER_INFO_CONFIGURATION="COM_FAT246_CYBERCAR_USER_INFO_CONFIGURATION";

    //属性
    private String UserName;
    private String Password;

    public UserInfo(String UserName,String Password) {

        this.UserName=UserName;
        this.Password=Password;
    }
}
