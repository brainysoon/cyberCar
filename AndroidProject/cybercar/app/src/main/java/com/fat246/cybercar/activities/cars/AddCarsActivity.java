package com.fat246.cybercar.activities.cars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.Brand;
import com.fat246.cybercar.beans.Car;
import com.fat246.cybercar.beans.Model;
import com.fat246.cybercar.openwidgets.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class AddCarsActivity extends AppCompatActivity {

    private CircleImageView mBrandSign;
    private Spinner mBrand;
    private Spinner mModel;
    private TextInputEditText mNum;
    private TextInputEditText mRack;
    private TextInputEditText mEngine;
    private TextInputEditText mMileage;
    private TextInputEditText mNick;
    private Button mAdd;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    //data
    private List<Brand> mBrandData = new ArrayList<>();
    private List<Model> mModelData = new ArrayList<>();

    //adapter
    private BrandAdapter mBrandAdapter;
    private ModelAdapter mModelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cars);

        initToolbar();

        initView();

        setAdapter();
    }

    //setAdapter
    private void setAdapter() {

        mBrandAdapter = new BrandAdapter(this);
        mModelAdapter = new ModelAdapter(this);

        mBrand.setAdapter(mBrandAdapter);
        mModel.setAdapter(mModelAdapter);
    }


    //initView
    private void initView() {

        //findView
        mBrandSign = (CircleImageView) findViewById(R.id.activity_add_cars_image);
        mBrand = (Spinner) findViewById(R.id.activity_add_cars_brand);
        mModel = (Spinner) findViewById(R.id.activity_add_cars_model);
        mNum = (TextInputEditText) findViewById(R.id.activity_add_cars_num);
        mRack = (TextInputEditText) findViewById(R.id.activity_add_cars_rack);
        mEngine = (TextInputEditText) findViewById(R.id.activity_add_cars_engine);
        mMileage = (TextInputEditText) findViewById(R.id.activity_add_cars_mileage);
        mNick = (TextInputEditText) findViewById(R.id.activity_add_cars_nick);
        mAdd = (Button) findViewById(R.id.activity_add_cars_add);
        progressBar = (ProgressBar) findViewById(R.id.activity_add_cars_progressbar);
        scrollView = (ScrollView) findViewById(R.id.activity_add_cars_scrollview);

        setListener();

        initData();
    }

    //initData 加载数据
    private void initData() {

        final BmobQuery<Brand> query = new BmobQuery<>("Brand");

        final BmobQuery<Model> query1 = new BmobQuery<>("Model");

        query.findObjects(this, new FindListener<Brand>() {
            @Override
            public void onSuccess(List<Brand> list) {

                if (list.size() > 0) {

                    mBrandData = list;

                    Brand brand = mBrandData.get(0);

                    query1.addWhereMatches("Brand_Name", brand.getBrand_Name());

                    query1.findObjects(AddCarsActivity.this, new FindListener<Model>() {
                        @Override
                        public void onSuccess(List<Model> list) {

                            if (list.size() > 0) {

                                mModelData = list;

                                mModelAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                            Log.e("here>>i", s);
                        }
                    });

                    brand.getBrand_Sign().download(AddCarsActivity.this, new DownloadFileListener() {
                        @Override
                        public void onSuccess(String s) {

                            Bitmap bt = BitmapFactory.decodeFile(s);//图片地址

                            mBrandSign.setImageBitmap(bt);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            Toast.makeText(AddCarsActivity.this, "图片加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    });

                    mBrandAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {

                Log.e("here>>i", s);
            }
        });
    }

    //setListener
    private void setListener() {

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String num = mNum.getText().toString().trim();
                String rack = mRack.getText().toString().trim();
                String engine = mEngine.getText().toString().trim();
                String mlieage = mMileage.getText().toString().trim();
                String nick = mNick.getText().toString().trim();

                String model = ((Model) mModel.getSelectedItem()).getModel_Name();

                Double mMlieage = Double.parseDouble(mlieage);

                //假设所有验证都完事了

                Car mCar = new Car(num, rack, engine, mMlieage, nick
                        , model, MyApplication.mUser.getUser_Tel());

                mCar.save(AddCarsActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        Toast.makeText(AddCarsActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();

                        if (MyCarsActivity.succeed != null) {

                            MyCarsActivity.succeed.succeedAddCars();
                        }

                        AddCarsActivity.this.finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                        Toast.makeText(AddCarsActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mBrand.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        Brand brand = mBrandData.get(position);

                        brand.getBrand_Sign().download(AddCarsActivity.this, new DownloadFileListener() {
                            @Override
                            public void onSuccess(String s) {

                                Bitmap bt = BitmapFactory.decodeFile(s);//图片地址

                                mBrandSign.setImageBitmap(bt);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });

                        BmobQuery<Model> query = new BmobQuery<>();

                        query.addWhereMatches("Brand_Name", brand.getBrand_Name());

                        query.findObjects(AddCarsActivity.this, new FindListener<Model>() {
                            @Override
                            public void onSuccess(List<Model> list) {

                                if (list.size() > 0) {

                                    mModelData = list;

                                    mModelAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    //校验车牌号
    private boolean checkNum(String num) {

        if (num.length() != 5) {

            return false;
        }

        return true;
    }

    //校验车架号
    private boolean checkRack(String rack) {

        return true;
    }

    //校验引擎号
    private boolean checkEngin(String engine) {

        return true;
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_my_cars_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AddCarsActivity.this.finish();
                }
            });
        }
    }

    class BrandAdapter extends BaseAdapter {

        private Context context;

        public BrandAdapter(Context context) {

            this.context = context;
        }

        @Override
        public int getCount() {
            return mBrandData.size();
        }

        @Override
        public Object getItem(int position) {
            return mBrandData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = new TextView(context);

            textView.setText(mBrandData.get(position).getBrand_Name());

            return textView;
        }
    }

    class ModelAdapter extends BaseAdapter {

        private Context context;

        public ModelAdapter(Context context) {

            this.context = context;
        }

        @Override
        public int getCount() {
            return mModelData.size();
        }

        @Override
        public Object getItem(int position) {
            return mModelData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = new TextView(context);

            textView.setText(mModelData.get(position).getModel_Name());
            return textView;
        }
    }

    public interface succeedAdd {

        void succeedAddCars();
    }
}
