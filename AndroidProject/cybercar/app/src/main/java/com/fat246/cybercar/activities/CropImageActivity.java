package com.fat246.cybercar.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.Register.StartRegisterActivity;
import com.fat246.cybercar.fragments.CropImageFragment;
import com.isseiaoki.simplecropview.callback.CropCallback;

public class CropImageActivity extends AppCompatActivity {

    public static final String ACTION_KEY = "ACTION_KEY";

    public static CropCallback mCropCallback;
    public static Bitmap mAvator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        initAction();

        initToolbar();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_crop_image_container, CropImageFragment.newInstance()).commit();

    }

    private void initAction() {

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        int i = bundle.getInt(ACTION_KEY);

        switch (i) {

            case 0:
                mCropCallback = StartRegisterActivity.mInstant;
                mAvator = StartRegisterActivity.avator;
                break;

            case 1:
                mCropCallback = MyInfoActivity.mInstant;
                mAvator = MyInfoActivity.mAvator;
                break;
        }

    }

    //Toolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_crop_image_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("剪切图片");

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
