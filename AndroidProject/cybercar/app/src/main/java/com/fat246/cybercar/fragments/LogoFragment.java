package com.fat246.cybercar.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.LoginActivity;
import com.fat246.cybercar.activities.MainActivity;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.User;
import com.fat246.cybercar.utils.PreferencesUtility;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class LogoFragment extends Fragment {

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

        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    //find view
    private void findView(View rootView) {

        mImageView = (ImageView) rootView.findViewById(R.id.fragment_logo_imageview);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utils();

        findView(view);
    }

    public void Utils() {
        PreferencesUtility mPrefernce = PreferencesUtility.getInstance(getContext().getApplicationContext());

        User mUser = mPrefernce.getUserInfo();

        Log.e("User", mUser.toString());

        MyApplication.mUser = mUser;

        //判断是否要自动登陆
        if (mPrefernce.isAutoLogin(mUser.getUser_Tel())) {

            BmobQuery<User> query = new BmobQuery<>("User");

            query.addWhereMatches(PreferencesUtility.USER_TEL, mUser.getUser_Tel());
            query.addWhereMatches(PreferencesUtility.USER_PASSWORD, mUser.getUser_Password());

            query.findObjects(getContext(), new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {

                    //登录成功
                    if (list.size() > 0) {

                        MyApplication.isLoginSucceed = true;
                    }

//                    startActivity(mIntent);

                    Log.e("succeed", "》》》》成功");

                }

                @Override
                public void onError(int i, String s) {

//                    startActivity(mIntent);
                    Log.e("defead", "》》》》失败");
                }
            });
        } else {

//            startActivity(mIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mImageView.animate().setDuration(2000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                startActivity(mIntent);

                getActivity().finish();
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();

        mImageView.animate().setListener(null);
    }
}
