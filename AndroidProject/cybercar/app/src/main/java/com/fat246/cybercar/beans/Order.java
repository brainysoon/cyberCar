package com.fat246.cybercar.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Administrator on 2016/5/18.
 */
public class Order extends BmobObject {

    private String User_Tel;
    private String Order_ID;
    private String Order_Station;
    private Integer Order_Status;
    private BmobDate Order_Time;
    private String Order_GasClass;
    private Float Order_GasPrice;
    private Float Order_GasNum;

    public Order(String User_Tel, String Order_ID, String Order_Station,
                 Integer Order_Status, BmobDate Order_Time, String Order_GasClass,
                 Float Order_GasPrice, Float Order_GasNum) {

        this.User_Tel = User_Tel;
        this.Order_ID = Order_ID;
        this.Order_Station = Order_Station;
        this.Order_Status = Order_Status;
        this.Order_Time = Order_Time;
        this.Order_GasClass = Order_GasClass;
        this.Order_GasPrice = Order_GasPrice;
        this.Order_GasNum = Order_GasNum;
    }

    //get
    public String getUser_Tel() {
        return this.User_Tel;
    }

    public String getOrder_ID() {
        return this.Order_ID;
    }

    public String getOrder_Station() {
        return this.Order_Station;
    }

    public Integer getOrder_Status() {
        return this.Order_Status;
    }

    public BmobDate getOrder_Time() {
        return this.Order_Time;
    }

    public String getOrder_GasClass() {
        return this.Order_GasClass;
    }

    public Float getOrder_GasPrice() {
        return this.Order_GasPrice;
    }

    public Float getOrder_GasNum() {
        return this.Order_GasNum;
    }
}
