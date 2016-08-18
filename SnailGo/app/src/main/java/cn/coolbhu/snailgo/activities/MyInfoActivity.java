package cn.coolbhu.snailgo.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.callback.CropCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.User;
import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MyInfoActivity extends AppCompatActivity implements CropCallback {

    private CircleImageView mAvatorView;
    private TextInputEditText mNickView;
    private RadioButton mManView;
    private RadioButton mWomanView;
    private TextView mDateView;
    private Button mSubmitView;
    private PtrClassicFrameLayout mPtrFrame;
    private Button mCancleLogin;

    public static Bitmap mAvator;

    //flag
    private boolean isEdit = false;

    private boolean isEditAvator = false;

    public static MyInfoActivity mInstant;

    //user
    public User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        mInstant = this;

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
        mCancleLogin = (Button) findViewById(R.id.cancle_login);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.activity_my_info_ptr);

        initPtr();

        setListener();

        //得到Bitmap
        mAvator = ((BitmapDrawable) mAvatorView.getDrawable()).getBitmap();
    }

    //setListener
    private void setListener() {

        mSubmitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //是够修改
                if (isEdit) {

                    updateUser();
                } else {

                    setEdit();
                    isEdit = true;
                    mSubmitView.setText("提交修改");
                }
            }
        });

        //退出登录
        mCancleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //更新信息
                MyApplication.isLoginSucceed = false;
                MyApplication.mUser = null;

                if (MainActivity.mInstance != null) {

                    MainActivity.mInstance.updateAfterLogOut();
                }

                Intent intent = new Intent(MyInfoActivity.this, LoginActivity.class);

                startActivity(intent);

                MyInfoActivity.this.finish();
            }
        });

        //点击头像
        mAvatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEdit) {

                    Intent mIntent = new Intent(MyInfoActivity.this, CropImageActivity.class);

                    Bundle bundle = new Bundle();

                    bundle.putInt(CropImageActivity.ACTION_KEY, 1);

                    mIntent.putExtras(bundle);

                    startActivity(mIntent);
                } else {

                    Toast.makeText(MyInfoActivity.this, "请先点击修改！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置时间选择
        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(MyInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mDateView.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay).show();
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

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //加载用户信息
    private void loadUserInfo() {

        //用户登录成功的情况下
        if (MyApplication.isLoginSucceed) {

            BmobQuery<User> query = new BmobQuery<>("User");

            query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

            query.findObjects(new FindListener<User>() {

                @Override
                public void done(List<User> list, BmobException e) {

                    if (e == null) {

                        if (list.size() > 0) {

                            mUser = list.get(0);

                            mUser.setTableName("User");

                            //加载头像
                            mUser.getUser_Avator().download(new DownloadFileListener() {

                                @Override
                                public void done(String s, BmobException e) {

                                    if (e == null) {

                                        mAvator = BitmapFactory.decodeFile(s);//图片地址

                                        mAvatorView.setImageBitmap(mAvator);
                                    } else {

                                        Toast.makeText(MyInfoActivity.this, "加载头像失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onProgress(Integer integer, long l) {

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
                    } else {

                        Toast.makeText(MyInfoActivity.this, "加载信息失败，稍后再试！", Toast.LENGTH_SHORT).show();
                    }
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

    //
    private void updateUser() {

        //nick
        mUser.setUser_NickName(mNickView.getText().toString().trim());

        //sex
        Boolean sex = mManView.isChecked();
        mUser.setUser_Sex(sex);

        //birthday
        mUser.setUser_Birthday(mDateView.getText().toString().trim());

        if (isEditAvator) {

            String name = saveAvator(mAvator);

            if (name != null) {

                File f = new File(name);

                if (f.exists()) {

                    final BmobFile avator = new BmobFile(f);

                    avator.upload(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {

                            if (e == null) {

                                mUser.setUser_Avator(avator);
                                Log.e("here", ">>>>" + avator.toString());

                                mUser.update(mUser.getObjectId(), new UpdateListener() {

                                    @Override
                                    public void done(BmobException e) {

                                        if (e == null) {

                                            Toast.makeText(MyInfoActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

                                            isEdit = false;
                                            setUnedit();
                                            mSubmitView.setText("修改信息");

                                            //一定要更新信息
                                            MyApplication.mUser = mUser;

                                            //更新用户信息
                                            if (MainActivity.mInstance != null) {

                                                MainActivity.mInstance.updateUserInfo();
                                            }
                                        } else {

                                            Toast.makeText(MyInfoActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {

                                Toast.makeText(MyInfoActivity.this, "上传图片失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        } else {
            mUser.update(mUser.getObjectId(), new UpdateListener() {

                @Override
                public void done(BmobException e) {

                    if (e == null) {

                        Toast.makeText(MyInfoActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

                        isEdit = false;
                        setUnedit();
                        mSubmitView.setText("修改信息");

                        //一定要更新信息，不然就会多一个接口
                        MyApplication.mUser = mUser;

                        //更新用户信息
                        if (MainActivity.mInstance != null) {

                            MainActivity.mInstance.updateUserInfo();
                        }
                    } else {

                        Toast.makeText(MyInfoActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onSuccess(Bitmap cropped) {

        mAvatorView.setImageBitmap(cropped);

        mAvator = cropped;

        isEditAvator = true;
    }

    @Override
    public void onError() {
        Toast.makeText(this, "截取图片失败！", Toast.LENGTH_SHORT).show();
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
}
