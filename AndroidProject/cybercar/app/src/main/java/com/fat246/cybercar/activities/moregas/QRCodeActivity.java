package com.fat246.cybercar.activities.moregas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.utils.QRCodeUtil;

import org.json.JSONObject;

import java.util.Calendar;

public class QRCodeActivity extends AppCompatActivity {

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

        new QRCodeAsync(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher)).execute(mBundel);
    }

    //findView
    private void findView() {

        mImageView = (ImageView) findViewById(R.id.activity_qrcode_image);
        View view = findViewById(R.id.activity_qrcode_bar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_qrcode_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("订单二维码");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    QRCodeActivity.this.finish();
                }
            });
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
}
