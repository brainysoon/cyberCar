package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.LogoFragment;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        //以后得着一种方法 来替代这种不然 ActionBar显示的方法
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Fragment mFragment = new LogoFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.logo_framelayout, mFragment).commit();
    }
}
