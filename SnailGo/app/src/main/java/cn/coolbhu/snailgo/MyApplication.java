package cn.coolbhu.snailgo;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import cn.coolbhu.snailgo.permissions.Nammu;

/**
 * Created by ken on 16-7-25.
 */
public class MyApplication extends Application {

    public static final String APP_NAME = "snailgo";

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
}
