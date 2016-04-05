package com.fat246.cybercar.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.MainActivity;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.SnackBar;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    //上下文
    private Context mContext;

    //
    private static final int REQUEST_READ_CONTACTS = 0;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        this.mContext = getContext();

        setAutoComplete();
    }

    //加载  AutoCompleteTextView
    private boolean setAutoComplete() {

        //是否具有权限
        if (isPermission()) {

            getAutoCompleteData();
        }

        return false;
    }

    //拿去电话本数据
    private void getAutoCompleteData() {

    }

    //是否具有权限  没有的话  在运行时 申请一下 权限
    private boolean isPermission() {

        //当  android 版本低于M的时候  只需要 在 AndroidManifest 添加权限就行
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return true;
        }

        //判断 是否具有 运行时权限
        if (mContext.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            return true;
        }

        //是否能申请
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {

            //给用户提示
            new SnackBar(getActivity(), "亲，可以读取你的联系人么？", "没问题", new View.OnClickListener() {

                @Override
                @TargetApi(Build.VERSION_CODES.M)       //版本问题
                public void onClick(View v) {

                    requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                }
            });

        } else {

            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        return false;
    }

    //申请权限回掉
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //权限申请成功
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getAutoCompleteData();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        setRootView(rootView);

        return rootView;
    }

    private void setRootView(View rootView) {

        //用户名
        AutoCompleteTextView mUserName = (AutoCompleteTextView) rootView.findViewById(R.id.fragment_login_autocompletetextview_user_name);

        //密码
        EditText mUserPassword = (EditText) rootView.findViewById(R.id.fragment_login_edittext_user_password);

        //保存密码
        CheckBox mSavePassword = (CheckBox) rootView.findViewById(R.id.fragment_login_checkbox_save_password);

        //自动登录
        CheckBox mAutoLogin = (CheckBox) rootView.findViewById(R.id.fragment_login_checkbox_auto_login);

        //登陆按钮
        ButtonRectangle mLogin = (ButtonRectangle) rootView.findViewById(R.id.fragment_login_button_login_now);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent=new Intent(getActivity(), MainActivity.class);

                startActivity(mIntent);

                getActivity().finish();
            }
        });
    }
}
