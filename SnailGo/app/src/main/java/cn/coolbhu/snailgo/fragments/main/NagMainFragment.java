package cn.coolbhu.snailgo.fragments.main;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.navigates.PoiSearchActivity;
import cn.coolbhu.snailgo.activities.navigates.RoutePlanActivity;
import cn.coolbhu.snailgo.utils.ConnectivityUtils;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class NagMainFragment extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, AMapLocationListener {

    public static final int REQUEST_CODE_START = 0;
    public static final int REQUEST_CODE_END = 1;

    public static final String POSITION_X = "position_x";
    public static final String POSITION_Y = "position_y";
    public static final String POSITION_NAME = "position_name";
    public static final String POSITION_X_S = "position_xx";
    public static final String POSITION_Y_S = "position_yy";

    public static final String STATUS_JAM = "status_jam";
    public static final String STATUS_SPEED = "status_speed";
    public static final String STATUS_CHARGE = "status_charge";
    public static final String STATUS_NOT_SPEED = "status_not_speed";

    //View
    private TextView mStartView;
    private TextView mEndView;
    private TextView myPlaceView;

    private View rootView;

    private CheckBox boxJam = null;
    private CheckBox boxNotSpeedWay = null;
    private CheckBox boxAvoidCharge = null;
    private CheckBox boxSpeedWayFirst = null;

    //起点和终点的信息
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private String startName;
    private String endName;

    //交换起始位置

    //定位
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;

    //进度条
    private ProgressDialog progDialog;

    public NagMainFragment() {
        // Required empty public constructor
    }

    public static NagMainFragment newInstance() {
        NagMainFragment fragment = new NagMainFragment();
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
        return inflater.inflate(R.layout.fragment_nag_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;
        mStartView = (TextView) view.findViewById(R.id.start_view);
        mEndView = (TextView) view.findViewById(R.id.end_view);

        boxJam = (CheckBox) view.findViewById(R.id.box_avoid_jam);
        boxAvoidCharge = (CheckBox) view.findViewById(R.id.box_avoid_charge);
        boxNotSpeedWay = (CheckBox) view.findViewById(R.id.box_not_speedway);
        boxSpeedWayFirst = (CheckBox) view.findViewById(R.id.box_speedway_first);
        myPlaceView = (TextView) view.findViewById(R.id.hint_my_place);

        ImageView buttonSwap = (ImageView) view.findViewById(R.id.fragment_init_navigate_img_swap);

        buttonSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startName != null && endName != null) {

                    String str = startName;
                    startName = endName;
                    endName = str;

                    double tmp = startX;
                    startX = endX;
                    endX = tmp;

                    tmp = startY;
                    startY = endY;
                    endY = tmp;

                    myPlaceView.setText("");
                    mStartView.setText(startName);
                    mEndView.setText(endName);
                } else {

                    Toast.makeText(getContext(), "起点和终点都选择才可以交换哦！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mStartView.setOnClickListener(this);
        mEndView.setOnClickListener(this);

        Button button = (Button) view.findViewById(R.id.fragment_init_navigate_button_nav);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startName != null && endName != null && !startName.equals("") && !endName.equals("")) {

                    Intent intent = new Intent(getContext(), RoutePlanActivity.class);

                    intent.putExtra(POSITION_X, endX);
                    intent.putExtra(POSITION_Y, endY);
                    intent.putExtra(POSITION_X_S, startX);
                    intent.putExtra(POSITION_Y_S, startY);

                    intent.putExtra(STATUS_JAM, boxJam.isChecked());
                    intent.putExtra(STATUS_CHARGE, boxAvoidCharge.isChecked());
                    intent.putExtra(STATUS_SPEED, false);
                    intent.putExtra(STATUS_NOT_SPEED, true);

                    startActivity(intent);
                } else {

                    Toast.makeText(getContext(), "起点和终点不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        boxJam.setOnCheckedChangeListener(this);
        boxAvoidCharge.setOnCheckedChangeListener(this);
        boxNotSpeedWay.setOnCheckedChangeListener(this);
        boxSpeedWayFirst.setOnCheckedChangeListener(this);

        NagMainFragmentPermissionsDispatcher.initLoacationWithCheck(this);

        //检查是联网
        ConnectivityUtils.shouldShowNotConnectdNotic(getContext());
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(getContext(), PoiSearchActivity.class);

        switch (view.getId()) {

            case R.id.start_view:

                intent.putExtra(PoiSearchActivity.IS_PICK_END, false);
                startActivityForResult(intent, REQUEST_CODE_START);
                break;

            case R.id.end_view:

                intent.putExtra(PoiSearchActivity.IS_PICK_END, true);
                startActivityForResult(intent, REQUEST_CODE_END);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == PoiSearchActivity.RESULT_CODE) {

            switch (requestCode) {

                case REQUEST_CODE_START:

                    startX = data.getDoubleExtra(POSITION_X, 0.0);
                    startY = data.getDoubleExtra(POSITION_Y, 0.0);
                    startName = data.getStringExtra(POSITION_NAME);

                    if (startName != null) {

                        mStartView.setText(startName);
                    }
                    break;

                case REQUEST_CODE_END:

                    endX = data.getDoubleExtra(POSITION_X, 0.0);
                    endY = data.getDoubleExtra(POSITION_Y, 0.0);
                    endName = data.getStringExtra(POSITION_NAME);

                    if (endName != null) {

                        mEndView.setText(endName);
                    }
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

//        if (!b) return;
//
//        switch (compoundButton.getId()) {
//
//            case R.id.box_avoid_jam:
//
//                break;
//
//            case R.id.box_avoid_charge:
//
//                break;
//
//            case R.id.box_not_speedway:
//
//                if (boxSpeedWayFirst.isChecked()) {
//
//                    boxSpeedWayFirst.setChecked(false);
//                }
//
//                break;
//
//            case R.id.box_speedway_first:
//
//                if (boxNotSpeedWay.isChecked()) {
//
//                    boxNotSpeedWay.setChecked(false);
//                }
//                break;
//        }

        //修复必须选择一个偏好
        switch (compoundButton.getId()){

            case R.id.box_not_speedway:

                boxSpeedWayFirst.setChecked(!b);

                break;

            case R.id.box_speedway_first:

                boxNotSpeedWay.setChecked(!b);
                break;
        }
    }

    //开始定位
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initLoacation() {

        //初始化
        locationClient = new AMapLocationClient(this.getContext().getApplicationContext());

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

        showProgressDialog();
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

        Snackbar.make(rootView, R.string.permission_denie, Snackbar.LENGTH_SHORT)
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

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

            startX = aMapLocation.getLatitude();
            startY = aMapLocation.getLongitude();
            startName = aMapLocation.getPoiName();

            mStartView.setText(startName);
            locationClient.stopLocation();

            progDialog.dismiss();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        NagMainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setMessage("正在定位:\n" + "请稍后。。。。");
        progDialog.show();
    }
}
