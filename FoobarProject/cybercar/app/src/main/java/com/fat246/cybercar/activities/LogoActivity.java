package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.FirstLoadFragment;
import com.fat246.cybercar.fragments.LogoFragment;
import com.fat246.cybercar.utils.PreferencesUtility;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Fragment mFragment =null;

        if (PreferencesUtility.getInstance(this).setIsFirstLoad()){

            mFragment=FirstLoadFragment.newInstance();
        }else {

            mFragment = new LogoFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.activity_logo_framelayout, mFragment).commit();
    }
}