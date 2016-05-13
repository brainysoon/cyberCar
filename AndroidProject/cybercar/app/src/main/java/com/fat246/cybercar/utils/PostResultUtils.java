package com.fat246.cybercar.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.LoginActivity;
import com.fat246.cybercar.fragments.RegisterDetailInfoFragment;

/**
 * Created by Administrator on 2016/5/3.
 */
public class PostResultUtils {

    //一些 状态码标准
    public static final String STATUS = "status";
    public static final int STATUS_SUCCEED = 1;
    public static final int STATUS_DEFEAT = 0;
    public static final int STATUS_SERVER_ERROR = -1;

    public static final int STATUS_ACCOUNT = 1;
    public static final int STATUS_MOBILE = 2;
    public static final int STATUS_EMAIL = 3;

    private PostResultUtils() {
    }

    //对Login post请求的结果做出反应
    public static boolean isLoginSucceed(int resultCode, Context mContext) {

        switch (resultCode) {

            case STATUS_SUCCEED:
                loginSucceed(mContext);

                return true;

            case STATUS_SERVER_ERROR:
                serverError(mContext);

                return false;
            case STATUS_DEFEAT:
                loginDefeat(mContext);

                return false;
        }

        return false;
    }

    //登陆成功
    private static void loginSucceed(Context mContext) {

        Toast.makeText(mContext, "亲，登录成功！", Toast.LENGTH_LONG).show();
    }

    //登录失败
    private static void loginDefeat(Context mContext) {

        Toast.makeText(mContext, "亲，登陆失败了！", Toast.LENGTH_LONG).show();
    }

    //服务器错误
    private static void serverError(Context mContext) {

        Toast.makeText(mContext, "亲，服务器遛弯去了！", Toast.LENGTH_LONG).show();
    }

    //对Reigster post 请求做出正确反映
    public static boolean isRegisterSucceed(int resultCode, AppCompatActivity mActivity,
                                            int status, String UserID) {

        if (resultCode == STATUS_SUCCEED) {

            registerSucceed(mActivity, status, UserID);

            return true;
        } else if (resultCode == STATUS_DEFEAT) {

            registerDefeat(mActivity, status);
            return false;
        } else if (resultCode == STATUS_SERVER_ERROR) {

            registerDefeat(mActivity, status);
            return false;
        }
        return false;
    }

    //对resultCode 处理
    private static void registerSucceed(final AppCompatActivity mActivity, int status, String UserID) {

        showDialog(mActivity, status, UserID);
    }

    //注册失败
    private static void registerDefeat(final AppCompatActivity mActivity, int status) {

        showDefeatDialog(mActivity, status);
    }

    //注册失败的Dialog
    private static void showDefeatDialog(final AppCompatActivity mActivity, int status) {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);

        //设置标题
        mBuilder.setTitle("注册失败");

        mBuilder.setMessage("我也不知道是哪儿出错啦！");

        mBuilder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mBuilder.create().show();
    }

    //弹出提示
    private static void showDialog(final AppCompatActivity mActivity, final int status, final String UserID) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);

        //设置标题
        mBuilder.setTitle("注册成功");

        //setIcon
        mBuilder.setIcon(R.mipmap.ic_launcher);

        //设置类容
        mBuilder.setMessage("亲，你已经成功注册！");

        //设置按钮
        mBuilder.setPositiveButton("完善信息", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Fragment mFragment = RegisterDetailInfoFragment.newInstance(status, UserID);
//                mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.activity_register_framelayout, mFragment).commit();
            }
        });

        //设置跳转
        mBuilder.setNegativeButton("现在去登陆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent mIntent = new Intent(mActivity, LoginActivity.class);

                mActivity.startActivity(mIntent);

                mActivity.finish();
            }
        });

        Dialog mDialog = mBuilder.create();

        //设置点击不消失
        mDialog.setCanceledOnTouchOutside(false);

        mDialog.show();
    }

    //提交详细信息成功
    public static boolean isSubmitSucceed(int resultCode, AppCompatActivity mActivity) {

        if (resultCode == 1) {

            showLoginOnlyDialog(mActivity);

            return true;
        }

        return false;
    }

    //弹出提示
    private static void showLoginOnlyDialog(final AppCompatActivity mActivity) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);

        //设置标题
        mBuilder.setTitle("提交成功");

        //setIcon
        mBuilder.setIcon(R.mipmap.ic_launcher);

        //设置类容
        mBuilder.setMessage("亲，你的详细信息已经提交成功！");

        //设置跳转
        mBuilder.setNegativeButton("现在去登陆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent mIntent = new Intent(mActivity, LoginActivity.class);

                mActivity.startActivity(mIntent);

                mActivity.finish();
            }
        });

        Dialog mDialog = mBuilder.create();

        //设置点击不消失
        mDialog.setCanceledOnTouchOutside(false);

        mDialog.show();
    }
}
