package com.fat246.gasservice.activities;

import android.content.Intent;
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
import com.fat246.gasservice.Order;
import com.fat246.gasservice.R;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

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

        init();
    }

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

                final String Order_ID = jsonObject.getString("Order_ID");
                String User_Tel = jsonObject.getString("User_Tel");

                if (Order_ID != null && User_Tel != null) {

                    BmobQuery<Order> bmobQuery = new BmobQuery<>();

                    bmobQuery.addWhereEqualTo("Order_ID", Order_ID);

                    bmobQuery.addWhereEqualTo("User_Tel", User_Tel);

                    //查找
                    bmobQuery.findObjects(this, new FindListener<Order>() {
                        @Override
                        public void onSuccess(List<Order> list) {

                            if (list.size() > 0) {

                                Order or = list.get(0);

                                if (or != null) {

                                    Intent intent = new Intent();

                                    Bundle bundle = new Bundle();

                                    bundle.putString(MainActivity.ORDER_ID, or.getOrder_ID());
                                    bundle.putString(MainActivity.ORDER_STATION, or.getOrder_Station());
                                    bundle.putString(MainActivity.ORDER_NUM, or.getOrder_GasNum() + "");
                                    bundle.putString(MainActivity.ORDER_PRICE, or.getOrder_GasPrice() + "");
                                    bundle.putString(MainActivity.ORDER_CLASS, or.getOrder_GasClass());
                                    bundle.putString(MainActivity.ORDER_ALL, or.getOrder_GasNum() * or.getOrder_GasPrice() + "");
                                    bundle.putString(MainActivity.ORDER_TIME, or.getOrder_Time().getDate());
                                    bundle.putString(MainActivity.ORDER_STATUS, getStatus(or.getOrder_Status()));
                                    bundle.putString(MainActivity.ORDER_WATER, or.getPay_Serial_Number());
                                    bundle.putString(MainActivity.CAR_NUM,or.getCar_Num());

                                    intent.putExtras(bundle);

                                    setResult(2, intent);

                                    flag=true;

                                    DecodeActivity.this.finish();
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                            Log.e("查找失败", i + s);

                            hideBar();

                            flag = true;

                            Toast.makeText(DecodeActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {

                flag = true;

                hideBar();
            }
        }
    }

    public String getStatus(int status) {

        if (status == -1) {

            return "未支付";
        } else if (status == 1) {

            return "已支付";
        } else if (status == 0) {

            return "支付失败";
        } else {

            return "未支付";
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

        startQRCode();
    }

    public void startQRCode() {

        mQRCdoeReaderView.getCameraManager().startPreview();
    }

    public void stopQRCode() {

        mQRCdoeReaderView.getCameraManager().stopPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopQRCode();
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
