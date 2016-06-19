package com.fat246.cybercar.application;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.afollestad.appthemeengine.ATE;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.cheshouye.api.client.WeizhangIntentService;
import com.fat246.cybercar.R;
import com.fat246.cybercar.beans.User;
import com.fat246.cybercar.permissions.Nammu;
import com.fat246.cybercar.utils.PreferencesUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.thinkland.sdk.android.JuheSDKInitializer;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class MyApplication extends Application {

    //实例
    private static MyApplication mInstance;

    //AppName
    public static final String APP_FULL_NAME="com.fat246.cybercar";
    public static final String APP_NAME="cybercar";

    //SMSSDK INFO
    private static final String SMSSDK_APPKEY = "11725853623e3";
    private static final String SMSSDK_APPSECRET = "496c4afbdfea32b78ffb5964385c46d1";

    //Tibmer 相关的四种主题
    private static final String LIGHT_THEME = "light_theme";
    private static final String DARK_THEME = "dark_theme";
    private static final String LIGHT_THEME_NOTOOLBAR = "light_theme_notoolbar";
    private static final String DARK_THEME_NOTOOLBAR = "dark_theme_notoolbar";

    //Bmob 密钥
    private static final String BOMB_APPKEY = "20d6303487c60c4c630c3e6a7b4615d3";

    //保存头像的位置
    public static String USER_AVATOR_DIRCTORY;

    //全局队列
    private static RequestQueue mRequestQueue;

    //全局User
    public static User mUser;
    public static Boolean isLoginSucceed = false;
    public static Bitmap mAvator;

    @Override
    public void onCreate() {
        super.onCreate();

        MyApplication.USER_AVATOR_DIRCTORY = this.getExternalCacheDir().getAbsolutePath();

        //初始化实例
        mInstance = this;

        //初始化请求队列
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        //初始化百度地图API
        SDKInitializer.initialize(this);

        //初始化聚合数据
        JuheSDKInitializer.initialize(getApplicationContext());

        //TIMBER 相关的初始化
        initTimber();

        //初始化Bmob
        Bmob.initialize(this, BOMB_APPKEY);

        //查询违章信息的Service
        initRegulation();

        //启动推送消息
        initPush();
    }

    //initPush
    private void initPush() {

        //是否需要启动推送服务
        if (PreferencesUtility.getInstance(this).isSettingsCarPush()) {

            // 使用推送服务时的初始化操作
            BmobInstallation.getCurrentInstallation(this).save();

            BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);

            //订阅维护信息
            installation.subscribe("Maintain");
            installation.save();

            // 启动推送服务
            BmobPush.startWork(this);
        }
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
    public static MyApplication getInstance() {

        return mInstance;
    }

    //iniRegulation
    private void initRegulation() {

        // ********************************************************
        Log.d("初始化服务代码", "");
        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId", 1626);// 您的appId
        weizhangIntent.putExtra("appKey", "624fbe005c8138700f31bdfca484f878");// 您的appKey
        startService(weizhangIntent);
        // ********************************************************
    }

}