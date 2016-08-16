package cn.coolbhu.snailgo;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import cn.bmob.v3.Bmob;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.permissions.Nammu;

/**
 * Created by ken on 16-7-25.
 */
public class MyApplication extends Application {

    public static final String APP_NAME = "snailgo";

    //Bmob 密钥
    private static final String BOMB_APPKEY = "20d6303487c60c4c630c3e6a7b4615d3";

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public MyApplication() {
        super();
    }

    public static boolean isLoginSucceed = true;

    //保存头像的位置
    public static String USER_AVATOR_DIRCTORY;

    //全局User
    public static User mUser;
    public static Bitmap mAvator;

    //全局请求队列
    private static RequestQueue mRequestQueue;

    //保存文件的位置
    public static String SAVE_PATH;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        //获得保存头像的位置
        MyApplication.USER_AVATOR_DIRCTORY = this.getExternalCacheDir().getAbsolutePath();

        //初始化请求队列
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        //初始化更新Apk放的位置
        MyApplication.SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

        //初始化Bmob
        Bmob.initialize(this, BOMB_APPKEY);

        initMusic();
    }

    private void initMusic() {

        //ImageLoader Configuration
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(localImageLoaderConfiguration);

        L.writeLogs(false);
        L.disableLogging();
        L.writeDebugLogs(false);

        //检查权限
        Nammu.init(this);
    }

    //获取队列
    public static RequestQueue getRequestQueue() {

        return MyApplication.mRequestQueue;
    }
}
