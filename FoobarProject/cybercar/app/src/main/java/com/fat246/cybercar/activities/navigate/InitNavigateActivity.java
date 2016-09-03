package com.fat246.cybercar.activities.navigate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.InitNavigateFragment;

public class InitNavigateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_navigate);

        initToolbar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_initnavigate_container, InitNavigateFragment.newInstance()).commit();
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_initnavigate_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("设置导航");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InitNavigateActivity.this.finish();
                }
            });
        }
    }
}
