package cn.coolbhu.snailgo.activities.register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.callback.CropCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.CropImageActivity;
import cn.coolbhu.snailgo.activities.MainActivity;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.utils.FormatUtils;
import de.hdodenhof.circleimageview.CircleImageView;

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

    public static Bitmap avator;

    //layout
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

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

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
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

                    showProgressBar();

                    String tel = mCodeView.getText().toString().trim();

                    BmobSMS.verifySmsCode(mTel, tel, new UpdateListener() {

                        @Override
                        public void done(BmobException ex) {
                            // TODO Auto-generated method stub
                            if (ex == null) {//短信验证码已验证成功

                                final User mUser = initData();

                                if (mUser != null) {

                                    //上传头像
                                    mUser.getUser_Avator().upload(new UploadFileListener() {
                                        @Override
                                        public void done(BmobException e) {

                                            if (e == null) {

                                                Log.e("Upload>>>", "Succeed");

                                                Log.e("File>>>Name", mUser.getUser_Avator().getFilename());

                                                mUser.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {

                                                        if (e == null) {

                                                            MyApplication.mUser = mUser;
                                                            MyApplication.isLoginSucceed = true;
                                                            MyApplication.mAvator = avator;

                                                            //更新用户信息
                                                            if (MainActivity.mInstance != null) {

                                                                MainActivity.mInstance.updateUserInfo();
                                                            }

                                                            showDialog();
                                                            showLayout();
                                                        } else {

                                                            Toast.makeText(StartRegisterActivity.this, "注册失败，请稍后再试！", Toast.LENGTH_SHORT).show();

                                                            showLayout();
                                                        }
                                                    }
                                                });
                                            } else {

                                                Toast.makeText(StartRegisterActivity.this, "头像上传失败！", Toast.LENGTH_SHORT).show();

                                                showLayout();
                                            }
                                        }
                                    });
                                } else {

                                    showLayout();

                                }

                                Log.i("bmob", "验证通过");
                            } else {

                                Toast.makeText(StartRegisterActivity.this, "验证码不正确，请重新确认！", Toast.LENGTH_SHORT).show();
                                Log.i("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                                showLayout();
                            }
                        }
                    });


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


        //设置时间选择
        mBirthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(StartRegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mBirthdayView.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });
    }

    //dialog
    private void showDialog() {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);

        //设置标题
        mBuilder.setTitle("注册成功");

        //setIcon
        mBuilder.setIcon(R.mipmap.ic_launcher);

        //设置类容
        mBuilder.setMessage("亲，你已经成功注册！");

        //设置跳转
        mBuilder.setPositiveButton("现在去主页面", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent mIntent = new Intent(StartRegisterActivity.this, MainActivity.class);

                StartRegisterActivity.this.startActivity(mIntent);

                StartRegisterActivity.this.finish();
            }
        });

        Dialog mDialog = mBuilder.create();

        //设置点击不消失
        mDialog.setCanceledOnTouchOutside(false);

        mDialog.show();
    }

    //initData
    private User initData() {

        String User_Tel = mTel;
        String User_Password = mPasswordView.getText().toString().trim();
        String User_NickName = mNickNameView.getText().toString().trim();

        Boolean User_Sex;
        if (mManView.isChecked()) {

            User_Sex = true;
        } else {

            User_Sex = false;
        }

        String User_Birthday = mBirthdayView.getText().toString().trim();

        //得到Bitmap

        avator = ((BitmapDrawable) mAvatorView.getDrawable()).getBitmap();

        //保存Bitmap
        String name = saveAvator(avator);

        if (name != null) {

            File f = new File(name);

            if (f.exists()) {

                Log.e("Feil", f.getAbsolutePath());

                BmobFile User_Avator = new BmobFile(f);

                return new User(User_Tel, User_Password, User_NickName,
                        User_Sex, User_Birthday, User_Avator);
            } else {


                Log.e("Feil>>>", "Null");

                return null;
            }
        } else {

            Log.e("Name>>>", "Null");

            return null;
        }
    }

    //saveAvator
    private String saveAvator(Bitmap bm) {

        try {

            Log.e("png>>>>>", "开始保存");

            String name = Calendar.getInstance().getTimeInMillis() + ((int) (Math.random() * 1000)) + ".png";

            File f = new File(MyApplication.USER_AVATOR_DIRCTORY, name);

            if (f.exists()) {

                f.delete();
            }

            name = f.getAbsolutePath();

            Log.e("MyF>>>", MyApplication.USER_AVATOR_DIRCTORY);
            Log.e("f>>>AboslutePath", f.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(f);

            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();

            fos.close();

            Log.e("png>>>>>", "保存成功");

            return name;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }
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

        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        mBirthdayView.setText(time);

        mSendView = (Button) findViewById(R.id.activity_start_register_button_register);

        avator = BitmapFactory.decodeResource(getResources(), R.drawable.avator);

        mCodeView = (TextInputEditText) findViewById(R.id.activity_start_register_text_code);

        linearLayout = (LinearLayout) findViewById(R.id.activity_start_register_linearlayout);
        progressBar = (ProgressBar) findViewById(R.id.activity_start_register_progressbar);
    }

    //验证
    private boolean checkOut() {

        String nick = mNickNameView.getText().toString().trim();

        if (!FormatUtils.isNick(nick)) {

            mNickNameView.setError("昵称为2-8位字母数字则下划线或者中文");

            return false;
        }

        String code = mCodeView.getText().toString().trim();
        if (!FormatUtils.isCode(code)) {

            mCodeView.setError("验证码为6为数字");
        }

        return true;
    }

    //选中图片了
    @Override
    public void onSuccess(Bitmap cropped) {

        mAvatorView.setImageBitmap(cropped);

        avator = cropped;
    }

    @Override
    public void onError() {

        Toast.makeText(this, "截取图片失败！", Toast.LENGTH_SHORT).show();
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
