package com.fat246.cybercar.activities.moregas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.navigate.NavigateActivity;
import com.fat246.cybercar.beans.GasStationInfo;
import com.fat246.cybercar.holder.StationDialogHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MoreGasActivity extends AppCompatActivity implements GasStationInfo.canHandGasStationPostResult {

    //View
    private MapView mMapView;
    private LinearLayout mDialog;
    private ImageView mCancel;
    private Button mBookGas;
    private Button mGoHere;


    //BaiduMap
    private BaiduMap mBaiduMap;

    //定位Client
    private LocationClient mLocationClient;

    //是否是第一次进入第一次定位
    private boolean isFirstLocation = true;
    private LatLng mLatLng;

    //标注点 加油站的位置
    private BitmapDescriptor mMarkBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_local_gas_station_red);

    //标注点当前位置
    private BitmapDescriptor mNowLoc = BitmapDescriptorFactory.fromResource(R.drawable.icon_car_now);

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

        findView();

        setSometing();

        setListener();

    }

    //设置监听事件
    private void setListener() {

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
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

                MoreGasActivity.this.finish();
            }
        });

        //到哪儿去
        mGoHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng mStationLatLng = new LatLng(nowDialogStation.getGas_station_lat()
                        , nowDialogStation.getGas_station_lon());

                if (mStationLatLng != null && mLatLng != null) {

                    Intent mIntent = new Intent(MoreGasActivity.this, NavigateActivity.class);

                    Bundle mBundle = new Bundle();

                    mBundle.putDouble(NavigateActivity.sLat, mLatLng.latitude);
                    mBundle.putDouble(NavigateActivity.sLng, mLatLng.longitude);
                    mBundle.putDouble(NavigateActivity.eLat, mStationLatLng.latitude);
                    mBundle.putDouble(NavigateActivity.eLng, mStationLatLng.longitude);

                    mIntent.putExtras(mBundle);

                    startActivity(mIntent);

                    MoreGasActivity.this.finish();
                }
            }
        });
    }

    //setSometing
    private void setSometing() {

        //地图初始化
        mBaiduMap = mMapView.getMap();

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        mLocationClient = new LocationClient(this);

        //注册监听事件
        mLocationClient.registerLocationListener(new MyLocationListener());

        //设置
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); //打开GPS

        option.setCoorType("gcj02"); //设置坐标类型
        option.setScanSpan(3000);   //定位间隙

        option.setIsNeedAddress(true);

        //绑定设置
        mLocationClient.setLocOption(option);

        //开始定位
        mLocationClient.start();

        //设置一些 configeration
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, mNowLoc));

        //初始化Holder
        mDialogHolder = new StationDialogHolder(mDialog);
    }

    //findView
    private void findView() {

        mMapView = (MapView) findViewById(R.id.activity_more_gas_mapview_map);

        mDialog = (LinearLayout) findViewById(R.id.activity_more_gas_linearlayout_dialog);
        mCancel = (ImageView) findViewById(R.id.activity_more_gas_imageview_cancel);
        mBookGas = (Button) findViewById(R.id.activity_more_gas_button_bookgas);
        mGoHere = (Button) findViewById(R.id.activity_more_gas_button_gohere);


    }

    //定位SDK 监听器
    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {


            Log.e("Address", bdLocation.getAddrStr());

            Log.e("Location", "unSucceed");

            Log.e("LoacitonNow", bdLocation.getLatitude() + "||" + bdLocation.getLongitude());
            //map View 销毁过后不再处理新的定位
            if (bdLocation == null || mMapView == null) {

                return;
            }

            //更新当前位置
            nowLoc = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

            MyLocationData mLocationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    //此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();

            //设置定位数据
            mBaiduMap.setMyLocationData(mLocationData);

            //是否是第一次定位
            if (isFirstLocation) {

                isFirstLocation = false;

                Log.e("hereFirst", isFirstLocation + "");

                //当第一次定位成功后我会去访问周边的加油站
                GasStationInfo.attmptGasStationPost(MoreGasActivity.this, GasStationInfo.setPostRequest(bdLocation.getLongitude(),
                        bdLocation.getLatitude(), 5000, 1, 1), MoreGasActivity.this);

                //经纬度信息
                mLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

                MapStatus.Builder mBuilder = new MapStatus.Builder();

                mBuilder.target(mLatLng).zoom(18.0f);

                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));
            }
        }
    }

    //同步生命周期
    @Override
    protected void onPause() {

        mMapView.onPause();

        mLocationClient.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {

        mMapView.onResume();

        mLocationClient.start();
        super.onResume();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        // 退出时销毁定位
        mLocationClient.stop();

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
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
        OverlayOptions option = new MarkerOptions().position(point).icon(mMarkBitmap)
                .title(mGasStation.getGas_station_name());

        //在地图上添加 并显示
        mBaiduMap.addOverlay(option);
    }
}
