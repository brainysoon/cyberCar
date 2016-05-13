package com.fat246.cybercar.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.checkouts.CheckPostResult;
import com.fat246.cybercar.utils.PostUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class RegisterDetailInfoFragment extends Fragment implements PostUtils.HandDetailInfoPostResult {

    //View
    private EditText mNickName;
    private RadioButton mMen;
    private RadioButton mWomen;
    private TextView mBirthDay;
    private Button mSubmit;
    private EditText mNumber;
    private EditText mEmail;
    private TextInputLayout mNumberLayout;
    private TextInputLayout mEmailLayout;

    private static final String STATUS = "STATUS";
    private static final String User_ID = "User_ID";

    private int status;
    private String UserID;

    public RegisterDetailInfoFragment() {
        // Required empty public constructor
    }


    public static RegisterDetailInfoFragment newInstance(int status, String UserID) {
        RegisterDetailInfoFragment fragment = new RegisterDetailInfoFragment();
        Bundle args = new Bundle();
        args.putInt(STATUS, status);
        args.putString(User_ID, UserID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            status = getArguments().getInt(STATUS);
            UserID = getArguments().getString(User_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register_detail_info, container, false);

        setRootView(rootView);

        return rootView;
    }

    //setView
    private void setRootView(View rootView) {

        findView(rootView);

        setListener();

        setSomeThing();
    }

    //set
    private void setSomeThing() {

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mBirthDay.setText(mYear + "-" + mMonth + "-" + mDay);

        //选中男的
        mMen.setChecked(true);

        switch (status) {

            case CheckPostResult.STATUS_EMAIL:
                mEmailLayout.setVisibility(View.GONE);
                break;

            case CheckPostResult.STATUS_MOBILE:

                mNumberLayout.setVisibility(View.GONE);
                break;

        }
    }

    //findView
    private void findView(View rootView) {

        mNickName = (EditText) rootView.findViewById(R.id.fragment_register_detail_info_edittext_nickname);
        mMen = (RadioButton) rootView.findViewById(R.id.fragment_register_detail_info_radiobutton_sexman);
        mWomen = (RadioButton) rootView.findViewById(R.id.fragment_register_detail_info_radiobutton_sexwoman);
        mBirthDay = (TextView) rootView.findViewById(R.id.fragment_register_detail_info_textview_birthday);
        mSubmit = (Button) rootView.findViewById(R.id.fragment_register_detail_info_button_submit);
        mNumber = (EditText) rootView.findViewById(R.id.fragment_register_detail_info_edittext_number);
        mEmail = (EditText) rootView.findViewById(R.id.fragment_register_detail_info_edittext_email);
        mNumberLayout = (TextInputLayout) rootView.findViewById(R.id.fragment_register_detail_info_textinputlayout_number);
        mEmailLayout = (TextInputLayout) rootView.findViewById(R.id.fragment_register_detail_info_textinputlayout_email);
    }

    //setListener
    private void setListener() {

        //点击保存
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nickName = mNickName.getText().toString().trim();
                String sex = mMen.isChecked() ? "men" : "women";
                String birthday = mBirthDay.getText().toString().trim();
                String number = mNumber.getText().toString().trim();
                String email = mEmail.getText().toString().trim();

                try {

                    JSONObject mParams = new JSONObject();

                    mParams.put(UserInfo.User_NickName, nickName);
                    mParams.put(UserInfo.User_Sex, sex);
                    mParams.put(UserInfo.User_Birthday, birthday);
                    mParams.put(UserInfo.User_Tel, number);
                    mParams.put(UserInfo.User_Email, email);

                    PostUtils.sendDetailInfoPost(mParams, RegisterDetailInfoFragment.this);
                } catch (JSONException ex) {

                    ex.printStackTrace();
                }
            }
        });

        //设置时间选择
        mBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mBirthDay.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });
    }

    @Override
    public void handDetailInfoPostResult(JSONObject jsonObject) {

        int resultCode = -1;

        try {

            resultCode = jsonObject.getInt(CheckPostResult.STATUS);
        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        CheckPostResult.isSubmitSucceed(resultCode, (AppCompatActivity) getActivity());
    }

    @Override
    public void handDetailInfoPostErrot(VolleyError volleyError) {

        volleyError.printStackTrace();
    }
}
