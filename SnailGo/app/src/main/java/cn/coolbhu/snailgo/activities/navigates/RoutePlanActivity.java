package cn.coolbhu.snailgo.activities.navigates;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.fragments.main.NagMainFragment;
import cn.coolbhu.snailgo.utils.IntentUtils;
import cn.coolbhu.snailgo.utils.TTSController;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class RoutePlanActivity extends AppCompatActivity implements AMapNaviListener,
        AMapLocationListener, LocationSource {

    private MapView mMapView = null;
    private Button mButton = null;

    private AMapNavi mAMapNavi = null;

    private View rootView = null;

    //Map
    private AMap mAMap = null;
//    private Marker mStartMarker;
//    private Marker mEndMarker;

    //起点和终点的信息
    private LatLng startLL = null;
    private LatLng endLL = null;

    private ProgressDialog progDialog = null;

    //Listenter
    private OnLocationChangedListener mListener = null;

    //偏好
    private boolean jam;
    private boolean charge;
    private boolean speed;
    private boolean notSpeed;

    //定位
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;

    private TTSController ttsManager;

    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();

    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();

    // 规划线路
    private RouteOverLay mRouteOverLay;

    /**
     * 路线计算成功标志位
     */
    private boolean calculateSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);

        ttsManager = TTSController.getInstance(this);
        ttsManager.init();

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(ttsManager);

        initToolbar();

        initData();

        initView(savedInstanceState);

        beginPlan();
    }

    private void beginPlan() {

        //显示进度
        showProgressDialog();

        int strategyFlag = 0;

        try {

            strategyFlag = mAMapNavi.strategyConvert(jam, notSpeed, charge, speed, false);

            Log.e("strategyFlag:", strategyFlag + "");
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        if (strategyFlag > 0) {

            //首先清空
            startList.clear();
            endList.clear();

            startList.add(new NaviLatLng(startLL.latitude, startLL.longitude));

            endList.add(new NaviLatLng(endLL.latitude, endLL.longitude));

            mAMapNavi.calculateDriveRoute(startList, endList, null, strategyFlag);
        }
    }

    private void initData() {

        Intent intent = getIntent();

        if (intent != null) {

            double startX = intent.getDoubleExtra(NagMainFragment.POSITION_X_S, 0.0);
            double startY = intent.getDoubleExtra(NagMainFragment.POSITION_Y_S, 0.0);
            double endX = intent.getDoubleExtra(NagMainFragment.POSITION_X, 0.0);
            double endY = intent.getDoubleExtra(NagMainFragment.POSITION_Y, 0.0);

            Log.e("X:", startX + ":" + startY);
            Log.e("Y:", endX + ":" + endY);

            startLL = new LatLng(startX, startY);
            endLL = new LatLng(endX, endY);

            jam = intent.getBooleanExtra(NagMainFragment.STATUS_JAM, true);
            charge = intent.getBooleanExtra(NagMainFragment.STATUS_CHARGE, true);
            speed = intent.getBooleanExtra(NagMainFragment.STATUS_SPEED, false);
            notSpeed = intent.getBooleanExtra(NagMainFragment.STATUS_NOT_SPEED, false);
        }
    }

    private void initView(Bundle savedInstanceState) {

        mMapView = (MapView) findViewById(R.id.mapview);
        mButton = (Button) findViewById(R.id.btn_start_nag);
        rootView = findViewById(R.id.rootView);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (calculateSuccess) {

                    Intent intent = new Intent(getApplicationContext(), RouteNaviActivity.class);

                    startActivity(intent);

                    finish();

                } else {

                    Toast.makeText(RoutePlanActivity.this, "必须路经规划成功过后才能导航！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMapView.onCreate(savedInstanceState);

        mAMap = mMapView.getMap();

        // 初始化Marker添加到地图
//        mStartMarker = mAMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.start))));
//        mEndMarker = mAMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.end))));

//        mStartMarker.setPosition(startLL);
//        mEndMarker.setPosition(endLL);

        mRouteOverLay = new RouteOverLay(mAMap, null, getApplicationContext());

        mAMap.animateCamera(CameraUpdateFactory.changeLatLng(startLL));

        //开始定位
        RoutePlanActivityPermissionsDispatcher.initLoacationWithCheck(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        startList.clear();
        endList.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        /**
         * 当前页面只是展示地图，activity销毁后不需要再回调导航的状态
         */
        mAMapNavi.removeAMapNaviListener(this);
        //注意：不要调用这个destory方法，因为在当前页面进行算路，算路成功的数据全部存在此对象中。到另外一个activity中只需要开始导航即可。
        //如果用户是回退退出当前activity，可以调用下面的destory方法。
        //mAMapNavi.destroy();

        if (locationClient != null) {

            locationClient.onDestroy();
            locationClient = null;
        }
        locationClientOption = null;

    }

    //显示进度条
    private void showProgressDialog() {
        try {
            if (progDialog == null)
                progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在路经规划!\n");
            progDialog.show();
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    //关闭进度条
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    //开始定位
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initLoacation() {

        //初始化
        locationClient = new AMapLocationClient(this.getApplicationContext());

        locationClientOption = new AMapLocationClientOption();


        // 设置定位模式为低功耗模式
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置只定位一次
//        locationClientOption.setOnceLocation(true);

        locationClientOption.setInterval(1000);

        // 设置定位监听
        locationClient.setLocationListener(this);

        //开始定位
        locationClient.startLocation();
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showLoacationRationale(final PermissionRequest request) {

        new AlertDialog.Builder(this)
                .setTitle(R.string.request_permission)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.request_location_permission)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.cancel();
                    }
                })

                .setCancelable(false)
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showLoacationDenied() {

        Snackbar.make(rootView, R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(RoutePlanActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

            if (mListener != null) {

                mListener.onLocationChanged(aMapLocation);

                Log.e("Position:", aMapLocation.getPoiName());

                locationClient.stopLocation();
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        RoutePlanActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * ************************************************************吓死宝宝了。。。 这么多。。。。×××××××××××××××××××××××××××××××××
     */
    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteSuccess() {

        AMapNaviPath naviPath = mAMapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }
        // 获取路径规划线路，显示到地图上
        mRouteOverLay.setAMapNaviPath(naviPath);
        mRouteOverLay.addToMap();

        dissmissProgressDialog();

        calculateSuccess = true;
    }

    @Override
    public void onCalculateRouteFailure(int i) {

        Toast.makeText(RoutePlanActivity.this, "路经规划出错：" + i, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }
}
