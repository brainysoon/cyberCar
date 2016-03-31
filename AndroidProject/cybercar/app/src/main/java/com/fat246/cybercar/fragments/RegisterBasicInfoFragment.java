package com.fat246.cybercar.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fat246.cybercar.R;

/**
 * 注册基本信息的Fragment
 */
public class RegisterBasicInfoFragment extends Fragment {

    public RegisterBasicInfoFragment() {
    }

    public static RegisterBasicInfoFragment newInstance() {
        RegisterBasicInfoFragment fragment = new RegisterBasicInfoFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_register_basic_info, container, false);

        setRootView(rootView);

        return rootView;
    }

    //对rootView 进行一些设置
    private void setRootView(View rootView) {

        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_register_basic_info_viewpager);
        TabLayout mTabLayout = (TabLayout) rootView.findViewById(R.id.fragment_register_basic_info_tablayout);

        //set Adapter
        mViewPager.setAdapter(new RegisterMethodAdapter(getChildFragmentManager()));

        //set TabLayout
        mTabLayout.setupWithViewPager(mViewPager);
    }

    //Fragment Adapter
    class RegisterMethodAdapter extends FragmentStatePagerAdapter {

        public RegisterMethodAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RegisterMethodFragmentsHolder.getFragment(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            CharSequence mTitle;

            switch (position) {

                case 0:
                    mTitle = getResources().getText(R.string.register_basic_info_fragment_register_method_account);
                    break;
                case 1:
                    mTitle = getResources().getText(R.string.register_basic_info_fragment_register_method_mobile);
                    break;
                case 2:
                    mTitle = getResources().getText(R.string.register_basic_info_fragment_register_method_email);
                    break;
                default:
                    mTitle = getResources().getText(R.string.register_basic_info_fragment_register_method_mobile);
            }
            return mTitle;
        }
    }

    public static class RegisterMethodFragmentsHolder {

        //保存Fragment 实例
        private static Fragment[] mFragments = new Fragment[3];

        public static Fragment getFragment(int position) {

            //如果为空则实例化一个对象
            if (mFragments[position] == null) {
                switch (position) {

                    case 0:
                        mFragments[0] = RegisterAccountFragment.newInstance();
                        break;
                    case 1:
                        mFragments[1] = RegisterMobileFragment.newInstance();
                        break;
                    case 2:
                        mFragments[2] = RegisterEmailFragment.newInstance();
                        break;
                    default:
                        mFragments[1] = RegisterMobileFragment.newInstance();
                        break;
                }
            }

            return mFragments[position];
        }
    }
}