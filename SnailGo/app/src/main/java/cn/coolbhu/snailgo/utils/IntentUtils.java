package cn.coolbhu.snailgo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by ken on 16-8-1.
 */
public class IntentUtils {

    //package
    public static final String PACKAGE_LABLE = "package:";

    //跳转到当前App设置页面
    public static void toCStartSettings(Context context) {

        Uri packageURI = Uri.parse(PACKAGE_LABLE + context.getPackageName());
        Intent mIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        context.startActivity(mIntent);
    }
}
