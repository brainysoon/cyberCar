package com.fat246.cybercar.application;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import com.afollestad.appthemeengine.ATE;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.fat246.cybercar.R;
import com.fat246.cybercar.permissions.Nammu;
import com.fat246.cybercar.utils.PreferencesUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.thinkland.sdk.android.JuheSDKInitializer;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;

public class MyApplication extends Application {

    //实例
    private static MyApplication mInstance;

    //SMSSDK INFO
    private static final String SMSSDK_APPKEY = "11725853623e3";
    private static final String SMSSDK_APPSECRET = "496c4afbdfea32b78ffb5964385c46d1";

    //Tibmer 相关的四种主题
    private static final String LIGHT_THEME = "light_theme";
    private static final String DARK_THEME = "dark_theme";
    private static final String LIGHT_THEME_NOTOOLBAR = "light_theme_notoolbar";
    private static final String DARK_THEME_NOTOOLBAR = "dark_theme_notoolbar";

    //Bmob 密钥
    private static final String BOMB_APPKEY="20d6303487c60c4c630c3e6a7b4615d3";

    //是否登陆成功
    public static boolean isLoginSucceed = false;

    //头像
    public static Bitmap mAvator;

    //
    public static Uri mAvatorUri;

    //全局队列
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化实例
        mInstance = this;

        //初始化请求队列
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        //从配置文件中出事话用户信息
//        mUserInfo = PreferencesUtility.getInstance(getApplicationContext()).getUserInfo();

        //初始化SMSSDK
        SMSSDK.initSDK(this, SMSSDK_APPKEY, SMSSDK_APPSECRET);

        //初始化百度地图API
        SDKInitializer.initialize(this);

        //初始化聚合数据
        JuheSDKInitializer.initialize(getApplicationContext());

        //TIMBER 相关的初始化
        initTimber();

        //初始化Bmob
        Bmob.initialize(this,BOMB_APPKEY);
    }

    //Timber
    private void initTimber() {

        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();

        ImageLoader.getInstance().init(localImageLoaderConfiguration);

//        L.writeLogs(false);
//        L.disableLogging();
//        L.writeDebugLogs(false);

        //权限相关
        Nammu.init(this);

        //确定配置文件的主题
        if (!ATE.config(this, LIGHT_THEME).isConfigured()) {
            ATE.config(this, LIGHT_THEME)
                    .activityTheme(R.style.AppThemeLight)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        } else if (!ATE.config(this, DARK_THEME).isConfigured()) {
            ATE.config(this, DARK_THEME)
                    .activityTheme(R.style.AppThemeDark)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        } else if (!ATE.config(this, LIGHT_THEME_NOTOOLBAR).isConfigured()) {
            ATE.config(this, LIGHT_THEME_NOTOOLBAR)
                    .activityTheme(R.style.AppThemeLight)
                    .coloredActionBar(false)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        } else if (!ATE.config(this, DARK_THEME_NOTOOLBAR).isConfigured()) {
            ATE.config(this, DARK_THEME_NOTOOLBAR)
                    .activityTheme(R.style.AppThemeDark)
                    .coloredActionBar(false)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .coloredNavigationBar(true)
                    .usingMaterialDialogs(true)
                    .commit();
        }
    }

    //设置 用户信息 并将原来的用户信息返回
//    public UserInfo replaceUserInfo(UserInfo mUserInfo) {
//
//        UserInfo temp = MyApplication.mUserInfo;
//
//        MyApplication.mUserInfo = mUserInfo;
//        PreferencesUtility.getInstance(getApplicationContext()).saveUserInfo(mUserInfo);
//        return temp;
//    }

    //获取队列
    public static RequestQueue getRequestQueue() {

        return MyApplication.mRequestQueue;
    }

    //判断用户是否登陆


    //get
    public MyApplication getInstance() {

        return mInstance;
    }
}