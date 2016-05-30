package com.fat246.cybercar.activities.QRCode;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.fat246.cybercar.R;

public class DecodeActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView mQRCdoeReaderView;
    private ImageView mImageView;

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

    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_decode_bar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("扫描二维码");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DecodeActivity.this.finish();
                }
            });
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        Intent mIntent = new Intent(DecodeActivity.this, QRResultActivity.class);

        Bundle mBundle = new Bundle();
        mBundle.putString("qr_info", text);
        mIntent.putExtras(mBundle);

        startActivity(mIntent);
        DecodeActivity.this.finish();
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

}
