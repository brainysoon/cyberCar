package cn.coolbhu.snailgo.activities.moregas;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.thinkland.sdk.android.JuheSDKInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.navigates.RoutePlanActivity;
import cn.coolbhu.snailgo.beans.GasStationInfo;
import cn.coolbhu.snailgo.fragments.main.NagMainFragment;
import cn.coolbhu.snailgo.helpers.StationDialogHolder;
import cn.coolbhu.snailgo.utils.ConnectivityUtils;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MoreGasActivity extends AppCompatActivity implements
        GasStationInfo.canHandGasStationPostResult, AMapLocationListener, LocationSource {

    //View
    private MapView mMapView;
    private LinearLayout mDialog;
    private ImageView mCancel;
    private Button mBookGas;
    private Button mGoHere;


    //定位Client
    private AMapLocationClient locationClient = null;

    //定位选项
    private AMapLocationClientOption locationClientOption = null;

    //Listener
    private OnLocationChangedListener mListener = null;

    //标注点 加油站的位置
    private BitmapDescriptor mMarkBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_local_gas_station_red);

    //地图
    private AMap mAMap;

    //当前的位置
    private LatLng nowLoc;

    //周边加油站
    private Map<String, GasStationInfo> mGasStations;

    //Dialog View Holder
    private StationDialogHolder mDialogHolder;

    //当前展示的 Station
    private GasStationInfo nowDialogStation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_gas);

        //获得地图控件
        mMapView = (MapView) findViewById(R.id.activity_more_gas_mapview_map);

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        initToolbar();

        findView();

        setSometing();

        setListener();

        //检查联网
        ConnectivityUtils.shouldShowNotConnectdNotic(this);
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


    //设置监听事件
    private void setListener() {

        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                nowDialogStation = mGasStations.get(marker.getTitle());


                //替换Dialgo
                mDialogHolder.replaceDialog(nowDialogStation);

                mDialog.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //取消
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.setVisibility(View.INVISIBLE);
            }
        });

        //预约加油
        mBookGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(MoreGasActivity.this, BookGasActivity.class);

                if (nowDialogStation != null) {

                    mIntent.putExtra(GasStationInfo.GAS_STATION_JSON_STRING, nowDialogStation.getJsonString());
                }

                startActivity(mIntent);

            }
        });

        //到哪儿去
        mGoHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng mStationLatLng = new LatLng(nowDialogStation.getGas_station_lat()
                        , nowDialogStation.getGas_station_lon());

                if (mStationLatLng != null && nowLoc != null) {

                    Intent intent = new Intent(MoreGasActivity.this, RoutePlanActivity.class);

                    intent.putExtra(NagMainFragment.POSITION_X, mStationLatLng.latitude);
                    intent.putExtra(NagMainFragment.POSITION_Y, mStationLatLng.longitude);
                    intent.putExtra(NagMainFragment.POSITION_X_S, nowLoc.latitude);
                    intent.putExtra(NagMainFragment.POSITION_Y_S, nowLoc.longitude);

                    startActivity(intent);
                } else {

                    Toast.makeText(MoreGasActivity.this, "起点和终点不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        //初始化聚合数据
        JuheSDKInitializer.initialize(getApplicationContext());

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

        Snackbar.make(this.findViewById(R.id.rootView), R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(MoreGasActivity.this);
                    }
                })
                .show();
    }

    //setSometing
    private void setSometing() {

        //地图初始化
        mAMap = mMapView.getMap();

        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        // 设置定位的类型为定位模式，参见类AMap。
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

        //初始化地图
        MoreGasActivityPermissionsDispatcher.initLoacationWithCheck(this);

        //初始化Holder
        mDialogHolder = new StationDialogHolder(mDialog);
    }

    //findView
    private void findView() {

        mDialog = (LinearLayout) findViewById(R.id.activity_more_gas_linearlayout_dialog);
        mCancel = (ImageView) findViewById(R.id.activity_more_gas_imageview_cancel);
        mBookGas = (Button) findViewById(R.id.activity_more_gas_button_bookgas);
        mGoHere = (Button) findViewById(R.id.activity_more_gas_button_gohere);

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {

            if (aMapLocation.getErrorCode() == 0) {

                mListener.onLocationChanged(aMapLocation);

                //当第一次定位成功后我会去访问周边的加油站
                GasStationInfo.attmptGasStationPost(MoreGasActivity.this, GasStationInfo.setPostRequest(aMapLocation.getLongitude(),
                        aMapLocation.getLatitude(), 5000, 1, 1), MoreGasActivity.this);

                locationClient.stopLocation();

                nowLoc = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            }
        }
    }

    @Override
    public void deactivate() {

        mListener = null;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

        mListener = onLocationChangedListener;
    }

    //同步生命周期
    @Override
    protected void onPause() {
        super.onPause();

        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();

        if (MyApplication.isLoginSucceed) {

            mBookGas.setVisibility(View.VISIBLE);
            mBookGas.setEnabled(true);
        } else {

            mBookGas.setVisibility(View.INVISIBLE);
            mBookGas.setEnabled(false);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

        if (locationClient != null) {

            locationClient.onDestroy();
            locationClient = null;
        }
        locationClientOption = null;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    //回掉接口
    @Override
    public void handGasStationPostError(int i, String s, Throwable throwable) {


    }

    @Override
    public void handGasStationPostFinish() {

    }

    @Override
    public void handGasStationPostResult(int resultCode, String s) {

        Log.e("myJson", s.toString());

        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject(s);

        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        mGasStations = GasStationInfo.paserGasStationPostResult(jsonObject);

        if (mGasStations == null || mGasStations.size() == 0) {

            return;
        }


        //依次设置 遍历map
        for (Map.Entry<String, GasStationInfo> entry : mGasStations.entrySet()) {

            setMark(entry.getValue());
        }

    }

    //处理加油站的标注点
    private void setMark(GasStationInfo mGasStation) {

        //定义标注点坐标
        LatLng point = new LatLng(mGasStation.getGas_station_lat(), mGasStation.getGas_station_lon());

        //构建mark option 用于在地图上添加
        MarkerOptions option = new MarkerOptions().position(point).icon(mMarkBitmap)
                .title(mGasStation.getGas_station_name());

        //在地图上添加 并显示
        mAMap.addMarker(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MoreGasActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
