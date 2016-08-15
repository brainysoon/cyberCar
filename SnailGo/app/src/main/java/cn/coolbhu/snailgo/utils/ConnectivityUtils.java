package cn.coolbhu.snailgo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ken on 16-7-15.
 * <p/>
 * 这个类主要是用来判断网络链接的，是否链接，是否是wi-fi链接，是否是移动网络链接
 */
public class ConnectivityUtils {

    /**
     * 判断是否链接网络
     */
    public static boolean isConnected(Context context) {

        try {

            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

            //返回链接状态
            if (activeInfo != null && activeInfo.isConnected()) {

                return true;
            } else {

                return false;
            }
        } catch (Exception e) {

            return false;
        }
    }

    //判断是否是wifi链接
    public static boolean isWifiConnected(Context context) {

        try {

            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

            //返回链接状态
            if (activeInfo != null && activeInfo.isConnected()
                    && activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                return true;
            } else {

                return false;
            }
        } catch (Exception e) {

            return false;
        }
    }
}
