package cn.coolbhu.snailgo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.coolbhu.snailgo.utils.NotificationUtil;

/**
 * Created by Administrator on 2016/5/31.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

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
                String num = strs[1];

                new NotificationUtil(context)
                        .sendNotification(content, num);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }
}