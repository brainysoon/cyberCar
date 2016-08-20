package com.fat246.gasservice.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.gasservice.R;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_STATION = "order_station";
    public static final String ORDER_NUM = "order_num";
    public static final String ORDER_CLASS = "order_class";
    public static final String ORDER_PRICE = "order_price";
    public static final String ORDER_ALL = "order_all";
    public static final String ORDER_TIME = "order_time";
    public static final String ORDER_STATUS = "order_status";
    public static final String ORDER_WATER = "order_water";


    //View
    private View layout_pr;
    private View layout_ok;

    //Order
    private TextView order_id;
    private TextView order_station;
    private TextView order_num;
    private TextView order_class;
    private TextView order_price;
    private TextView order_all;
    private TextView order_time;
    private TextView order_status;
    private TextView order_water;

    private Button startCarmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolber();

        initView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {

            if (data != null) {

                Bundle bundle = data.getExtras();

                if (bundle != null) {

                    order_id.setText((String) bundle.getString(ORDER_ID));
                    order_station.setText((String) bundle.getString(ORDER_STATION));
                    order_num.setText((String) bundle.getString(ORDER_NUM));
                    order_class.setText((String) bundle.getString(ORDER_CLASS));
                    order_price.setText((String) bundle.getString(ORDER_PRICE));
                    order_all.setText((String) bundle.getString(ORDER_ALL));
                    order_time.setText((String) bundle.getString(ORDER_TIME));
                    order_status.setText((String) bundle.getString(ORDER_STATUS));
                    order_water.setText((String) bundle.getString(ORDER_WATER));

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示")
                            .setMessage("订单扫描成功！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                }
            }
        }
    }

    private void initView() {

        layout_ok = findViewById(R.id.layout_ok);
        layout_pr = findViewById(R.id.layout_pr);
        startCarmer = (Button) findViewById(R.id.start_scan);

        order_id = (TextView) findViewById(R.id.order_id);
        order_station = (TextView) findViewById(R.id.order_station);
        order_num = (TextView) findViewById(R.id.order_num);
        order_class = (TextView) findViewById(R.id.order_class);
        order_price = (TextView) findViewById(R.id.order_price);
        order_all = (TextView) findViewById(R.id.order_all);
        order_time = (TextView) findViewById(R.id.order_time);
        order_status = (TextView) findViewById(R.id.order_status);
        order_water = (TextView) findViewById(R.id.order_water);


        startCarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //清空数据
                order_id.setText("");
                order_station.setText("");
                order_num.setText("");
                order_class.setText("");
                order_price.setText("");
                order_all.setText("");
                order_time.setText("");
                order_status.setText("");
                order_status.setText("");


                MainActivityPermissionsDispatcher.startCarmerWithCheck(MainActivity.this);
            }
        });
    }

    private void initToolber() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    public void startCarmer() {

        Intent intent = new Intent(this, DecodeActivity.class);

        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnShowRationale({Manifest.permission.CAMERA})
    public void showLoacationRationale(final PermissionRequest request) {

        new AlertDialog.Builder(this)
                .setTitle("请求权限")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("扫描二维码需要打开相机的权限！")
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.cancel();
                    }
                })

                .setCancelable(false)
                .show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA})
    public void showLoacationDenied() {

        Toast.makeText(this, "申请权限失败，请在设置里面打开！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
