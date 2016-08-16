package cn.coolbhu.snailgo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.permissions.Nammu;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

/**
 * Created by ken on 16-7-25.
 */
public class MyApplication extends Application {

    public static final String APP_NAME = "snailgo";

    //Bmob 密钥
    private static final String BOMB_APPKEY = "20d6303487c60c4c630c3e6a7b4615d3";

    //汽车维护信息推送订阅
    public static final String MAINTAINCE_CAR = "Maintain";

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public MyApplication() {
        super();
    }

    public static boolean isLoginSucceed = false;

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

        initDrawerUri();

        initPush();
    }

    //
    private void initDrawerUri() {

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
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

    //initPush
    private void initPush() {

        //是否需要启动推送服务
        if (PreferencesUtils.getInstance(this).isSettingsCarPush()) {

            // 使用推送服务时的初始化操作
            BmobInstallation.getCurrentInstallation(this).save();

            BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);

            //订阅维护信息
            installation.subscribe(MAINTAINCE_CAR);
            installation.save();

            // 启动推送服务
            BmobPush.startWork(this);
        }
    }

    //获取队列
    public static RequestQueue getRequestQueue() {

        return MyApplication.mRequestQueue;
    }
}
