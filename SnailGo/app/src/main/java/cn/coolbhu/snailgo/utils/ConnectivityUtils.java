package cn.coolbhu.snailgo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cn.coolbhu.snailgo.R;

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

    public static void shouldShowNotConnectdNotic(Context context) {

        if (!isConnected(context)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.notice)
                    .setMessage(R.string.location_must_need_connected)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });

            builder.create().show();
        }
    }
}
