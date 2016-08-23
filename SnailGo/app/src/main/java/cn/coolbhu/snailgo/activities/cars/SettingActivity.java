package cn.coolbhu.snailgo.activities.cars;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.qrcode.DecodeActivity;
import cn.coolbhu.snailgo.fragments.SettingFragment;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SettingActivity extends AppCompatActivity {

    public static final String  IS_UPDATE_CAR_INFO="is_update_info";

    private FloatingActionButton mUpdateCarInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initToolbar();

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_setting_container, SettingFragment.newInstance()).commit();

        mUpdateCarInfo = (FloatingActionButton) findViewById(R.id.update_my_cars_action);

        mUpdateCarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SettingActivityPermissionsDispatcher.initWithCheck(SettingActivity.this);
            }
        });
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

    //初始化二维码扫描按钮
    @NeedsPermission({Manifest.permission.CAMERA})
    public void init() {

        Intent mIntnet = new Intent(SettingActivity.this, DecodeActivity.class);

        mIntnet.putExtra(IS_UPDATE_CAR_INFO,true);

        startActivity(mIntnet);
    }

    @OnShowRationale({Manifest.permission.CAMERA})
    public void showLoacationRationale(final PermissionRequest request) {

        new AlertDialog.Builder(this)
                .setTitle(R.string.request_permission)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.request_camera_permission)
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

    @OnPermissionDenied({Manifest.permission.CAMERA})
    public void showLoacationDenied() {

        Snackbar.make(this.findViewById(R.id.rootView), R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(SettingActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        SettingActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
}
