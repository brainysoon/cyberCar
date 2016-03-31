package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fat246.cybercar.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterMobileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterMobileFragment extends Fragment {


    public RegisterMobileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterMobileFragment newInstance() {
        RegisterMobileFragment fragment = new RegisterMobileFragment();
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
        return inflater.inflate(R.layout.fragment_register_mobile, container, false);
    }


}
