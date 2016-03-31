package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.RegisterBasicInfoFragment;
import com.fat246.cybercar.fragments.RegisterDetailInfoFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fragment mFragment=new RegisterBasicInfoFragment();
        //Load Fragmentw
        getSupportFragmentManager().beginTransaction().
                add(R.id.activity_register_framelayout,mFragment).commit();
    }
}
