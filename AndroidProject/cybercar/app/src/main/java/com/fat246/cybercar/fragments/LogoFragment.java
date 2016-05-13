package com.fat246.cybercar.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.LoginActivity;
import com.fat246.cybercar.activities.MainActivity;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.checkouts.CheckPostResult;
import com.fat246.cybercar.utils.PostUtils;
import com.fat246.cybercar.utils.PreferencesUtility;

import org.json.JSONException;
import org.json.JSONObject;

public class LogoFragment extends Fragment implements PostUtils.HandLoginPost {

    //View
    private ImageView mImageView;

    //根据配置文件决定到去哪
    private Intent mIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferencesUtility.getInstance(getContext()).isSettingsUserStraight()) {

            mIntent = new Intent(getContext(), MainActivity.class);

        } else {

            mIntent = new Intent(getContext(), LoginActivity.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_logo, container, false);

        findView(rootView);

        setListener();

        return rootView;
    }

    //find view
    private void findView(View rootView) {

        mImageView = (ImageView) rootView.findViewById(R.id.fragment_logo_imageview);
    }

    private void setListener() {

    }


    @Override
    public void onResume() {
        super.onResume();

        //设置透明度为
        mImageView.setAlpha(0.6f);

        //设置动画 并且在动画结束的时候跳转到登陆界面
        mImageView.animate()
                .alpha(1.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        //如果是自动登陆的话尝试自动登陆
                        if (MyApplication.mUserInfo.getIsAutoLogin()) {

                            attmptLogin();
                        } else {

                            startActivity(mIntent);
                            //结束
                            getActivity().finish();
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();

        mImageView.animate().setListener(null);
    }

    //尝试登陆
    private void attmptLogin() {

//        try {
//
//            JSONObject mParams = new JSONObject();
//
////            mParams.put(UserInfo.User_Name, MyApplication.mUserInfo.getUserName());
////            mParams.put(UserInfo.User_Password, MyApplication.mUserInfo.getUserPassword());
//
//            PostUtils.sendLoginPost(mParams, this);
//        } catch (JSONException ex) {
//
//            ex.printStackTrace();
//        }
    }

    //登陆回掉接口
    @Override
    public void handLoginPostResult(JSONObject jsonObject) {

        try {

            JSONObject mResult = new JSONObject();

            int status = mResult.getInt(CheckPostResult.STATUS);

            //是否登陆成功
            if (status == CheckPostResult.STATUS_SUCCEED) {

//                String User_ID = mResult.getString(UserInfo.User_ID);
//
//                MyApplication.mUserInfo.setUserID(User_ID);

                MyApplication.isLoginSucceed = true;
            }

        } catch (JSONException ex) {

            ex.printStackTrace();
        }

        this.getContext().startActivity(mIntent);

        this.getActivity().finish();
    }

    @Override
    public void handLoginPostError(VolleyError volleyError) {

        volleyError.printStackTrace();

        startActivity(mIntent);

        this.getActivity().finish();
    }
}
