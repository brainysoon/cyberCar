package cn.coolbhu.snailgo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.MainActivity;
import cn.coolbhu.snailgo.activities.forgot.TelActivity;
import cn.coolbhu.snailgo.activities.register.RegisterActivity;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.utils.CheckUserInfo;
import cn.coolbhu.snailgo.utils.PreferencesUtils;
import cn.coolbhu.snailgo.utils.SucceedLoginUtil;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginFragment extends Fragment {

    //View
    private AutoCompleteTextView mUserName;
    private EditText mUserPassword;
    private CheckBox mSavePassword;
    private CheckBox mAutoLogin;
    private Button mLogin;

    private ProgressBar mProgressBar;
    private ScrollView mScrollView;

    private TextView mRegister;
    private TextView mForgot;

    //上下文
    private Context mContext;

    private String userName;
    private String userPassword;


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

//            //给用户提示
//            new SnackBar(getActivity(), "亲，可以读取你的联系人么？", "没问题", new View.OnClickListener() {
//
//                @Override
//                @TargetApi(Build.VERSION_CODES.M)       //版本问题
//                public void onClick(View v) {
//
//                    requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                }
//            });

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

    //可获得用户信息接口
    private void setRootView(View rootView) {

        findView(rootView);

        setListener();

        setSomeThing();
    }

    //设置一些东西
    private void setSomeThing() {

        try {

            if (MyApplication.mUser != null) {

                toSetSomeThing();
            } else {

                PreferencesUtils mPrefernce = PreferencesUtils.getInstance(getContext().getApplicationContext());

                User mUser = mPrefernce.getUserInfo();

                //这个还是得要，Login的时候有用
                MyApplication.mUser = mUser;

                toSetSomeThing();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    //toSetSomeThing
    private void toSetSomeThing() throws Exception {

        mUserName.setText(MyApplication.mUser.getUser_Tel());

        //是否保存密码
        if (PreferencesUtils.getInstance(getContext())
                .isSavePassword(MyApplication.mUser.getUser_Tel())) {

            mSavePassword.setChecked(true);

            mUserPassword.setText(MyApplication.mUser.getUser_Password());
        }

        //是否自动登陆
        if (PreferencesUtils.getInstance(getContext())
                .isAutoLogin(MyApplication.mUser.getUser_Tel())) {

            mAutoLogin.setChecked(true);
        }
    }

    //设置  监听器
    private void setListener() {

        //登陆
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = mUserName.getText().toString();
                userPassword = mUserPassword.getText().toString();

                if (checkBeforeLogin(userName, userPassword)) {

                    //hide  show
                    mScrollView.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);

                    BmobQuery<User> query = new BmobQuery<>("User");

                    query.addWhereEqualTo("User_Tel", userName);
                    query.addWhereEqualTo("User_Password", userPassword);

                    query.findObjects(getContext(), new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {

                            if (list.size() > 0) {

                                Intent mIntnet = new Intent(getContext(), MainActivity.class);

                                startActivity(mIntnet);

                                MyApplication.mUser = list.get(0);
                                MyApplication.isLoginSucceed = true;

                                //更新用户信息
                                if (MainActivity.mInstance != null) {

                                    MainActivity.mInstance.updateUserInfo();
                                }

                                Toast.makeText(getContext(), "登陆成功！", Toast.LENGTH_SHORT).show();

                                //保存一些东西
                                PreferencesUtils.getInstance(getContext()).saveUserInfo(list.get(0));

                                PreferencesUtils.getInstance(getContext())
                                        .saveIsSavePassAndAutoLogin(list.get(0).getUser_Tel(),
                                                mSavePassword.isChecked(), mAutoLogin.isChecked());

                                //更新设备信息
                                SucceedLoginUtil.checkUid(getContext(), MyApplication.mUser);

                                getActivity().finish();
                            } else {

                                Toast.makeText(getContext().getApplicationContext(), "手机号或者密码错误，请重试！", Toast.LENGTH_SHORT).show();
                                reBackToLogin();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                            Toast.makeText(getContext(), "服务器遛弯去了，请稍后再试！", Toast.LENGTH_SHORT).show();
                            reBackToLogin();

                            Log.e("i>>" + i, s);
                        }
                    });
                }
            }
        });

        mAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mSavePassword.isChecked()) {

                    mSavePassword.setChecked(true);
                }
            }
        });

        mSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mSavePassword.isChecked()) {

                    if (mAutoLogin.isChecked()) mAutoLogin.setChecked(false);
                }
            }
        });

        //跳转到注册页面
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent mIntent = new Intent(getContext(), RegisterActivity.class);

                startActivity(mIntent);
            }
        });

        //跳转到修改密码
        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(getContext(), TelActivity.class);

                startActivity(mIntent);
            }
        });
    }

    //找到 相应的View
    private void findView(View rootView) {

        //用户名
        mUserName = (AutoCompleteTextView) rootView.findViewById(R.id.fragment_login_autocompletetextview_user_name);

        //密码
        mUserPassword = (EditText) rootView.findViewById(R.id.fragment_login_edittext_user_password);

        //保存密码
        mSavePassword = (CheckBox) rootView.findViewById(R.id.fragment_login_checkbox_save_password);

        //自动登录
        mAutoLogin = (CheckBox) rootView.findViewById(R.id.fragment_login_checkbox_auto_login);

        //登陆按钮
        mLogin = (Button) rootView.findViewById(R.id.fragment_login_button_login_now);

        //跳转到注册页面
        mRegister = (TextView) rootView.findViewById(R.id.fragment_login_textview_register);

        //bar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.fragment_login_progressbar_loading);

        //content
        mScrollView = (ScrollView) rootView.findViewById(R.id.fragment_login_scrollview_content);

        mForgot = (TextView) rootView.findViewById(R.id.fragment_login_textview_forgot);
    }

    //登陆前的校验
    private boolean checkBeforeLogin(String userName, String userPassword) {

        //检验账户
        if (!(CheckUserInfo.isAccount(userName) ||
                CheckUserInfo.isEmail(userName) ||
                CheckUserInfo.isMobile(userName))) {

            mUserName.setError("亲，有点不对哦！");
            return false;
        }

        //校验密码
        if (!CheckUserInfo.checkPassword(userPassword, mUserPassword)) {

            return false;
        }

        return true;
    }

    //返回到登陆之前的状态
    private void reBackToLogin() {

        mProgressBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);
    }

}
