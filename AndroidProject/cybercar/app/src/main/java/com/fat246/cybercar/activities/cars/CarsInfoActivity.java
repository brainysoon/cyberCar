package com.fat246.cybercar.activities.cars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.beans.Brand;
import com.fat246.cybercar.beans.Car;
import com.fat246.cybercar.beans.Model;
import com.fat246.cybercar.openwidgets.CircleImageView;
import com.fat246.cybercar.utils.NotificationUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class CarsInfoActivity extends AppCompatActivity {

    public static final String Action = "Action";

    //View
    private CircleImageView mBrand;
    private TextView mCarNum;
    private TextView mCarNick;
    private TextView mCarEngineNum;
    private TextView mCarRackNum;
    private TextView mCarMileage;
    private TextView mCarType;
    private TextView mCarGas;
    private TextView mCarEngineStatus;
    private TextView mCarSpeedStatus;
    private TextView mCarLightStatus;
    private TextView mCarMaintain;
    private LinearLayout linearLayout;

    private int action = 0;

    //maintain
    private String num = null;
    private String content = null;
    private Car mCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_info);

        initData();

        initToolbar();

        findView();

        initView();
    }

    //initView
    private void initView() {

        if (action != 1) {


        }
    }

    //initData
    private void initData() {

        Intent mIntent = getIntent();

        Bundle mBundle = mIntent.getExtras();

        action = mBundle.getInt(Action, 0);

        switch (action) {

            case 0:

                break;

            case 1:

                num = mBundle.getString(NotificationUtil.NOTIFICATION_NUM);
                content = mBundle.getString(NotificationUtil.NOTIFICATION_STR);

                //加载数据
                if (num != null) {
                    findCar();
                }
                break;
        }
    }

    //findCar
    private void findCar() {

        BmobQuery<Car> query = new BmobQuery<>("Car");

        query.addWhereMatches("Car_Num", num);

        query.findObjects(CarsInfoActivity.this, new FindListener<Car>() {
            @Override
            public void onSuccess(List<Car> list) {

                if (list.size() > 0) {

                    mCar = list.get(0);

                    initViewAfterFind();
                }
            }

            @Override
            public void onError(int i, String s) {

                Toast.makeText(CarsInfoActivity.this, "汽车信息加载失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViewAfterFind() {

        if (mCar != null) {

            this.mCarNum.setText(mCar.getCar_Num());
            this.mCarNick.setText(mCar.getCar_Nick());
            this.mCarRackNum.setText(mCar.getCar_RackNum());
            this.mCarEngineNum.setText(mCar.getCar_EngineNum());
            this.mCarMileage.setText(mCar.getCar_Mileage() + "");
            this.mCarType.setText(mCar.getCar_ModelType());
            this.mCarGas.setText(mCar.getCar_Gas().shortValue() + "");
            this.mCarEngineStatus.setText(mCar.getCar_EngineStatus().shortValue() + "");
            this.mCarSpeedStatus.setText(mCar.getCar_SpeedStatus().shortValue() + "");
            this.mCarLightStatus.setText(mCar.getCar_LightStatus().shortValue() + "");

            //加载图片

            final BmobQuery<Model> query = new BmobQuery<>("Model");

            query.addWhereMatches("Model_Name", mCar.getCar_ModelType());

            query.findObjects(CarsInfoActivity.this, new FindListener<Model>() {
                @Override
                public void onSuccess(List<Model> list) {

                    if (list.size() > 0) {


                        Model model = list.get(0);

                        BmobQuery<Brand> query1 = new BmobQuery<Brand>("Brand");

                        query1.addWhereMatches("Brand_Name", model.getBrand_Name());

                        query1.findObjects(CarsInfoActivity.this, new FindListener<Brand>() {
                            @Override
                            public void onSuccess(List<Brand> list) {

                                if (list.size() > 0) {

                                    Brand brand = list.get(0);

                                    if (brand != null) {

                                        brand.getBrand_Sign().download(CarsInfoActivity.this, new DownloadFileListener() {
                                            @Override
                                            public void onSuccess(String s) {

                                                Bitmap bt = BitmapFactory.decodeFile(s);//图片地址

                                                mBrand.setImageBitmap(bt);
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }

        if (content != null) {

            mCarMaintain.setText(content);

            linearLayout.setVisibility(View.VISIBLE);
        }

    }

    //findView
    private void findView() {

        mBrand = (CircleImageView) findViewById(R.id.activity_cars_info_img);
        mCarNum = (TextView) findViewById(R.id.activity_cars_info_num);
        mCarNick = (TextView) findViewById(R.id.activity_cars_info_nick);
        mCarEngineNum = (TextView) findViewById(R.id.activity_cars_info_engine);
        mCarRackNum = (TextView) findViewById(R.id.activity_cars_info_rack);
        mCarMileage = (TextView) findViewById(R.id.activity_cars_info_mileage);
        mCarType = (TextView) findViewById(R.id.activity_cars_info_type);
        mCarGas = (TextView) findViewById(R.id.activity_cars_info_gas);
        mCarEngineStatus = (TextView) findViewById(R.id.activity_cars_info_engine_status);
        mCarSpeedStatus = (TextView) findViewById(R.id.activity_cars_info_speed);
        mCarLightStatus = (TextView) findViewById(R.id.activity_cars_info_light);
        mCarMaintain = (TextView) findViewById(R.id.activity_cars_info_mantain);
        linearLayout = (LinearLayout) findViewById(R.id.activity_cars_info_layout_mantain);
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_cars_info);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("汽车信息");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CarsInfoActivity.this.finish();
                }
            });
        }
    }
}
