package com.fat246.cybercar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.checkouts.CheckPostResult;
import com.fat246.cybercar.checkouts.CheckUserInfo;
import com.fat246.cybercar.utils.PostUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterAccountFragment extends Fragment implements PostUtils.HandRegisterPostResult {

    // Instance of Fragment
    private static RegisterAccountFragment mFragment;

    //View
    private Button mRegister;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_register_account_recyclerview_root);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setHasFixedSize(true);
//
//        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
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
    private void findView(View rootView) {

        mRegister = (Button) rootView.findViewById(R.id.fragment_register_account_button_register);
        mUserName = (EditText) rootView.findViewById(R.id.fragment_register_account_edittext_username);
        mPassword = (EditText) rootView.findViewById(R.id.fragment_register_account_edittext_password);
        mConfirmPassword = (EditText) rootView.findViewById(R.id.fragment_register_account_edittext_confirm_password);
    }

    //设置监听事件
    private void setListener() {

        //注册事件
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUserName.getText().toString().trim();
                String passwrd = mPassword.getText().toString().trim();

                //注册
                if (CheckUserInfo.canRegister(mUserName, mPassword, mConfirmPassword, CheckUserInfo.STATUS_ACCTOUNT)) {

//                    try {
//
//                        JSONObject mParams = new JSONObject();
//
////                        mParams.put(UserInfo.User_Name, mUserName);
////                        mParams.put(UserInfo.User_Password, mPassword);
//
//                        PostUtils.sendRegisterPost(mParams, RegisterAccountFragment.this);
//
//                    } catch (JSONException ex) {
//
//                        ex.printStackTrace();
//                    }

                }
            }
        });

        //确认密码
        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                CheckUserInfo.checkIsSamePassword(s.toString(), mPassword.getText().toString(), mConfirmPassword);
            }
        });

        //密码
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //清空确认密码
                mConfirmPassword.setText("");
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

        int resultCode = -1;

        String UserID = "new Excption";
        try {

            resultCode = jsonObject.getInt(CheckPostResult.STATUS);

//            UserID = jsonObject.getString(UserInfo.User_ID);
        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        boolean isSucceed = CheckPostResult.isRegisterSucceed(resultCode, (AppCompatActivity) getActivity(), CheckPostResult.STATUS_ACCOUNT, UserID);

        if (isSucceed) {

//            ((MyApplication) getActivity().getApplication()).replaceUserInfo(new UserInfo(mUserName.getText().toString(),
//                    mPassword.getText().toString(), true, false));

        }
    }
}
