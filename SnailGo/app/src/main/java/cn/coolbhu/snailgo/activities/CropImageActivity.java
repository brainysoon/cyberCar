package cn.coolbhu.snailgo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.isseiaoki.simplecropview.callback.CropCallback;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.register.StartRegisterActivity;
import cn.coolbhu.snailgo.fragments.CropImageFragment;

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

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
