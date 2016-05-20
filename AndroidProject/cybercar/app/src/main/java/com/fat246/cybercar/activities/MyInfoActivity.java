package com.fat246.cybercar.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.User;
import com.fat246.cybercar.openwidgets.CircleImageView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MyInfoActivity extends AppCompatActivity {

    private CircleImageView mAvatorView;
    private TextInputEditText mNickView;
    private RadioButton mManView;
    private RadioButton mWomanView;
    private TextView mDateView;
    private Button mSubmitView;
    private PtrClassicFrameLayout mPtrFrame;

    private Bitmap mAvator;

    //flag
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        initToolbar();

        initView();

        loadUserInfo();

        setUnedit();
    }

    //initView
    private void initView() {

        //findView
        mAvatorView = (CircleImageView) findViewById(R.id.activity_my_info_image_avator);
        mNickView = (TextInputEditText) findViewById(R.id.activity_my_info_text_nick);
        mManView = (RadioButton) findViewById(R.id.activity_my_info_radiobutton_sexman);
        mWomanView = (RadioButton) findViewById(R.id.activity_my_info_radiobutton_sexwoman);
        mDateView = (TextView) findViewById(R.id.activity_my_info_textview_birthday);
        mSubmitView = (Button) findViewById(R.id.activity_my_info_button_register);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.activity_my_info_ptr);

        initPtr();

        setListener();
    }

    //setListener
    private void setListener() {

        mSubmitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdit) {

                    setUnedit();
                    isEdit = false;
                } else {

                    setEdit();
                    isEdit = true;
                }
            }
        });
    }

    //intiPtr
    private void initPtr() {

        mPtrFrame.setLastUpdateTimeRelateObject(this);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                //开始刷新
                loadUserInfo();

                mPtrFrame.refreshComplete();
            }
        });
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_my_info_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MyInfoActivity.this.finish();
                }
            });
        }
    }

    //加载用户信息
    private void loadUserInfo() {

        //用户登录成功的情况下
        if (MyApplication.isLoginSucceed) {

            BmobQuery<User> query = new BmobQuery<>("User");

            query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

            query.findObjects(MyInfoActivity.this, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {

                    if (list.size() > 0) {

                        Toast.makeText(MyInfoActivity.this, "加载成功！", Toast.LENGTH_SHORT).show();

                        User mUser = list.get(0);

                        //加载头像
                        mUser.getUser_Avator().download(MyInfoActivity.this, new DownloadFileListener() {
                            @Override
                            public void onSuccess(String s) {


                                mAvator = BitmapFactory.decodeFile(s);//图片地址

                                mAvatorView.setImageBitmap(mAvator);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                                Toast.makeText(MyInfoActivity.this, "加载头像失败！", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //设置昵称
                        mNickView.setText(mUser.getUser_NickName());

                        //设置性别
                        if (mUser.getUser_Sex()) {

                            mManView.setChecked(true);
                        } else {

                            mWomanView.setChecked(true);
                        }

                        //设置生日
                        mDateView.setText(mUser.getUser_Birthday());

                    } else {

                        Toast.makeText(MyInfoActivity.this, "加载信息失败，稍后再试！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(int i, String s) {

                    Toast.makeText(MyInfoActivity.this, "加载信息失败，稍后再试！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //设置为不可修改
    private void setUnedit() {

        mNickView.setEnabled(false);
        mManView.setEnabled(false);
        mWomanView.setEnabled(false);
        mDateView.setEnabled(false);
    }

    //设置为可修修改该
    private void setEdit() {
        mNickView.setEnabled(true);
        mManView.setEnabled(true);
        mWomanView.setEnabled(true);
        mDateView.setEnabled(true);
    }
}
