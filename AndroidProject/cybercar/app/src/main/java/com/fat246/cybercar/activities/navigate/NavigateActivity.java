package com.fat246.cybercar.activities.navigate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.fat246.cybercar.R;

public class NavigateActivity extends AppCompatActivity implements OnGetRoutePlanResultListener {

    public static final String sLat = "sLat";
    public static final String sLng = "sLng";
    public static final String eLat = "eLat";
    public static final String eLng = "eLng";

    //View
    private MapView mMap;
    private BaiduMap mBaiduMap;

    OverlayManager routeOverlay = null;
    RouteLine route = null;

    //serach
    RoutePlanSearch mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);


        findView();

        setSomething();

        initSearch();
    }

    private void initSearch() {

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        PlanNode sNode = null;
        PlanNode eNode = null;

        Bundle mBundle = getIntent().getExtras();

        //传过来的坐标
        LatLng sLatLng = new LatLng(mBundle.getDouble(sLat), mBundle.getDouble(sLng));
        LatLng eLatLng = new LatLng(mBundle.getDouble(eLat), mBundle.getDouble(eLng));

        sNode = PlanNode.withLocation(sLatLng);
        eNode = PlanNode.withLocation(eLatLng);

        //开始搜索
        mSearch.drivingSearch(new DrivingRoutePlanOption().from(sNode).to(eNode));

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

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(NavigateActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {

            route = drivingRouteResult.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(drivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }
}
