package cn.coolbhu.snailgo.activities.navigates;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.fragments.main.NagMainFragment;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class PoiSearchActivity extends AppCompatActivity implements TextWatcher
        , Inputtips.InputtipsListener, AMapLocationListener, LocationSource, View.OnClickListener
        , PoiSearch.OnPoiSearchListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener {

    public static final String IS_PICK_END = "is_pick_end";
    public static final int RESULT_CODE = 1;

    //View
    private ImageView btnBack = null;
    private AutoCompleteTextView textContent = null;
    private ImageButton btnSearch = null;
    private MapView mAMapView = null;

    private ProgressDialog progDialog = null;

    //地图
    private AMap aMap = null;

    //关键字
    private String keyWord = null;

    //当前城市 默认是吉林
    private String cityText = "吉林";

    //poi检索
    // poi返回的结果
    private PoiResult poiResult;
    // 当前页面，从0开始计数
    private int currentPage = 0;
    // Poi查询条件类
    private PoiSearch.Query query;
    // POI搜索
    private PoiSearch poiSearch;

    //Listener
    private OnLocationChangedListener mListener = null;

    //定位
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;

    //是否是选择终点
    private boolean isPickEnd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);

        mAMapView = (MapView) findViewById(R.id.amap_view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mAMapView.onCreate(savedInstanceState);

        initData();

        initView();
    }

    private void initData() {

        Intent intent = getIntent();

        isPickEnd = intent.getBooleanExtra(IS_PICK_END, true);
    }

    private void initView() {

        btnBack = (ImageView) findViewById(R.id.btn_back);
        textContent = (AutoCompleteTextView) findViewById(R.id.text_content);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PoiSearchActivity.this.finish();
            }
        });

        btnSearch.setOnClickListener(this);

        textContent.addTextChangedListener(this);

        //初始化地图
        aMap = mAMapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        //监听事件
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件

        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

        //初始化地图
        PoiSearchActivityPermissionsDispatcher.initLoacationWithCheck(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String str = charSequence.toString().trim();

        if (!str.equals("")) {
            InputtipsQuery inputquery = new InputtipsQuery(str, cityText);
            Inputtips inputTips = new Inputtips(PoiSearchActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        // 正确返回
        if (i == 1000) {
            List<String> listString = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                listString.add(list.get(j).getName());

                Log.e("text:", list.get(j).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1, listString);
            textContent.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {

            Toast.makeText(this, "网络错误：" + i, Toast.LENGTH_SHORT).show();
        }
    }

    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    //关闭进度条
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
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
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

            mListener.onLocationChanged(aMapLocation);

            //改变当前城市
            cityText = aMapLocation.getCity();

            //停止定位
            locationClient.stopLocation();
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

        Snackbar.make(this.findViewById(R.id.rootView), R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(PoiSearchActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PoiSearchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //同步生命周期
    @Override
    protected void onPause() {
        super.onPause();

        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mAMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mAMapView.onResume();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mAMapView.onDestroy();

        if (locationClient != null) {

            locationClient.onDestroy();
            locationClient = null;
        }
        locationClientOption = null;

    }

    @Override
    public void onClick(View view) {

        keyWord = textContent.getText().toString().trim();

        if (!keyWord.equals("")) {

            //开始poi检索
            doSearchQuery();
        } else {

            Toast.makeText(this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        // 隐藏对话框
        dissmissProgressDialog();

        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();
                        // 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
//                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "错误代码:" + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public View getInfoWindow(final Marker marker) {

        View view = getLayoutInflater().inflate(R.layout.poi_search_info_window,
                null);
        TextView title = (TextView) view.findViewById(R.id.text_title);

        String str = "现在以" + marker.getTitle() + "作为" + (isPickEnd ? "终点" : "起点");
        title.setText(str);

        Button button = (Button) view
                .findViewById(R.id.btn_nag);

        // 调起高德地图app
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

                intent.putExtra(NagMainFragment.POSITION_X, marker.getPosition().latitude);
                intent.putExtra(NagMainFragment.POSITION_Y, marker.getPosition().longitude);
                intent.putExtra(NagMainFragment.POSITION_NAME, marker.getTitle());

                PoiSearchActivity.this.setResult(RESULT_CODE, intent);

                PoiSearchActivity.this.finish();
            }
        });
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        marker.showInfoWindow();
        return false;
    }

    //开始poi检索
    protected void doSearchQuery() {
        showProgressDialog();
        // 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", cityText);
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);
        // 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);
        // 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
}
