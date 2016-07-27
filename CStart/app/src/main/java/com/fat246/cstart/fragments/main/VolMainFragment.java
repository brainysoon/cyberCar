package com.fat246.cstart.fragments.main;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fat246.cstart.R;

public class VolMainFragment extends Fragment {

    //上下文
    private Context mContext = null;

    //回调接口
    private VolMainFragmentCallback mCallback = null;

    public static VolMainFragment newInstance() {
        VolMainFragment fragment = new VolMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        //启动service
        mCallback.startedWeiZhangService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vol_main, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;

        //判断是否可以转换成回调接口
        if (context instanceof VolMainFragmentCallback) {

            mCallback = (VolMainFragmentCallback) context;
        } else {

            throw new RuntimeException(context.toString()
                    + " must implement VolMainFragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallback = null;
        this.mContext = null;
    }

    //一系列回调
    public interface VolMainFragmentCallback {

        //回调启动违章查询的Service
        void startedWeiZhangService();
    }
}
