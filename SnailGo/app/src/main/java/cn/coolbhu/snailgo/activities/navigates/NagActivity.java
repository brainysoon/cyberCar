package cn.coolbhu.snailgo.activities.navigates;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;

import cn.coolbhu.snailgo.R;

public class NagActivity extends NagBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nag);

        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }
}
