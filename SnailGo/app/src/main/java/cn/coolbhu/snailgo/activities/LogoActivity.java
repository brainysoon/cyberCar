package cn.coolbhu.snailgo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.fragments.FirstLoadFragment;
import cn.coolbhu.snailgo.fragments.LogoFragment;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Fragment mFragment = null;

        if (PreferencesUtils.getInstance(this).setIsFirstLoad()) {

            mFragment = FirstLoadFragment.newInstance();
        } else {

            mFragment = new LogoFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.activity_logo_framelayout, mFragment).commit();
    }
}