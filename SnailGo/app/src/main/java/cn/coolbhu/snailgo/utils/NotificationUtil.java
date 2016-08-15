package cn.coolbhu.snailgo.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.cars.CarsInfoActivity;


/**
 * Created by Administrator on 2016/6/1.
 */
public class NotificationUtil {

    private static int NOTIFICATION_FLAG = 1;

    public static final String NOTIFICATION_NUM = "NOTIFICATION_NUM";
    public static final String NOTIFICATION_STR = "NOTIFICATION_STR";


    private NotificationManager mNotificationManager;
    private Context context;

    public NotificationUtil(Context context) {

        this.context = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //发送通知
    public void sendNotification(String str, String num) {


        Intent mIntent = new Intent(context, CarsInfoActivity.class);

        Bundle mBundle = new Bundle();

        mBundle.putString(NOTIFICATION_NUM, num);
        mBundle.putString(NOTIFICATION_STR, str);
        mBundle.putInt(CarsInfoActivity.Action, 1);
        mIntent.putExtras(mBundle);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                mIntent, 0);

        Notification notify2 = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                // icon)
                .setTicker("通知:" + str)// 设置在status
                // bar上显示的提示文字
                .setContentTitle("新的汽车维护信息")
                // 设置在下拉status
                // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                .setContentText(str)
                // TextView中显示的详细内容
                .setContentIntent(intent)
                // 关联PendingIntent
                .setNumber(1)
                // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                .getNotification();
        // 需要注意build()是在API level
        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFICATION_FLAG++, notify2);
    }
}
