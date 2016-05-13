package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.checkouts.CheckPostResult;
import com.fat246.cybercar.checkouts.CheckUserInfo;
import com.fat246.cybercar.utils.PostUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterMobileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterMobileFragment extends Fragment implements PostUtils.HandRegisterPostResult {

    //View
    private EditText mUserName;
    private EditText mPassword;
    private EditText mConfirmPasswrod;
    private EditText mSecurityCode;
    private Button sendSecurityCode;
    private Button mRegister;

    //验证
    private EventHandler mEventHandler;

    //handler
    private Handler mHandler;

    //国家代码
    private static final String COUNTRY_CODE = "86";

    public RegisterMobileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterMobileFragment newInstance() {
        RegisterMobileFragment fragment = new RegisterMobileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


        //注册短信验证毁掉接口

        mEventHandler = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功

                        mHandler.handleMessage(new Message());
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功

                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(mEventHandler);

        mHandler = new MyHandler();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SMSSDK.unregisterEventHandler(mEventHandler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register_mobile, container, false);
        setRootView(rootView);

        return rootView;
    }

    //setRootView

    private void setRootView(View rootView) {

        findView(rootView);

        setListener();

        setAdapter();
    }

    //设置数据源
    private void setAdapter() {

    }

    //找到View
    private void findView(View rootView) {

        mUserName = (EditText) rootView.findViewById(R.id.fragment_register_mobile_autocompletetextview_username);
        mSecurityCode = (EditText) rootView.findViewById(R.id.fragment_register_mobile_edittext_securitycode);
        sendSecurityCode = (Button) rootView.findViewById(R.id.fragment_register_mobile_button_securitycode);
        mPassword = (EditText) rootView.findViewById(R.id.fragment_register_mobile_edittext_password);
        mConfirmPasswrod = (EditText) rootView.findViewById(R.id.fragment_register_mobile_edittext_confirmpassword);
        mRegister = (Button) rootView.findViewById(R.id.fragment_register_mobile_button_register);
    }

    //设置监听监听事件
    private void setListener() {

        //发送验证码
        sendSecurityCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckUserInfo.checkNumber(mUserName.getText().toString(), mUserName)) {

                    String userName = mUserName.getText().toString();

                    Log.e("phone", userName);

                    //请求获取短信验证码，在监听中返回
                    SMSSDK.getVerificationCode(COUNTRY_CODE, userName);

                }
            }
        });

        //提交注册
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckUserInfo.canRegister(mUserName, mPassword, mConfirmPasswrod, CheckUserInfo.STATUS_MOBILE)) {

                    //提交验证码
                    SMSSDK.submitVerificationCode(COUNTRY_CODE, mUserName.getText().toString().trim(), mSecurityCode.getText().toString().trim());

                }
            }
        });

        //确认密码
        mConfirmPasswrod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                CheckUserInfo.checkIsSamePassword(mConfirmPasswrod.getText().toString(), mPassword.getText().toString(), mConfirmPasswrod);
            }
        });

        //密码
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //清空确认密码
                mConfirmPasswrod.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //处理注册结果
    @Override
    public void handRegisterPostError(VolleyError volleyError) {

        volleyError.printStackTrace();
    }

    @Override
    public void handRegisterPostResult(JSONObject jsonObject) {


        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();
        int resultCode = -1;

        String UserID = "new Exception";
        try {

            resultCode = jsonObject.getInt(CheckPostResult.STATUS);

            UserID = jsonObject.getString(UserInfo.User_ID);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        boolean isSucceed = CheckPostResult.isRegisterSucceed(resultCode, (AppCompatActivity) getActivity(), CheckPostResult.STATUS_MOBILE, UserID);

        if (isSucceed) {

            ((MyApplication) getActivity().getApplication())
                    .replaceUserInfo(new UserInfo(mUserName.getText().toString(),
                            mPassword.getText().toString(), true, false));

        }
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {


            try {

                JSONObject mParams = new JSONObject();

                mParams.put(UserInfo.User_Tel, mUserName.getText().toString().trim());
                mParams.put(UserInfo.User_Password, mPassword.getText().toString().trim());

                PostUtils.sendRegisterPost(mParams, RegisterMobileFragment.this);
            } catch (JSONException ex) {

                ex.printStackTrace();
            }
        }
    }
}
