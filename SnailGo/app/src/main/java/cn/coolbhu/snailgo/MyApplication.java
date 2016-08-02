package cn.coolbhu.snailgo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by ken on 16-7-25.
 */
public class MyApplication extends Application {

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化百度地图API
        SDKInitializer.initialize(this);
    }
}
