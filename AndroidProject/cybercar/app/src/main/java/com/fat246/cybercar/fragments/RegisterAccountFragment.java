package com.fat246.cybercar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;

public class RegisterAccountFragment extends Fragment {

    public RegisterAccountFragment() {
        // Required empty public constructor
    }

    public static RegisterAccountFragment newInstance() {
        RegisterAccountFragment fragment = new RegisterAccountFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
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

        //Button
        Button mRegister = (Button) rootView.findViewById(R.id.register_basic_info_fragment_method_account_register);

        final EditText mUserMsg = (EditText) rootView.findViewById(R.id.register_basic_info_fragment_method_account);
        final EditText mUserPassword = (EditText) rootView.findViewById(R.id.register_basic_info_fragment_method_account_password);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(getActivity(), "墨汁墨汁。。。", Toast.LENGTH_SHORT).show();
                volleyPost(mUserMsg.getText().toString(), mUserPassword.getText().toString());
            }
        });
    }

    private void volleyPost(String User_Msg, String User_Password) {

        String url = "http://192.168.0.15:8080/cybercar/initUserAction.action";

        HashMap<String, String> map = new HashMap<>();
        map.put("User_Msg", User_Msg);
        map.put("User_Password", User_Password);

        JSONObject object = new JSONObject(map);

        Log.e("json",object.toString());

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
            }
        });

        //添加标签
        jsonObjectRequest.setTag("ken");

        //添加到队列
        MyApplication.getRequestQueue().add(jsonObjectRequest);

    }

//    private void volleyPost(final String User_Msg, final String User_Password) {
//
//        String url = "http://192.168.0.15:8080/cybercar/initUserAction.action";
//
//        StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
//
//                Log.e("s",s);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
////                Toast.makeText(getActivity(),volleyError.getMessage(),Toast.LENGTH_LONG).show();
//
//                Log.e("volleyerror",volleyError.toString());
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                Map<String,String> map=new HashMap<>();
//
//                map.put("User_Msg",User_Msg);
//                map.put("User_Password",User_Password);
//                return map;
//            }
//        };
//
//        //setTag
//        mStringRequest.setTag("ken");
//
//        MyApplication.getRequestQueue().add(mStringRequest);
//    }
}
