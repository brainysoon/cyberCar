package com.fat246.cybercar.utils;

import android.content.Context;
import android.util.Log;

import com.fat246.cybercar.beans.MyBmobInstallation;
import com.fat246.cybercar.beans.User;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/6/1.
 */
public class SucceedLoginUtil {

    public static void checkUid(final Context context, final User mUser) {

        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(context));
        query.findObjects(context, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    MyBmobInstallation mbi = object.get(0);


                    if (mbi.getUid() == null || mbi.getUid().equals(mUser.getUser_Tel())) {

                        mbi.setUid(mUser.getUser_Tel());
                        mbi.update(context, new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "设备信息更新成功");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("bmob", "设备信息更新失败:" + msg);
                            }
                        });

                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }
}
