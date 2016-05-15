package com.fat246.cybercar.activities.Register;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.CropImageActivity;
import com.fat246.cybercar.openwidgets.CircleImageView;
import com.fat246.cybercar.utils.FormatUtils;
import com.isseiaoki.simplecropview.callback.CropCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class StartRegisterActivity extends AppCompatActivity implements CropCallback {

    public static StartRegisterActivity mInstant;

    //view
    private CircleImageView mAvatorView;
    private TextInputEditText mNickNameView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mConfirmPasswordView;
    private RadioButton mManView;
    private RadioButton mWomenView;
    private TextView mBirthdayView;
    private TextInputEditText mCodeView;
    private Button mSendView;

    public static Bitmap mAvator;

    //Tel
    private String mTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_register);

        //拿到号码
        mTel = getIntent().getStringExtra("Tel");

        initView();

        intiToolbar();
    }

    //initToolbar
    private void intiToolbar() {

        View rootView = findViewById(R.id.activity_start_register_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StartRegisterActivity.this.finish();
                }
            });
        }
    }

    private void initView() {

        findView();

        setListener();

        mInstant = this;
    }

    private void setListener() {

        //点击头像
        mAvatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(StartRegisterActivity.this, CropImageActivity.class);

                Bundle bundle = new Bundle();

                bundle.putInt(CropImageActivity.ACTION_KEY, 0);

                mIntent.putExtras(bundle);

                startActivity(mIntent);
            }
        });

        mSendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkOut()) {


                }
            }
        });

        mConfirmPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 0) {

                    mConfirmPasswordView.setTextColor(Color.BLACK);
                }

                String t = mPasswordView.getText().toString().trim();

                if (s.length() > t.length()) {

                    mConfirmPasswordView.setTextColor(Color.RED);

                    return;
                }

                for (int i = 0; i < s.length(); i++) {

                    if (t.charAt(i) != s.charAt(i)) {

                        mConfirmPasswordView.setTextColor(Color.RED);

                        return;
                    }
                }

                mConfirmPasswordView.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                    if (!FormatUtils.isPassword(mPasswordView.getText().toString().trim())) {

                        mPasswordView.setError("密码为6-16位字符数字或则字母，至少包括两项");
                    }
                }
            }
        });


        mBirthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void findView() {

        mAvatorView = (CircleImageView) findViewById(R.id.activity_start_register_image_avator);
        mNickNameView = (TextInputEditText) findViewById(R.id.activity_start_register_text_nick);
        mPasswordView = (TextInputEditText) findViewById(R.id.activity_start_register_text_password);
        mConfirmPasswordView = (TextInputEditText) findViewById(
                R.id.activity_start_register_text_confirm_password);
        mManView = (RadioButton) findViewById(R.id.activity_start_register_radiobutton_sexman);
        mWomenView = (RadioButton) findViewById(R.id.activity_start_register_radiobutton_sexwoman);
        mBirthdayView = (TextView) findViewById(R.id.activity_start_register_textview_birthday);

        String time=new SimpleDateFormat("YYYY-MM-DD").format(new Date());

        mBirthdayView.setText(time);

        mSendView = (Button) findViewById(R.id.activity_start_register_button_register);

        mAvator = BitmapFactory.decodeResource(getResources(), R.drawable.avator);
    }

    //验证
    private boolean checkOut() {

        String nick = mNickNameView.getText().toString().trim();

        if (!FormatUtils.isNick(nick)) {

            mNickNameView.setError("昵称为3-8位字母数字则下划线或者中文");

            return false;
        }

        String code=mCodeView.getText().toString().trim();
        if (!FormatUtils.isCode(code)){

            mCodeView.setError("验证码为6为数字");
        }

        return true;
    }

    //选中图片了
    @Override
    public void onSuccess(Bitmap cropped) {

        mAvatorView.setImageBitmap(cropped);

        mAvator = cropped;
    }

    @Override
    public void onError() {

        Toast.makeText(this, "截取图片失败！", Toast.LENGTH_SHORT).show();
    }
}
