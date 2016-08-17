package cn.coolbhu.snailgo.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.MainActivity;
import cn.coolbhu.snailgo.beans.User;
import cn.coolbhu.snailgo.utils.AutoUpdateManager;
import cn.coolbhu.snailgo.utils.PreferencesUtils;
import cn.coolbhu.snailgo.utils.SucceedLoginUtil;

public class LogoFragment extends Fragment implements AutoUpdateManager.AfterUpdate {

    //View
    private ImageView mImageView;

    //根据配置文件决定到去哪
    private Intent mIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断跳转到哪儿去
        mIntent = new Intent(getContext(), MainActivity.class);
    }

    @Override
    public void onResume() {
        super.onResume();

        //检测版本信息 //onstart会执行两次 不能放到拿去
        AutoUpdateManager autoUpdateManager = new AutoUpdateManager(getContext());

        autoUpdateManager.beginUpdate(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageView = (ImageView) view.findViewById(R.id.fragment_logo_imageview);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolbhu.cn"));

                startActivity(intent);
            }
        });
    }

    //处理跳转和自动登陆等。
    public void Utils() {

        try {

            PreferencesUtils mPrefernce = PreferencesUtils.getInstance(getContext().getApplicationContext());

            User mUser = mPrefernce.getUserInfo();

            //这个还是得要，Login的时候有用
            MyApplication.mUser = mUser;

            //判断是否要自动登陆
            if (mPrefernce.isAutoLogin(mUser.getUser_Tel())) {

                BmobQuery<User> query = new BmobQuery<>("User");

                query.addWhereEqualTo(PreferencesUtils.USER_TEL, mUser.getUser_Tel());
                query.addWhereEqualTo(PreferencesUtils.USER_PASSWORD, mUser.getUser_Password());

                query.findObjects(getContext(), new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {

                        //登录成功
                        if (list.size() > 0) {

                            MyApplication.isLoginSucceed = true;
                            MyApplication.mUser = list.get(0);

                            Toast.makeText(getContext(), "登陆成功！", Toast.LENGTH_SHORT).show();

                            //更新设备设备信息
                            SucceedLoginUtil.checkUid(getContext(), MyApplication.mUser);

                        } else {

                            Toast.makeText(getContext().getApplicationContext(), "登陆失败！", Toast.LENGTH_SHORT).show();
                        }

                        startActivity(mIntent);
                        getActivity().finish();

                    }

                    @Override
                    public void onError(int i, String s) {

                        Toast.makeText(MyApplication.getInstance().getApplicationContext(), "网络出错，请稍后再登陆！", Toast.LENGTH_SHORT).show();

                        startActivity(mIntent);
                        getActivity().finish();
                    }
                });
            } else {

                startActivity(mIntent);
                getActivity().finish();
            }
        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mImageView.animate().setListener(null);
    }

    //更新完成过后的事情
    @Override
    public void toDoAfterUpdate() {

        setImageAnimate();
    }

    //设置图片动画
    private void setImageAnimate() {

        mImageView.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (MyApplication.isLoginSucceed) {

                    startActivity(mIntent);
                    getActivity().finish();
                } else {

                    Utils();
                }
            }
        }).start();
    }

    @Override
    public void toShowNoNeedUpdate() {

    }

    @Override
    public void toShowError(int error) {

    }
}
