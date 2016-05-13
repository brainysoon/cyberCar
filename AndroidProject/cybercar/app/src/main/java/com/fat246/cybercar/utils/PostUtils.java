package com.fat246.cybercar.utils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fat246.cybercar.application.MyApplication;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/2.
 */
public class PostUtils {


    //用户登录的地址
    private static final String USER_LOGIN_ADDRESS = MyApplication.SERVER_ADDRESS + "/login.action";

    //用户注册的地址
    private static final String USER_REGISTER_ADDRESS = MyApplication.SERVER_ADDRESS + "/initUserAction.action";

    //用户提交详细信息地址
    private static final String USER_SUBMIT_DETAIL_INFO_ADDRESS = MyApplication.SERVER_ADDRESS + "/CompleteMsg.action";

    //volley 提交订单地址
    private static final String BOOK_GAS_POST_URL = MyApplication.SERVER_ADDRESS + "/orderInfo.action";

    //volley 提交订单Tag
    private static final String BOOK_GAS_POET_TAG = "BOOK_GAS_POET_TAG";

    //requset tag
    private static final String USER_LOGIN_REQUEST_TAG = "USER_LOGIN_QUEST_TAG";
    private static final String USER_REGISTER_REQUEST_TAG = "USER_REGISTER_QUEST_TAG";
    private static final String USER_SUBMIT_DETAIL_INFO_TAG = "USER_SUBMIT_DETAIL_INFO_TAG";

    /**
     * *********************************************************************************************
     *
     * @param jsonObject 用户登陆需要传的参数
     * @param mHand      处理用户登陆回掉
     */
    public static void sendLoginPost(JSONObject jsonObject, final HandLoginPost mHand) {

        JsonObjectRequest mJsonRequest = new JsonObjectRequest(Request.Method.POST, USER_LOGIN_ADDRESS, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        mHand.handLoginPostResult(jsonObject);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handLoginPostError(volleyError);
            }
        });

        //set tag
        mJsonRequest.setTag(USER_LOGIN_REQUEST_TAG);

        //设置超时时间
        mJsonRequest.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 1, 1.0f));

        MyApplication.getRequestQueue().add(mJsonRequest);

    }

    //处理  登陆请求的接口
    public interface HandLoginPost {

        void handLoginPostResult(JSONObject jsonObject);

        void handLoginPostError(VolleyError volleyError);
    }
    /***********************************************************************************************
     **/

    /**
     * *********************************************************************************************
     *
     * @param jsonObject 用户注册需要的各种参数
     * @param mHand      注册回到接口
     */
    public static void sendRegisterPost(JSONObject jsonObject, final HandRegisterPostResult mHand) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, USER_REGISTER_ADDRESS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                mHand.handRegisterPostResult(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handRegisterPostError(volleyError);
            }
        });

        //setTag
        jsonObjectRequest.setTag(USER_REGISTER_REQUEST_TAG);

        //加入到 请求队列
        MyApplication.getRequestQueue().add(jsonObjectRequest);
    }

    //处理注册的接口
    public interface HandRegisterPostResult {

        void handRegisterPostResult(JSONObject jsonObject);

        void handRegisterPostError(VolleyError volleyError);
    }

    /**
     * *********************************************************************************************
     **/


    /**
     * *********************************************************************************************
     *
     * @param jsonObject 用户详细资料的参数
     * @param mHand      回掉接口
     */
    public static void sendDetailInfoPost(JSONObject jsonObject, final HandDetailInfoPostResult mHand) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, USER_SUBMIT_DETAIL_INFO_ADDRESS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                mHand.handDetailInfoPostResult(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handDetailInfoPostErrot(volleyError);
            }
        });

        //setTag
        jsonObjectRequest.setTag(USER_SUBMIT_DETAIL_INFO_TAG);

        MyApplication.getRequestQueue().add(jsonObjectRequest);
    }

    //处理提交用户详细的接口
    public interface HandDetailInfoPostResult {

        void handDetailInfoPostResult(JSONObject jsonObject);

        void handDetailInfoPostErrot(VolleyError volleyError);
    }

    /***********************************************************************************************
     **/

    /***********************************************************************************************
     * @param mParams 提交订单的参数
     * @param mHand   回掉接口
     */
    public static void sendBookGasPost(JSONObject mParams, final handBookGasPost mHand) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BOOK_GAS_POST_URL, mParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        mHand.handBookGasPostResult(jsonObject);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                mHand.handBookGasPostError(volleyError);
            }
        });

        //setTag
        jsonObjectRequest.setTag(BOOK_GAS_POET_TAG);

        //添加到请求队列
        MyApplication.getRequestQueue().add(jsonObjectRequest);
    }

    //CallBack
    public interface handBookGasPost {

        void handBookGasPostResult(JSONObject jsonObject);

        void handBookGasPostError(VolleyError volleyError);
    }

    /**
     * *********************************************************************************************
     */
}
