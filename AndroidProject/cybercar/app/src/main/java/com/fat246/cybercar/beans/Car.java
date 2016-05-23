package com.fat246.cybercar.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/5/21.
 */
public class Car extends BmobObject {

    private String Car_Num;
    private String Car_RackNum;
    private String Car_EngineNum;
    private Double Car_Mileage;
    private String Car_Nick;
    private String Car_ModelType;

    private String User_Tel;

    public Car(String Car_Num, String Car_RackNum, String Car_EngineNum, Double Car_Mileage,
               String Car_Nick, String Car_ModelType, String User_Tel) {
        super();

        this.Car_Num = Car_Num;
        this.Car_RackNum = Car_RackNum;
        this.Car_EngineNum = Car_EngineNum;
        this.Car_Mileage = Car_Mileage;
        this.Car_Nick = Car_Nick;
        this.Car_ModelType = Car_ModelType;
        this.User_Tel = User_Tel;
    }

    //get
    public String getCar_Num() {
        return this.Car_Num;
    }

    public String getCar_RackNum() {
        return this.Car_RackNum;
    }

    public String getCar_EngineNum() {
        return this.Car_EngineNum;
    }

    public String getCar_Nick() {
        return this.Car_Nick;
    }

    public Double getCar_Mileage() {
        return this.Car_Mileage;
    }

    public String getCar_ModelType() {
        return this.Car_ModelType;
    }

    public String getUser_Tel() {
        return this.User_Tel;
    }

}
