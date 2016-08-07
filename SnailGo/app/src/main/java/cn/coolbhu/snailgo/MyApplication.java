package cn.coolbhu.snailgo;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.coolbhu.snailgo.permissions.Nammu;

/**
 * Created by ken on 16-7-25.
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        //初始化百度地图API
//        SDKInitializer.initialize(this);

        //ImageLoader Configuration
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(localImageLoaderConfiguration);

        //检查权限
        Nammu.init(this);
    }
}
