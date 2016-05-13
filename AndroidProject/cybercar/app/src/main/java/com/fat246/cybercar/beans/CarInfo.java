package com.fat246.cybercar.beans;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fat246.cybercar.application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarInfo {

    //获取汽车信息的列表
    private static final String CAR_INFO_POST_URL = "";

    //Tag
    private static final String CAR_INFO_POST_TAG = "CAR_INFO_POST_TAG";

    //标准key
    public static final String Car_Num = "Car_Num";
    public static final String Car_Grand = "Car_Grand";
    public static final String Car_Logo = "Car_Logo";
    public static final String Car_Type = "Car_Type";
    public static final String Engine_Type = "Engine_Type";
    public static final String Car_Body_Type = "Car_Body_Type";
    public static final String Car_Mileage = "Mileage";

    //用户的所有的汽车宝贝
    public static List<CarInfo> mCarInfos;

    //汽车的一些属性
    private String CarNum;
    private String CarGrand;
    private String CarLogo;
    private String CarType;
    private String EngineType;
    private String CarBodyType;
    private String CarMileage;

    //JsonObject
    public CarInfo(JSONObject mCar) {

        try {

            this.CarNum = mCar.getString(Car_Num);
            this.CarGrand = mCar.getString(Car_Grand);
            this.CarLogo = mCar.getString(Car_Logo);
            this.CarType = mCar.getString(Car_Type);
            this.EngineType = mCar.getString(Engine_Type);
            this.CarBodyType = mCar.getString(Car_Body_Type);
            this.CarMileage = mCar.getString(Car_Mileage);


        } catch (JSONException ex) {

            ex.printStackTrace();
        }
    }

    public CarInfo(String CarNum, String CarGrand, String CarLogo,
                   String CarType, String EngineType, String CarBodyType, String CarMileage) {

        this.CarNum = CarNum;
        this.CarGrand = CarGrand;
        this.CarLogo = CarLogo;
        this.CarType = CarType;
        this.EngineType = EngineType;
        this.CarBodyType = CarBodyType;
        this.CarMileage = CarMileage;
    }

    /**
     * **********************************************************************************************
     * 得到用户的  汽车列表
     */
    public static void sendCarInfoPost(JSONObject mParams, final canHandCarInfosPostResult mHand) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, CAR_INFO_POST_URL, mParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        mHand.handCarInfosPostResutl(jsonObject);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handCarInfosPostError(volleyError);
            }
        });

        //setTag
        jsonObjectRequest.setTag(CAR_INFO_POST_TAG);

        //add
        MyApplication.getRequestQueue().add(jsonObjectRequest);
    }

    //发送post请求

    //接口
    public interface canHandCarInfosPostResult {

        void handCarInfosPostResutl(JSONObject jsonObject);

        void handCarInfosPostError(VolleyError volleyError);
    }

    /**
     * *********************************************************************************************
     */


    //get
    public String getCarNum() {
        return this.CarNum;
    }

    public String getCarGrand() {
        return this.CarGrand;
    }

    public String getCarType() {
        return this.CarType;
    }

    public String getCarBodyType() {
        return this.CarBodyType;
    }
}
