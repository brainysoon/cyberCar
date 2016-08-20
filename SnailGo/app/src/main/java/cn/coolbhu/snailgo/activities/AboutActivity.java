package cn.coolbhu.snailgo.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.coolbhu.snailgo.R;

public class AboutActivity extends AppCompatActivity {

    private ListView mDataList;

    private String[] mData = new String[]{
            "高德地图",
            "讯飞",
            "后端云",
            "银联",
            "MaterialDrawer",
            "BottomBar",
            "GoogleMaterialTypeface",
            "OcticonsTypeface",
            "ItemAnimators",
            "PermissionDispatcher",
            "UltraPtr",
            "CircleImageView",
            "TrackSelector",
            "UniversalImageLoder",
            "MaterialDialogs",
            "AppThemeEngine",
            "MaterialIconLib",
            "Retrofit",
            "OkHttp",
            "QRCodeReaderView",
            "SimpleCropView",
            "BoomMenu",
            "SpringIndicator",
            "Glide",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar();

        initList();
    }

    //initList
    private void initList() {

        mDataList = (ListView) findViewById(R.id.activity_about_list);

        mDataList.setAdapter(new ArrayAdapter<String>(AboutActivity.this, android.R.layout.simple_list_item_1, mData));
    }

    //initToolbar
    private void initToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
