package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;
import com.fat246.cybercar.fragments.CropImageFragment;

public class CropImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        initToolbar();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_crop_image_container, CropImageFragment.newInstance()).commit();
    }

    //Toolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_crop_image_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CropImageActivity.this.finish();
                }
            });
        }
    }
}
