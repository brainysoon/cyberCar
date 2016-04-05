package com.fat246.cybercar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterAccountFragment extends Fragment {

    // Instance of Fragment
    private static RegisterAccountFragment mFragment;

    //View
    private ButtonRectangle mRegister;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mConfirmPassword;

    public RegisterAccountFragment() {
        // Required empty public constructor
    }

    //得到新实例， 做成半单利的比较好
    public static RegisterAccountFragment newInstance() {

        if (mFragment == null) {

            mFragment = new RegisterAccountFragment();
            Bundle args = new Bundle();

            mFragment.setArguments(args);
        }

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register_account, container, false);

        setRootView(rootView);

        return rootView;
    }

    //对RootView 设置
    private void setRootView(View rootView) {

        findView(rootView);

        setListener();
    }

    //找到 要用的View
    private void findView(View rootView){

        mRegister=(ButtonRectangle)rootView.findViewById(R.id.fragment_register_account_button_register);
        mUserName=(EditText)rootView.findViewById(R.id.fragment_register_account_edittext_username);
        mPassword=(EditText)rootView.findViewById(R.id.fragment_register_account_edittext_password);
        mConfirmPassword=(EditText)rootView.findViewById(R.id.fragment_register_account_edittext_confirm_password);
    }

    //设置监听事件
    private void setListener(){

        //注册事件
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptRegister();

                jvolleyPost(mUserName.getText().toString(),mPassword.getText().toString());
            }
        });
    }

    //尝试登录
    private boolean attemptRegister(){

        return false;
    }

    private void jvolleyPost(String User_Msg, String User_Password) {

        String url = "http://192.168.0.15:8080/cybercar/initUserAction.action";

        HashMap<String, String> map = new HashMap<>();
        map.put("User_Msg", User_Msg);
        map.put("User_Password", User_Password);

        JSONObject object = new JSONObject(map);

        Log.e("json", object.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_LONG).show();

                Log.e("error", volleyError.toString());
                volleyError.printStackTrace();
            }
        });

        //添加标签
        jsonObjectRequest.setTag("ken");

        //添加到队列
        MyApplication.getRequestQueue().add(jsonObjectRequest);

    }

    private void volleyPost(final String User_Msg, final String User_Password) {

        String url = "http://192.168.0.15:8080/cybercar/initUserAction.action";

        StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();

                Log.e("s",s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(getActivity(),volleyError.getMessage(),Toast.LENGTH_LONG).show();

                Log.e("volleyerror",volleyError.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map=new HashMap<>();

                map.put("User_Msg",User_Msg);
                map.put("User_Password",User_Password);
                return map;
            }
        };

        //setTag
        mStringRequest.setTag("ken");

        MyApplication.getRequestQueue().add(mStringRequest);
    }
}
