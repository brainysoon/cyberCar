package cn.coolbhu.snailgo.activities.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.utils.FormatUtils;

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

                    queryTel(tel);
                } else {

                    mTel.setError("亲，这是你的手机号么？");
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.activity_register_progressbar);
        linearLayout = (LinearLayout) findViewById(R.id.activity_register_linearlayout);
    }

    private void queryTel(final String tel) {

        BmobQuery<User> query = new BmobQuery<>("User");

        query.addWhereMatches("User_Tel", tel);

        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {

                if (e == null) {

                    if (list.size() > 0) {

                        showLinearLayout();
                        Toast.makeText(RegisterActivity.this, "这个手机号已经注册！", Toast.LENGTH_SHORT).show();
                    } else {

                        //发送短信
                        BmobSMS.requestSMSCode(tel, "cybercar", new QueryListener<Integer>() {
                            @Override
                            public void done(Integer integer, BmobException e) {

                                //总共就三十条短信，不能随便测试了
                                if (e == null) {

                                    //跳转
                                    Intent mIntent = new Intent(RegisterActivity.this, StartRegisterActivity.class);

                                    mIntent.putExtra("Tel", tel);

                                    startActivity(mIntent);

                                    RegisterActivity.this.finish();
                                } else {

                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, "服务器遛弯去了,请稍后再试！", Toast.LENGTH_SHORT).show();

                                }

                                showLinearLayout();
                            }
                        });
                    }
                } else {

                    showLinearLayout();
                    Toast.makeText(RegisterActivity.this, "服务器遛弯去了,请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initTooblar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
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