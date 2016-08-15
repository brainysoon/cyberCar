package cn.coolbhu.snailgo.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.coolbhu.snailgo.BuildConfig;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;

/**
 * Created by ken on 16-7-15.
 * 自动更新App
 */
public class AutoUpdateManager implements DialogInterface.OnClickListener, Runnable {

    //服务器地址
    private static final String AUTO_UPDATE_SERVER_ADDRESS = "http://www.coolbhu.cn/autoupdate.html";

    //数据key
    private static final String VERSION_NAME = "Version_Name";
    private static final String VERSION_CODE = "Version_Code";
    private static final String VERSION_UPDATE_CONTENT = "Version_Update_Content";
    private static final String VERSION_UPDATE_ADDRESS = "Version_Update_Address";

    //数据 包括 1.版本名称 2.版本号 3.版本更新内容 4.版本地址
    private String Version_Name;
    private int Version_Code;
    private String Version_Upadte_Content;
    private String Version_Update_Address;

    //上下文
    private Context context;

    //接口
    private AfterUpdate afterUpdate;

    //下载进度
    private int mProgress;
    private boolean mInteruptFlag;
    private static final int DOWN_UPDATE = 0;
    private static final int DOWN_OVER = 1;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgressDialog.setProgress(mProgress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;

    public AutoUpdateManager(Context context) {

        this.context = context;
    }

    //开始更新
    public void beginUpdate(AfterUpdate afterUpdate) {

        this.afterUpdate = afterUpdate;

        //判断是否有网络链接
        if (ConnectivityUtils.isConnected(context)) {

            //向服务器拉取数据
            pullUpdateInfo();
        } else {

            afterUpdate.toDoAfterUpdate();
        }
    }

    //拉取服务器的信息
    private void pullUpdateInfo() {

        //用Volley请求服务器最新的版本信息
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(AUTO_UPDATE_SERVER_ADDRESS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                paserJsonData(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                volleyError.printStackTrace();

                afterUpdate.toDoAfterUpdate();
            }
        });

        //设置超时时间，以及重复请求次数
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(500, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //设置Tag
        jsonObjectRequest.setTag(AUTO_UPDATE_SERVER_ADDRESS);

        //添加到请求队列里面
        MyApplication.getRequestQueue().add(jsonObjectRequest);
    }

    //解析 Json数据
    private void paserJsonData(JSONObject json) {

        try {

            //版本名称
            Version_Name = json.getString(VERSION_NAME);

            //版本号
            Version_Code = json.getInt(VERSION_CODE);

            //版本更新内容
            Version_Upadte_Content = json.getString(VERSION_UPDATE_CONTENT);

            //版本地址
            Version_Update_Address = json.getString(VERSION_UPDATE_ADDRESS);

            //判断是否要提醒用户更新
            if (isNeedUpdate()) {

                //显示提示Dialog
                showUpdateDialog();
            } else {

                afterUpdate.toDoAfterUpdate();
            }
        } catch (Exception e) {

            e.printStackTrace();

            afterUpdate.toDoAfterUpdate();
        }
    }

    //判断是否需要更新
    private boolean isNeedUpdate() {

        try {

            //判断服务器最新的版本号是否大于当前版本号
            if (Version_Code > BuildConfig.VERSION_CODE) {

                //首先得到跳过的版本号
                int jump_code = PreferencesUtils.getInstance(context).getJumpVersionCode();

                //判断跳过的版本号是否大于当前服务器版本号
                if (Version_Code > jump_code) {

                    return true;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }

        return false;
    }

    //显示更新提示
    private void showUpdateDialog() {

        try {

            //builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            //设置标题
            builder.setTitle("版本更新");

            //更新内容
            builder.setMessage(Version_Upadte_Content);

            //设置图标
            builder.setIcon(R.mipmap.ic_launcher);

            //设置以后更新
            builder.setPositiveButton("以后更新", this);

            //设置立即更新
            builder.setNegativeButton("立即更新", this);

            //设置跳过当前版本
            builder.setNeutralButton("跳过版本", this);

            //创建Dialog 并展示
            AlertDialog dialog = builder.create();

            //设置点击外面不消失
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();

        } catch (Exception e) {

            e.printStackTrace();

            afterUpdate.toDoAfterUpdate();
        }
    }

    //当更新完成后要做的事情
    public interface AfterUpdate {

        void toDoAfterUpdate();
    }

    //Dialog的点击事件
    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case AlertDialog.BUTTON_POSITIVE:
                afterUpdate.toDoAfterUpdate();
                break;

            case AlertDialog.BUTTON_NEGATIVE:

                toUpdateUtils();
                break;

            case AlertDialog.BUTTON_NEUTRAL:

                //设置跳过版本
                PreferencesUtils.getInstance(context).setJumpVersionCode(Version_Code);
                afterUpdate.toDoAfterUpdate();
                break;
        }
    }

    //点击立即更新按钮
    public void toUpdateUtils() {

        //判断是否是wifi链接
        if (ConnectivityUtils.isWifiConnected(context)) {

            toDownlaodApk();
        } else {

            //友情提示用户
            showOurFriendly();
        }
    }

    //友情提示用户
    private void showOurFriendly() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("友情提示");

        builder.setMessage("亲，你用的不是Wifi，是否继续下载？");

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                toDownlaodApk();
                afterUpdate.toDoAfterUpdate();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                afterUpdate.toDoAfterUpdate();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    //去下载
    private void toDownlaodApk() {

        Thread update = new Thread(this);

        showDownlaodProgressBar();

        update.start();
    }

    //显示下载ProgressBar
    private void showDownlaodProgressBar() {

        mProgressDialog = new ProgressDialog(context);

        mProgressDialog.setMessage("正在下载...");

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mProgressDialog.setButton("取消下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mProgressDialog.dismiss();
                mInteruptFlag = true;

                afterUpdate.toDoAfterUpdate();
            }
        });

        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);

        mProgressDialog.show();
    }

    @Override
    public void run() {

        //判空
        if (Version_Update_Address == null) {

            return;
        }
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(Version_Update_Address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            is = conn.getInputStream();

            //保存到的文件夹
            File file = new File(MyApplication.SAVE_PATH + "/Apk/");
            if (!file.exists()) {
                file.mkdir();
            }

            File apkFile = new File(MyApplication.SAVE_PATH + "/Apk/" + Version_Name + ".apk");
            fos = new FileOutputStream(apkFile);
            int count = 0;
            byte buf[] = new byte[1024];
            do {
                int len = is.read(buf);
                if (len != -1) {
                    count += len;
                    mProgress = (int) (((float) count / length) * 100);
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                } else {
                    mHandler.sendEmptyMessage(DOWN_OVER);
                    break;
                }
                fos.write(buf, 0, len);
            } while (!mInteruptFlag);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mProgressDialog.dismiss();
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //安装下载好的版本
    private void installApk() {
        File apkFile = new File(MyApplication.SAVE_PATH + "/Apk/" + Version_Name + ".apk");
        if (!apkFile.exists()) {
            return;
        }
        //开启安装界面
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);

        //如果不杀进程不会跳到安装后打开的页面
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
