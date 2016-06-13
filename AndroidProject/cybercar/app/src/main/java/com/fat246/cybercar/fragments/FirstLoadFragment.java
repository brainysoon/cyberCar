package com.fat246.cybercar.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.MainActivity;

public class FirstLoadFragment extends Fragment {

    private Button mStart;

    public FirstLoadFragment() {
        // Required empty public constructor
    }


    public static FirstLoadFragment newInstance() {
        FirstLoadFragment fragment = new FirstLoadFragment();
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
        View rootView= inflater.inflate(R.layout.fragment_first_load, container, false);

        initRootView(rootView);

        return rootView;
    }

    private void initRootView(View rootView){

        mStart=(Button)rootView.findViewById(R.id.fragment_first_load_start);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent=new Intent(getContext(), MainActivity.class);

                startActivity(mIntent);

                getActivity().finish();
            }
        });
    }


}
