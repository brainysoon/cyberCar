package cn.coolbhu.snailgo.activities.qrcode;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import cn.bmob.v3.listener.SaveListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.Car;

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

        findView();

        init();
    }

    //init
    private void init() {

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
        mQRCdoeReaderView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQRCdoeReaderView.getCameraManager().stopPreview();
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
