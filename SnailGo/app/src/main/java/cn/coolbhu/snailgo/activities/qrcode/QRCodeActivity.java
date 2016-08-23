package cn.coolbhu.snailgo.activities.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;

import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.cars.MyCarsActivity;
import cn.coolbhu.snailgo.beans.Car;
import cn.coolbhu.snailgo.utils.QRCodeUtil;

public class QRCodeActivity extends AppCompatActivity {

    public static final String Action = "action";

    private ImageView mImageView;
    private ProgressBar progressBar;

    private static final int Xlenght = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        initToolbar();

        findView();

        initData();
    }

    //initData
    private void initData() {

        showBar();

        Intent intent = getIntent();

        Bundle mBundel = intent.getExtras();

        int i = mBundel.getInt(Action);

        switch (i) {

            case 0:

                new QRCodeAsync(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher)).execute(mBundel);

                break;

            case 1:

                String str = mBundel.getString("car_info", "");

                if (MyCarsActivity.mCarClick != null) {

                    new QRCodeStrAsync(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher)).execute(MyCarsActivity.mCarClick);

                }

                break;
        }
    }

    //findView
    private void findView() {

        mImageView = (ImageView) findViewById(R.id.activity_qrcode_image);
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

    //showBar
    private void showBar() {

        progressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
    }

    //hideBar
    private void hideBar() {
        progressBar.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.VISIBLE);
    }

    class QRCodeAsync extends AsyncTask<Bundle, Void, String> {

        Bitmap logo;

        public QRCodeAsync(Bitmap logo) {

            this.logo = logo;
        }

        @Override
        protected String doInBackground(Bundle... params) {

            Bundle mBundel = params[0];

            String id = mBundel.getString("Order_ID", "null");
            String tel = mBundel.getString("User_Tel", "null");
            Double price = mBundel.getDouble("Order_GasPrice", 0);
            Double num = mBundel.getDouble("Order_GasNum", 0);
            String car=mBundel.getString("Car_Num");

            if (id.equals("null") || tel.equals("null")) {

                return "null";
            }

            String content = null;

            try {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("Order_ID", id);
                jsonObject.put("User_Tel", tel);
                jsonObject.put("Order_GasPrice", price);
                jsonObject.put("Order_GasNum", num);
                jsonObject.put("Car_Num",car);

                content = jsonObject.toString();

            } catch (Exception e) {

                return "null";
            }

            String name = MyApplication.USER_AVATOR_DIRCTORY + Calendar.getInstance().getTimeInMillis() + ((int) (Math.random() * 1000)) + ".png";

            boolean isSucceed = QRCodeUtil.createQRImage(content, Xlenght, Xlenght,
                    logo, name);

            if (isSucceed) {

                return name;
            } else {

                return "null";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("null")) {

                Toast.makeText(QRCodeActivity.this, "加载失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            } else {

                Bitmap bm = BitmapFactory.decodeFile(s);

                mImageView.setImageBitmap(bm);
            }

            hideBar();
        }
    }

    class QRCodeStrAsync extends AsyncTask<Car, Void, String> {

        private Bitmap logo;

        @Override
        protected String doInBackground(Car... params) {

            Car mCar = params[0];

            JSONObject jsonObject = new JSONObject();

            try {

                jsonObject.put("a", mCar.getCar_Num());
                jsonObject.put("c", mCar.getCar_RackNum());
                jsonObject.put("d", mCar.getCar_EngineNum());
                jsonObject.put("e", mCar.getCar_Mileage().toString());
                jsonObject.put("f", mCar.getCar_Nick());
                jsonObject.put("g", mCar.getCar_ModelType());
                jsonObject.put("h", mCar.getCar_Gas().toString());
                jsonObject.put("i", mCar.getCar_EngineStatus().toString());
                jsonObject.put("j", mCar.getCar_SpeedStatus().toString());
                jsonObject.put("k", mCar.getCar_LightStatus().toString());

            } catch (Exception e) {

            }

            String str = jsonObject.toString();

            String name = MyApplication.USER_AVATOR_DIRCTORY + Calendar.getInstance().getTimeInMillis() + ((int) (Math.random() * 1000)) + ".png";

            boolean isSucceed = QRCodeUtil.createQRImage(str, Xlenght, Xlenght,
                    logo, name);

            if (isSucceed) {

                return name;
            } else {

                return "null";
            }
        }

        public QRCodeStrAsync(Bitmap logo) {

            this.logo = logo;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("null")) {

                Toast.makeText(QRCodeActivity.this, "加载失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            } else {

                Bitmap bm = BitmapFactory.decodeFile(s);

                mImageView.setImageBitmap(bm);
            }

            hideBar();

        }
    }
}
