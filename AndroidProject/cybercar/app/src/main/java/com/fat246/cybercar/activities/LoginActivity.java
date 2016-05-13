package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        initToolbar();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_login_framelayout, LoginFragment.newInstance()).commit();

    }

    //setToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_login_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);


            setSupportActionBar(toolbar);

            //这个监听事件放在  setSupportActionBar 之后就好使了
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LoginActivity.this.finish();
                }
            });
        }
    }
}