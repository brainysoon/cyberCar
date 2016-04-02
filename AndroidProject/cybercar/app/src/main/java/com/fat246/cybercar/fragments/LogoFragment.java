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
import android.widget.Button;
import android.widget.ImageView;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.LoginActivity;
import com.fat246.cybercar.activities.RegisterActivity;

public class LogoFragment extends Fragment {

    //ImageView
    private ImageView mImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_logo,container,false);
        mImageView=(ImageView)rootView.findViewById(R.id.fragment_logo_imageview);
        setRootView(rootView);

        return rootView;
    }

    private void setRootView(View rootView){

        Button buttonLogin=(Button)rootView.findViewById(R.id.fragment_logo_button_login);
        Button buttonRegister=(Button)rootView.findViewById(R.id.fragment_logo_button_register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent=new Intent(getActivity(),LoginActivity.class);

                startActivity(mIntent);

                getActivity().finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent=new Intent(getActivity(),RegisterActivity.class);

                startActivity(mIntent);

                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //设置透明度为
//        mImageView.setAlpha(0.3f);

//        //设置动画 并且在动画结束的时候跳转到登陆界面
//        mImageView.animate()
//                .alpha(1.0f)
//                .setDuration(1000)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//
//                        Intent mIntent=new Intent(getActivity(), RegisterActivity.class);
//
//                        startActivity(mIntent);
//
//                        getActivity().finish();
//                    }
//                });
    }
}
