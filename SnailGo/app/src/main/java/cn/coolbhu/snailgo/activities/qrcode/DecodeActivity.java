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

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.cars.SettingActivity;
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

    public boolean flag = true;

    //是否是更新汽车信息
    private boolean isUpdateCarInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        initToolbar();

        isUpdateCarInfo = getIntent().getBooleanExtra(SettingActivity.IS_UPDATE_CAR_INFO, false);

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

                final String a = jsonObject.getString("a");
                final String c = jsonObject.getString("c");
                final String d = jsonObject.getString("d");
                final String e = jsonObject.getString("e");
                final String f = jsonObject.getString("f");
                final String g = jsonObject.getString("g");
                final String h = jsonObject.getString("h");
                final String i = jsonObject.getString("i");
                final String j = jsonObject.getString("j");
                final String k = jsonObject.getString("k");

                final Double mileage = Double.parseDouble(e);
                final Double gas = Double.parseDouble(h);
                final Double engine = Double.parseDouble(i);
                final Double speed = Double.parseDouble(j);
                final Double light = Double.parseDouble(k);

                if (isUpdateCarInfo) {

                    BmobQuery<Car> query = new BmobQuery<>("Car");

                    query.addWhereEqualTo("Car_Num", a);
                    query.addWhereEqualTo("User_Tel",MyApplication.mUser.getUser_Tel());

                    query.findObjects(DecodeActivity.this, new FindListener<Car>() {
                        @Override
                        public void onSuccess(List<Car> list) {

                            if (list.size() > 0) {


                                Car mCar = list.get(0);

                                mCar.setCar_Mileage(mileage);
                                mCar.setCar_Gas(gas);
                                mCar.setCar_EngineStatus(engine);
                                mCar.setCar_SpeedStatus(speed);
                                mCar.setCar_LightStatus(light);

                                //更新次数
                                mCar.updateTimes();

                                mCar.setTableName("Car");

                                mCar.update(DecodeActivity.this,mCar.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {

                                        Toast.makeText(DecodeActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

                                        hideBar();

                                        DecodeActivity.this.finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                        Toast.makeText(DecodeActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();

                                        hideBar();
                                        flag = true;
                                    }
                                });
                            } else {

                                Car mCar = new Car(a, c, d, mileage, f, g, MyApplication.mUser.getUser_Tel(), gas, engine, speed, light);

                                mCar.setMileage_Times(0.0);
                                mCar.save(DecodeActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {

                                        Toast.makeText(DecodeActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

                                        hideBar();

                                        DecodeActivity.this.finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                        Toast.makeText(DecodeActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();

                                        hideBar();
                                        flag = true;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                            Toast.makeText(DecodeActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();

                            hideBar();
                            flag = true;
                        }
                    });
                }else{

                    Car mCar = new Car(a, c, d, mileage, f, g, MyApplication.mUser.getUser_Tel(), gas, engine, speed, light);

                    mCar.setMileage_Times(0.0);

                    mCar.save(DecodeActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {

                            Toast.makeText(DecodeActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();

                            hideBar();

                            DecodeActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            Toast.makeText(DecodeActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();

                            hideBar();
                            flag = true;
                        }
                    });
                }

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
