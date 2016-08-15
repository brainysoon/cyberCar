package cn.coolbhu.snailgo.activities.forgot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.utils.FormatUtils;

public class EditPasswordActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    private TextInputEditText mPass;
    private TextInputEditText mConfPass;
    private TextInputEditText mNum;
    private Button mSubmit;

    private String mTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        //拿到号码
        mTel = getIntent().getStringExtra("Tel");

        initTooblar();

        initView();
    }

    private void initView() {

        mPass = (TextInputEditText) findViewById(R.id.activity_edit_password_pass);
        mConfPass = (TextInputEditText) findViewById(R.id.activity_edit_password_confirm_pass);
        mNum = (TextInputEditText) findViewById(R.id.activity_edit_password_num);
        mSubmit = (Button) findViewById(R.id.activity_edit_password_submit);

        progressBar = (ProgressBar) findViewById(R.id.activity_edit_password_progressbar);
        linearLayout = (LinearLayout) findViewById(R.id.activity_edit_password_linearlayout);

        //
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String pass = mPass.getText().toString().trim();
                String confPass = mConfPass.getText().toString().trim();
                String code = mNum.getText().toString().trim();

                if (pass.equals(confPass)) {

                    if (FormatUtils.isCode(code)) {

                        showProgressBar();

                        BmobSMS.verifySmsCode(EditPasswordActivity.this, mTel, code, new VerifySMSCodeListener() {
                            @Override
                            public void done(BmobException e) {


                                BmobQuery<User> query = new BmobQuery<User>("User");

                                query.addWhereMatches("User_Tel", mTel);

                                query.findObjects(EditPasswordActivity.this, new FindListener<User>() {
                                    @Override
                                    public void onSuccess(List<User> list) {

                                        if (list.size() > 0) {

                                            User mUser = list.get(0);

                                            mUser.setUser_Password(pass);

                                            mUser.setTableName("User");

                                            mUser.update(EditPasswordActivity.this, mUser.getObjectId(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {

                                                    showLayout();
                                                    Toast.makeText(EditPasswordActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();

                                                    EditPasswordActivity.this.finish();
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    Log.e("here2", i + ">>>>" + s);

                                                    Toast.makeText(EditPasswordActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
                                                    showLayout();
                                                }
                                            });
                                        } else {
                                            showLayout();

                                            Toast.makeText(EditPasswordActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                        Log.e("here1", i + ">>>>" + s);

                                        Toast.makeText(EditPasswordActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
                                        showLayout();
                                    }
                                });
                            }
                        });

                    } else {

                        Toast.makeText(EditPasswordActivity.this, "请输入正确验证码！", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(EditPasswordActivity.this, "请输入密码一致！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mConfPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {

                    mConfPass.setTextColor(Color.BLACK);
                }

                String t = mPass.getText().toString().trim();

                if (s.length() > t.length()) {

                    mConfPass.setTextColor(Color.RED);

                    return;
                }

                for (int i = 0; i < s.length(); i++) {

                    if (t.charAt(i) != s.charAt(i)) {

                        mConfPass.setTextColor(Color.RED);

                        return;
                    }
                }

                mConfPass.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    private void showLayout() {

        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {

        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
