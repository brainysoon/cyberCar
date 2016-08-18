package cn.coolbhu.snailgo.utils;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.coolbhu.snailgo.beans.MyBmobInstallation;
import cn.coolbhu.snailgo.beans.User;

/**
 * Created by Administrator on 2016/6/1.
 */
public class SucceedLoginUtil {

    public static void checkUid(final Context context, final User mUser) {

        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(context));
        query.findObjects(new FindListener<MyBmobInstallation>() {

            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {

                if (e == null) {

                    if (list.size() > 0) {
                        MyBmobInstallation mbi = list.get(0);


                        mbi.setUid(mUser.getUser_Tel());
                        mbi.update(new UpdateListener() {

                            @Override
                            public void done(BmobException e) {

                                if (e == null) {

                                    Log.i("bmob", "设备信息更新成功");
                                } else {

                                    Log.i("bmob", "设备信息更新失败:" + e.toString());
                                }
                            }

                        });
                    }
                }
            }
        });
    }
}
