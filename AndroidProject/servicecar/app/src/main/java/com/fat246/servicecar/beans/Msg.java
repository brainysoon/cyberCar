package com.fat246.servicecar.beans;

/**
 * Created by Administrator on 2016/6/2.
 */
public class Msg {

    private String User_Tel;
    private String Msg_Content;
    private String Car_Num;

    public Msg(String User_Tel, String Msg_Content, String Car_Num) {

        this.User_Tel = User_Tel;
        this.Msg_Content = Msg_Content;
        this.Car_Num = Car_Num;
    }

    public String getUser_Tel() {
        return this.User_Tel;
    }

    public String getMsg_Content() {
        return this.Msg_Content;
    }

    public String getCar_Num() {
        return this.Car_Num;
    }
}
