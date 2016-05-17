package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.navigate.InitNavigateActivity;

public class NavigatePreferFragment extends Fragment {

    //View
    private FloatingActionButton mAction;

    //callBack
    private canToInit mCallBack;

    public NavigatePreferFragment() {
        // Required empty public constructor
    }


    public static NavigatePreferFragment newInstance() {
        NavigatePreferFragment fragment = new NavigatePreferFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        mCallBack = (InitNavigateActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigate_prefer, container, false);

        initRootView(rootView);

        return rootView;
    }

    //initRootView
    private void initRootView(View rootView) {

        findView(rootView);

        setListener();
    }

    //setListener
    private void setListener() {

        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallBack.toInit();
            }
        });
    }

    //findView
    private void findView(View rootView) {

        mAction = (FloatingActionButton) rootView.findViewById(R.id.fragment_navigate_prefer_action_init);
    }

    public interface canToInit {

        void toInit();
    }
}
