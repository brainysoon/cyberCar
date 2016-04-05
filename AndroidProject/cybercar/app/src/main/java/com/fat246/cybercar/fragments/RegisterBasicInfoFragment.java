package com.fat246.cybercar.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fat246.cybercar.R;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * 注册基本信息的Fragment
 */
public class RegisterBasicInfoFragment extends Fragment implements MaterialTabListener{

    //View
    private ViewPager mViewPager;
    private MaterialTabHost mMaterialTabHost;
    private FragmentStatePagerAdapter mAdapter;

    private Context mContext;
    private Resources res;

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

        mContext = getContext();
        res=getActivity().getResources();
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

        //
        findView(rootView);

        //Adapter
        mAdapter = new RegisterMethodAdapter(getChildFragmentManager());

        //set Adapter
        mViewPager.setAdapter(mAdapter);

        //setListener
        setListener();

        //insert Tabs
        insertTabs();
    }

    //insert Tabs
    private void insertTabs() {

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mMaterialTabHost.addTab(
                    mMaterialTabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this)
            );
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {

        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    //得到想要的Icon
    @TargetApi(21)
    private Drawable getIcon(int i) {

        Drawable mDrawable;

        switch (i) {
            case 0:
                mDrawable = res.getDrawable(R.mipmap.ic_perm_identity_white);
                break;
            case 1:
                mDrawable = res.getDrawable(R.mipmap.ic_phone_android_white);
                break;
            case 2:
                mDrawable = res.getDrawable(R.mipmap.ic_email_white);
                break;
            default:
                mDrawable = res.getDrawable(R.mipmap.ic_perm_identity_white);
                break;
        }

        return mDrawable;
    }

    //设置一些监听事件
    private void setListener() {

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                mMaterialTabHost.setSelectedNavigationItem(position);
            }
        });
    }

    //找到一些控件
    private void findView(View rootView) {

        mViewPager = (ViewPager) rootView.findViewById(R.id.fragment_register_basic_info_viewpager);
        mMaterialTabHost = (MaterialTabHost) rootView.findViewById(R.id.fragment_register_basic_info_materialtabhost);

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
                    mTitle = mContext.getText(R.string.register_basic_info_fragment_register_method_account);
                    break;
                case 1:
                    mTitle = mContext.getText(R.string.register_basic_info_fragment_register_method_mobile);
                    break;
                case 2:
                    mTitle = mContext.getText(R.string.register_basic_info_fragment_register_method_email);
                    break;
                default:
                    mTitle = mContext.getText(R.string.register_basic_info_fragment_register_method_mobile);
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