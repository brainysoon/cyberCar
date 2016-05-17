package com.fat246.cybercar.beans;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fat246.cybercar.application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/4/30.
 */
public class OrderInfo {

    //请求接口地址
    private static final String MY_ORDERS_POST_URL = "";

    //tag
    private static final String MY_ORDERS_POST_TAG = "MY_ORDERS_POST_TAG";

    //接口常量
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_TYPE = "order_type";
    public static final String USER_NAME = "user_name";
    public static final String USER_PHONE = "user_phone";
    public static final String ORDER_STATUS = "order_status";
    public static final String ORDER_NUM = "order_num";

    //属性
    private String OrderID;
    private String OrderType;
    private String UserName;
    private String UserPhone;
    private String OrderStatus;
    private String OrderNum;

    //构造函数
    public OrderInfo(JSONObject mOrders) {

        try {

            //提取字段
            this.OrderID = mOrders.getString(ORDER_ID);
            this.OrderType = mOrders.getString(ORDER_TYPE);
            this.OrderNum = mOrders.getString(ORDER_NUM);
            this.OrderStatus = mOrders.getString(ORDER_STATUS);
            this.UserPhone = mOrders.getString(USER_PHONE);
            this.UserName = mOrders.getString(USER_NAME);

        } catch (JSONException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * *********************************************************************************************
     * 查询订单请求
     */
    public static void sendMyOrdersPost(JSONObject mParams, final handMyOrdersPost mHand) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MY_ORDERS_POST_URL,
                mParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                mHand.handMyOrdersPostResult(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handMyOrdersPostError(volleyError);
            }
        });

        //setTag
        jsonObjectRequest.setTag(MY_ORDERS_POST_TAG);

        //add
        MyApplication.getRequestQueue().add(jsonObjectRequest);
    }

    //回掉接口
    public interface handMyOrdersPost {

        void handMyOrdersPostResult(JSONObject jsonObject);

        void handMyOrdersPostError(VolleyError volleyError);
    }
    /**
     * *********************************************************************************************
     */

    //get
    public String getOrderID(){return this.OrderID;}
    public String getOrderType(){return this.OrderType;}
    public String getOrderStatus(){return this.OrderStatus;}
    public String getOrderNum(){return this.OrderNum;}
    public String getUserName(){return this.UserName;}
    public String getUserPhone(){return this.UserPhone;}
}
