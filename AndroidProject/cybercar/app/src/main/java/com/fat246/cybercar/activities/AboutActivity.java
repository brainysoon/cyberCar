package com.fat246.cybercar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fat246.cybercar.R;

public class AboutActivity extends AppCompatActivity {

    private ListView mDataList;

    private String[] mData = new String[]{
            "JUnit",
            "百度Api",
            "聚合数据",
            "车首页",
            "后端云",
            "UltraPtr",
            "Renderscript",
            "Boommenu",
            "Simplecropview",
            "MaterialDialogs",
            "Nineoldandroids",
            "UniversalImageLoader",
            "Support",
            "Okhttp",
            "Gson",
            "TrackSelector",
            "Okio",
            "QRCodeReaderView",

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

        mDataList.setAdapter(new ArrayAdapter<String>(AboutActivity.this, R.layout.simp_text, mData));
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_about_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("关于");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AboutActivity.this.finish();
                }
            });
        }
    }
}
