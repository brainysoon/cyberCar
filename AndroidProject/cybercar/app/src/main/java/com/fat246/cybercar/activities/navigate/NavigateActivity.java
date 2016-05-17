package com.fat246.cybercar.activities.navigate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.fat246.cybercar.R;

public class NavigateActivity extends AppCompatActivity {

    //View
    private MapView mMap;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);



        findView();

        setSomething();
    }

    //设置百度地图
    private void setSomething() {

        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
    }

    //findView
    private void findView() {

        mMap = (MapView) findViewById(R.id.activity_navigate_mapview_map);

        mBaiduMap = mMap.getMap();
    }

    //管理生命周期
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMap.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMap.onPause();
    }
}
