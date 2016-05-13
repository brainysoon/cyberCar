package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.RegisterBasicInfoFragment;

public class RegisterActivity extends AppCompatActivity {

    //View
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initTooblar();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_register_framelayout,
                        new RegisterBasicInfoFragment().newInstance()).commit();
    }

    private void initTooblar() {

        View rootView = findViewById(R.id.activity_register_toolbar);

        if (rootView != null) {

            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RegisterActivity.this.finish();
                }
            });
        }
    }
}