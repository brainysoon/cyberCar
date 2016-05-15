package com.fat246.cybercar.activities.Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fat246.cybercar.R;
import com.fat246.cybercar.utils.FormatUtils;

public class RegisterActivity extends AppCompatActivity {

    //View
    private Toolbar toolbar;
    private EditText mTel;
    private Button send;

    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initTooblar();

        initView();
    }

    //findView
    private void initView() {

        mTel = (EditText) findViewById(R.id.activity_register_edittext_tel);


        send = (Button) findViewById(R.id.activity_register_button_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tel = mTel.getText().toString().trim();

                if (FormatUtils.isTel(tel)) {

                    showProgressBar();

//                    //发送短信
//                    BmobSMS.requestSMSCode(RegisterActivity.this, tel, "", new RequestSMSCodeListener() {
//                        @Override
//                        public void done(Integer integer, BmobException e) {
//
//                            //总共就三十条短信，不能随便测试了
//
//                            if (e == null) {
//
//                                //跳转
//                                Intent mIntent = new Intent(RegisterActivity.this, StartRegisterActivity.class);
//
//                                mIntent.putExtra("Tel", tel);
//
//                                startActivity(mIntent);
//
//                                showLinearLayout();
//                            } else {
//
//                                showLinearLayout();
//                            }
//                        }
//                    });

                    //跳转
                    Intent mIntent = new Intent(RegisterActivity.this, StartRegisterActivity.class);

                    mIntent.putExtra("Tel", tel);

                    startActivity(mIntent);

                    finish();

                    showLinearLayout();
                } else {

                    mTel.setError("亲，这是你的手机号么？");
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.activity_register_progressbar);
        linearLayout = (LinearLayout) findViewById(R.id.activity_register_linearlayout);
    }

    private void initTooblar() {

        View rootView = findViewById(R.id.activity_register_toolbar);

        if (rootView != null) {

            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RegisterActivity.this.finish();
                }
            });
        }
    }

    private void showProgressBar() {

        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showLinearLayout() {
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}