package cn.coolbhu.snailgo.fragments.main;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.thinkland.sdk.android.JuheSDKInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.LoginActivity;
import cn.coolbhu.snailgo.activities.moregas.BookGasActivity;
import cn.coolbhu.snailgo.activities.moregas.MoreGasActivity;
import cn.coolbhu.snailgo.activities.navigates.RoutePlanActivity;
import cn.coolbhu.snailgo.beans.GasStationInfo;
import cn.coolbhu.snailgo.utils.IntentUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class OilMainFragment extends Fragment implements AdapterView.OnItemClickListener,
        AMapLocationListener, GasStationInfo.canHandGasStationPostResult {
    public static final String DEFAULT_GAS_PRICE = "E93#";

    public static int STATION_SEARCH_RANG = 2000;

    //View
    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;
    private View rootView;

    //Adapter
    private StationAdapter mAdapter;

    //当前的位置
    private LatLng nowLoc = null;

    //View
    private Button buttonMoreGas;

    //周边加油站
    private List<GasStationInfo> mGasStations = new ArrayList<>();

    //定位
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;

    public static OilMainFragment newInstance() {
        OilMainFragment fragment = new OilMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oil_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        initView(view);

        OilMainFragmentPermissionsDispatcher.initLoacationWithCheck(this);


        //接口请求次数有限，悠着点
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                mPtrFrame.autoRefresh();
            }
        }, 1000);
    }

    private void initView(View rootView) {

        buttonMoreGas = (Button) rootView.findViewById(R.id.my_button);
        mListView = (ListView) rootView.findViewById(R.id.my_cars_list);
        mPtrFrame = (PtrClassicFrameLayout) rootView.findViewById(R.id.ptr_layout);


        buttonMoreGas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getContext(), MoreGasActivity.class);

                startActivity(mIntent);
            }
        });

        //list
        mListView.setOnItemClickListener(this);

        initPtr();

        initList();
    }

    //initList
    private void initList() {

        mAdapter = new StationAdapter(getContext());

        mListView.setAdapter(mAdapter);
    }

    //initPtr
    private void initPtr() {

        mPtrFrame.setLastUpdateTimeRelateObject(this);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                if (getContext() != null) {

                    if (nowLoc == null) {

                        Toast.makeText(getContext(), "定位成功过后方可加载周边加油站！",
                                Toast.LENGTH_SHORT).show();

                        mPtrFrame.refreshComplete();
                    } else {

                        GasStationInfo.attmptGasStationPost(getContext(), GasStationInfo.setPostRequest(nowLoc.longitude,
                                nowLoc.latitude, STATION_SEARCH_RANG, 1, 1), OilMainFragment.this);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MyApplication.isLoginSucceed) {


            buttonMoreGas.setText(R.string.oil_button_bookgas);
        } else {

            buttonMoreGas.setText(R.string.oil_button_see_station);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final GasStationInfo stationInfo = mGasStations.get(i);

        if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

            if (stationInfo != null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(stationInfo.getGas_station_name())
                        .setMessage(stationInfo.getGas_station_address())
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("预约加油", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent mIntent = new Intent(getContext(), BookGasActivity.class);

                                mIntent.putExtra(GasStationInfo.GAS_STATION_JSON_STRING, stationInfo.getJsonString());

                                startActivity(mIntent);
                            }
                        })
                        .setNegativeButton("到那儿去", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                LatLng mStationLatLng = new LatLng(stationInfo.getGas_station_lat()
                                        , stationInfo.getGas_station_lon());

                                if (mStationLatLng != null && nowLoc != null) {

                                    Intent intent = new Intent(getContext(), RoutePlanActivity.class);

                                    intent.putExtra(NagMainFragment.POSITION_X, mStationLatLng.latitude);
                                    intent.putExtra(NagMainFragment.POSITION_Y, mStationLatLng.longitude);
                                    intent.putExtra(NagMainFragment.POSITION_X_S, nowLoc.latitude);
                                    intent.putExtra(NagMainFragment.POSITION_Y_S, nowLoc.longitude);

                                    startActivity(intent);
                                } else {

                                    Toast.makeText(getContext(), "起点和终点不能为空！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                builder.create().show();
            }
        } else {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

            builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.notice)
                    .setMessage("亲，你还没有登录，登录过后可以预约加油哦！")
                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(getContext(), LoginActivity.class);

                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("直接到那儿去", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            LatLng mStationLatLng = new LatLng(stationInfo.getGas_station_lat()
                                    , stationInfo.getGas_station_lon());

                            if (mStationLatLng != null && nowLoc != null) {

                                Intent intent = new Intent(getContext(), RoutePlanActivity.class);

                                intent.putExtra(NagMainFragment.POSITION_X, mStationLatLng.latitude);
                                intent.putExtra(NagMainFragment.POSITION_Y, mStationLatLng.longitude);
                                intent.putExtra(NagMainFragment.POSITION_X_S, nowLoc.latitude);
                                intent.putExtra(NagMainFragment.POSITION_Y_S, nowLoc.longitude);

                                startActivity(intent);
                            } else {

                                Toast.makeText(getContext(), "起点和终点不能为空！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            builder.create().show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationClient != null) {

            locationClient.onDestroy();
            locationClient = null;
        }
        locationClientOption = null;
    }

    //开始定位
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initLoacation() {

        //初始化
        locationClient = new AMapLocationClient(getContext().getApplicationContext());

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
        JuheSDKInitializer.initialize(getContext().getApplicationContext());

    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showLoacationRationale(final PermissionRequest request) {

        new AlertDialog.Builder(getContext())
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

                        IntentUtils.toSnailGoSettings(getContext());
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {

            if (aMapLocation.getErrorCode() == 0) {

                locationClient.stopLocation();

                nowLoc = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            }
        }
    }

    @Override
    public void handGasStationPostResult(int i, String s) {

        Log.e("myJson", s.toString());

        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject(s);

        } catch (JSONException jsonException) {

            jsonException.printStackTrace();
        }

        mGasStations = GasStationInfo.paserResultList(jsonObject);

        if (mGasStations == null || mGasStations.size() == 0) {

            if (getContext() != null) {

                Toast.makeText(getContext(), "加载周边加油站失败，请稍后再试!",
                        Toast.LENGTH_SHORT).show();
            }

            mPtrFrame.refreshComplete();
            return;
        }

        //加载成功
        mAdapter.notifyDataSetChanged();

        mPtrFrame.refreshComplete();

        if (getContext() != null) {

            Toast.makeText(getContext(), "周边" + STATION_SEARCH_RANG + "米范围内的加油站，加载成功！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void handGasStationPostError(int i, String s, Throwable throwable) {

        if (getContext() != null) {

            Toast.makeText(getContext(), "加载周边加油站失败，请稍后再试!",
                    Toast.LENGTH_SHORT).show();

            mPtrFrame.refreshComplete();
        }
    }

    @Override
    public void handGasStationPostFinish() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        OilMainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    class StationAdapter extends BaseAdapter {

        private LayoutInflater mLayoutInflater;

        public StationAdapter(Context context) {

            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mGasStations.size();
        }

        @Override
        public Object getItem(int i) {
            return mGasStations.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = mLayoutInflater.inflate(R.layout.item_station, null);

            initView(view, i);
            return view;
        }

        private void initView(View view, int i) {

            GasStationInfo stationInfo = mGasStations.get(i);

            if (stationInfo != null) {

                TextView stationTitle = (TextView) view.findViewById(R.id.station_title);
                TextView stationPosition = (TextView) view.findViewById(R.id.station_position);
                TextView stationPrice = (TextView) view.findViewById(R.id.station_price);
                TextView stationDiscount = (TextView) view.findViewById(R.id.station_is_discount);
                TextView stationDistance = (TextView) view.findViewById(R.id.station_distance);

                try {

                    String str = stationInfo.getGas_station_gastprice();

                    JSONObject jsonObject = new JSONObject(str);

                    str = (String) jsonObject.get(DEFAULT_GAS_PRICE);

                    stationPrice.setText(str);
                } catch (Exception ex) {

                    ex.printStackTrace();
                }

                stationTitle.setText(stationInfo.getGas_station_name());
                stationPosition.setText(stationInfo.getGas_station_address());
                stationDiscount.setText(stationInfo.getGas_station_discount());
                stationDistance.setText(stationInfo.getGas_station_distance());
            }
        }
    }
}
