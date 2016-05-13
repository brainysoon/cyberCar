package com.fat246.cybercar.fragments;


import android.os.Bundle;
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

public class RegisterEmailFragment extends Fragment implements PostUtils.HandRegisterPostResult {

    //View
    private EditText mUserName;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mRegister;


    public RegisterEmailFragment() {
        // Required empty public constructor
    }

    public static RegisterEmailFragment newInstance() {
        RegisterEmailFragment fragment = new RegisterEmailFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_register_email, container, false);

        setRootView(rootView);

        return rootView;
    }

    //setView
    private void setRootView(View rootView) {

        findView(rootView);

        setListener();
    }

    //找到 view
    private void findView(View rootView) {

        mUserName = (EditText) rootView.findViewById(R.id.fragment_register_email_autocompletetexview_username);
        mPassword = (EditText) rootView.findViewById(R.id.fragment_register_email_edittext_password);
        mConfirmPassword = (EditText) rootView.findViewById(R.id.fragment_register_email_edittext_confirm_password);
        mRegister = (Button) rootView.findViewById(R.id.fragment_register_email_button_register);
    }

    //setListener
    private void setListener() {

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUserName.getText().toString();
                String userPassword = mPassword.getText().toString();

                if (CheckUserInfo.canRegister(mUserName, mPassword, mConfirmPassword, CheckUserInfo.STATUS_EMAIL)) {

                    try {

                        JSONObject mParams = new JSONObject();

                        mParams.put(UserInfo.User_Email, userName);
                        mParams.put(UserInfo.User_Password, userPassword);

                        PostUtils.sendRegisterPost(mParams, RegisterEmailFragment.this);

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
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

    //处理post结果
    @Override
    public void handRegisterPostError(VolleyError volleyError) {

        volleyError.printStackTrace();
    }

    @Override
    public void handRegisterPostResult(JSONObject jsonObject) {

        int resultCode = -1;

        String UserID = "new Exception";
        try {

            resultCode = jsonObject.getInt(CheckPostResult.STATUS);

            UserID = jsonObject.getString(UserInfo.User_ID);
        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        boolean isSucceed = CheckPostResult.isRegisterSucceed(resultCode, (AppCompatActivity) getActivity(), CheckPostResult.STATUS_EMAIL, UserID);

        if (isSucceed) {

            ((MyApplication) getActivity().getApplication())
                    .replaceUserInfo(new UserInfo(mUserName.getText().toString(),
                            mPassword.getText().toString(), true, false));

        }
    }
}
