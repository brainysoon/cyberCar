package cn.coolbhu.snailgo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.coolbhu.snailgo.utils.NotificationUtil;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

/**
 * Created by Administrator on 2016/5/31.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

    public static String Status_Mileage = "里程数又超过15000了，该去车检了";
    public static String Status_Gas = "快没有油了，该加油了";
    public static String Status_Engine = "发动机该检查维修了";
    public static String Status_Speed = "变速器该检查维修了";
    public static String Status_Light = "车灯该检查维修了";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {

            Log.e("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));


            String json = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

            try {

                JSONObject jsonObject = new JSONObject(json);

                String content = jsonObject.getString("alert");

                String[] strs = content.split(":");

                Log.e("strs", strs.length + "");

                content = strs[0];

                int x = Integer.parseInt(content);

                if (!shouldSendMsg(x, context)) {

                    return;
                }

                content = convertContent(x);

                String num = strs[1];

                new NotificationUtil(context)
                        .sendNotification(content, num);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }

    private String convertContent(int x) {

        switch (x) {

            case 1:
                return Status_Mileage;

            case 2:
                return Status_Gas;

            case 3:
                return Status_Engine;
            case 4:
                return Status_Speed;
            case 5:
                return Status_Light;
        }

        return "";
    }

    //判断是否该推送
    private boolean shouldSendMsg(int x, Context context) {

        PreferencesUtils mPreference = PreferencesUtils.getInstance(context);

        switch (x) {

            case 1:
                return mPreference.shouldPushMsgMileage();

            case 2:

                return mPreference.shouldPushMsgGas();

            case 3:

                return mPreference.shouldPushMsgEngin();

            case 4:

                return mPreference.shouldPushMsgSpeed();

            case 5:

                return mPreference.shouldPushMsgLight();
        }

        return false;
    }
}