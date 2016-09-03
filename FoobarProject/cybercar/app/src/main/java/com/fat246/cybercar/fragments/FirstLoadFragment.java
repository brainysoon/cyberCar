package com.fat246.cybercar.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.MainActivity;

import github.chenupt.springindicator.SpringIndicator;

public class FirstLoadFragment extends Fragment {

    //View
    private Button mStart;
    private ViewPager mViewPager;
    private SpringIndicator mSpringIndicator;

    private MyAdapter mAdapter;

    public FirstLoadFragment() {

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
        View rootView = inflater.inflate(R.layout.fragment_first_load, container, false);

        initRootView(rootView);

        return rootView;
    }

    private void initRootView(View rootView) {

        mStart = (Button) rootView.findViewById(R.id.fragment_first_load_start);
        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_first_load_pager);
        mSpringIndicator = (SpringIndicator) rootView.findViewById(R.id.fragment_first_load_indicator);

        //设置监听事件
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(getContext(), MainActivity.class);

                startActivity(mIntent);

                getActivity().finish();
            }
        });

        //Adapter
        mAdapter = new MyAdapter(getChildFragmentManager());

        mViewPager.setAdapter(mAdapter);

        //绑定
        mSpringIndicator.setViewPager(mViewPager);

        mSpringIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        public Fragment[] fragments = new Fragment[5];

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {

            if (fragments[position] == null) {

                fragments[position] = IndicatorFragment.newInstance(position);
            }

            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position + 1 + "";
        }
    }

    //showButton
    public void showButton() {

        this.mStart.setVisibility(View.VISIBLE);
    }

    //hideButton
    private void hideButton() {

        this.mStart.setVisibility(View.INVISIBLE);
    }

    //setButton
    private void setButton(int positon) {

        if (positon == 4) {

            FirstLoadFragment.this.showButton();
        } else {

            FirstLoadFragment.this.hideButton();
        }
    }
}
