package cn.coolbhu.snailgo.activities.qrcode;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.json.JSONObject;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.Car;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DecodeActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView mQRCdoeReaderView;
    private ImageView mImageView;

    private RelativeLayout layout;
    private ProgressBar progressBar;

    public static boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        initToolbar();

        DecodeActivityPermissionsDispatcher.initWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    public void init() {

        findView();

        mQRCdoeReaderView.setOnQRCodeReadListener(this);

        //动画
        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.8f);
        mAnimation.setDuration(500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());

        mImageView.setAnimation(mAnimation);
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

                        IntentUtils.toSnailGoSettings(DecodeActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        DecodeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //findView
    private void findView() {

        mQRCdoeReaderView = (QRCodeReaderView) findViewById(R.id.activity_decode_reader);
        mImageView = (ImageView) findViewById(R.id.activity_decode_redline_image);

        layout = (RelativeLayout) findViewById(R.id.activity_decode_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

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
    public void onQRCodeRead(String text, PointF[] points) {

        Log.e("qrcode>>>text", text);

        if (flag) {

            //
            flag = false;

            //showbar
            showBar();

            try {

                JSONObject jsonObject = new JSONObject(text);

                String a = jsonObject.getString("a");
                String c = jsonObject.getString("c");
                String d = jsonObject.getString("d");
                String e = jsonObject.getString("e");
                String f = jsonObject.getString("f");
                String g = jsonObject.getString("g");
                String h = jsonObject.getString("h");
                String i = jsonObject.getString("i");
                String j = jsonObject.getString("j");
                String k = jsonObject.getString("k");

                Double mileage = Double.parseDouble(e);
                Double gas = Double.parseDouble(h);
                Double engine = Double.parseDouble(i);
                Double speed = Double.parseDouble(j);
                Double light = Double.parseDouble(k);

                Car mCar = new Car(a, c, d, mileage, f, g, MyApplication.mUser.getUser_Tel(), gas, engine, speed, light);

                mCar.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                        if (e == null) {

                            Toast.makeText(DecodeActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();

                            hideBar();

                            DecodeActivity.this.finish();
                        } else {

                            Toast.makeText(DecodeActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();

                            hideBar();
                            flag = true;
                        }
                    }
                });

            } catch (Exception e) {

                flag = true;

                hideBar();
            }
        }
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        DecodeActivityPermissionsDispatcher.startQRCodeWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    public void startQRCode() {

        mQRCdoeReaderView.getCameraManager().startPreview();
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    public void stopQRCode() {

        mQRCdoeReaderView.getCameraManager().stopPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();

        DecodeActivityPermissionsDispatcher.stopQRCodeWithCheck(this);
    }

    //showbar
    private void showBar() {

        layout.setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.VISIBLE);
    }

    //hideBar
    private void hideBar() {

        layout.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.INVISIBLE);
    }
}
