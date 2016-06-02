package com.fat246.cybercar.activities.navigate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
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

public class NavigateActivity extends AppCompatActivity implements OnGetRoutePlanResultListener, BDLocationListener {

    public static final String sLat = "sLat";
    public static final String sLng = "sLng";
    public static final String eLat = "eLat";
    public static final String eLng = "eLng";

    //View
    private MapView mMap;
    private BaiduMap mBaiduMap;

    private Button mLoc;

    OverlayManager routeOverlay = null;
    RouteLine route = null;

    //定位Client
    private LocationClient mLocationClient;
    private boolean isLoc = false;

    //标注点当前位置
    private BitmapDescriptor mNowLoc = BitmapDescriptorFactory.fromResource(R.drawable.icon_car_now);

    //serach
    RoutePlanSearch mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        initToolbar();

        findView();

        setSomething();

        initSearch();
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_navigate_bar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("导航");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NavigateActivity.this.finish();
                }
            });
        }
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

        mLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoc) {

                    mLoc.setText("开始跟踪");
                    isLoc = false;
                    mLocationClient.stop();
                } else {

                    mLoc.setText("停止跟踪");
                    isLoc = true;

                    mLocationClient.start();
                }
            }
        });
    }

    //findView
    private void findView() {

        mMap = (MapView) findViewById(R.id.activity_navigate_mapview_map);

        mLoc = (Button) findViewById(R.id.activity_navigate_loc);

        mBaiduMap = mMap.getMap();

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);

        //注册监听事件
        mLocationClient.registerLocationListener(this);

        //设置
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); //打开GPS

        option.setCoorType("gcj02"); //设置坐标类型
        option.setScanSpan(500);   //定位间隙

//        option.setIsNeedAddress(true);

        //绑定设置
        mLocationClient.setLocOption(option);

        //设置一些 configeration
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS,
                true, mNowLoc));

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

        if (isLoc) {

            mLocationClient.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMap.onPause();

        if (isLoc) {

            mLocationClient.stop();
        }
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
            return mNowLoc;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        //map View 销毁过后不再处理新的定位
        if (bdLocation == null || mMap == null) {

            return;
        }

        //更新当前位置
        LatLng nowLoc = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

        MyLocationData mLocationData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                //此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();

        //设置定位数据
        mBaiduMap.setMyLocationData(mLocationData);


        //经纬度信息

        MapStatus.Builder mBuilder = new MapStatus.Builder();

        mBuilder.target(nowLoc).zoom(18.0f);

        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));
    }
}
