package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fat246.cybercar.R;

public class IndicatorFragment extends Fragment {

    public static final String WHERE_IMAGE = "WHERE_IMAGE";

    //View
    private ImageView mImageView;
    private TextView mTextView;

    private int mWhere;

    public IndicatorFragment() {
        // Required empty public constructor
    }


    public static IndicatorFragment newInstance(int where) {
        IndicatorFragment fragment = new IndicatorFragment();
        Bundle args = new Bundle();

        args.putInt(WHERE_IMAGE, where);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mWhere = getArguments().getInt(WHERE_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_indicator, container, false);

        initRootView(rootView);

        return rootView;
    }

    //initView
    private void initRootView(View rootView) {

        mImageView = (ImageView) rootView.findViewById(R.id.fragment_indicator_img);
        mTextView = (TextView) rootView.findViewById(R.id.fragment_indicator_text);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initImage();
    }

    //初始化图片
    private void initImage() {

        if (mImageView != null) {

            switch (mWhere) {

                case 0:

                    mImageView.setImageResource(R.drawable.login);
                    mTextView.setText("完善的用户信息维护");
                    break;
                case 1:

                    mImageView.setImageResource(R.drawable.moregas);
                    mTextView.setText("周边加油站及预约加油");
                    break;
                case 2:

                    mImageView.setImageResource(R.drawable.navigate);
                    mTextView.setText("实时导航及语音播报功能");
                    break;
                case 3:

                    mImageView.setImageResource(R.drawable.cars);
                    mTextView.setText("汽车维护");
                    break;
                case 4:

                    mImageView.setImageResource(R.drawable.regulation);
                    mTextView.setText("违章信息查询");
                    break;
                default:
                    mImageView.setImageResource(R.drawable.first);
                    break;
            }
        }
    }
}
