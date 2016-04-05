package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_login_framelayout, LoginFragment.newInstance()).commit();
    }
}

