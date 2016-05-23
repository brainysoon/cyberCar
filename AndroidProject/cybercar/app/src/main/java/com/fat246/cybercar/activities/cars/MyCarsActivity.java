package com.fat246.cybercar.activities.cars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.Brand;
import com.fat246.cybercar.beans.Car;
import com.fat246.cybercar.beans.Model;
import com.fat246.cybercar.openwidgets.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MyCarsActivity extends AppCompatActivity implements AddCarsActivity.succeedAdd {

    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;
    private FloatingActionButton mAction;

    //详细信息
    private CircleImageView mImageView;
    private TextView mNum;
    private TextView mNick;
    private TextView mRack;
    private TextView mEngine;
    private TextView mBrand;
    private TextView mModel;
    private TextView mMileage;
    private ImageView mCancle;
    private LinearLayout mDialog;

    private boolean mActionFlag = false;
    private Car mCarClick = null;

    public static AddCarsActivity.succeedAdd succeed = null;

    //data
    private List<Car> mCarData = new ArrayList<>();
    private HashMap<String, Model> mModelData = new LinkedHashMap<>();
    private HashMap<String, Brand> mBrandData = new LinkedHashMap<>();

    private CarsAdapter mAdapter;

    //Listener
    private ModelFindListener mModelListener = new ModelFindListener();
    private BrandFindListener mBrandListener = new BrandFindListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);

        succeed = this;

        initToolbar();

        initView();

        beginToRefreshX();
    }

    //initView;
    private void initView() {

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.activity_my_cars_ptr);
        mListView = (ListView) findViewById(R.id.activity_my_cars_list);
        mAction = (FloatingActionButton) findViewById(R.id.activity_my_cars_action);

        mImageView = (CircleImageView) findViewById(R.id.activity_my_cars_image);
        mNum = (TextView) findViewById(R.id.activity_my_cars_num);
        mNick = (TextView) findViewById(R.id.activity_my_cars_nick);
        mRack = (TextView) findViewById(R.id.activity_my_cars_rack);
        mEngine = (TextView) findViewById(R.id.activity_my_cars_engine);
        mBrand = (TextView) findViewById(R.id.activity_my_cars_brand);
        mModel = (TextView) findViewById(R.id.activity_my_cars_model);
        mMileage = (TextView) findViewById(R.id.activity_my_cars_mileage);
        mCancle = (ImageView) findViewById(R.id.activity_my_cars_cancle);
        mDialog = (LinearLayout) findViewById(R.id.activity_my_cars_dialog);


        initListView();

        initPtr();

        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mActionFlag) {

                    mCarClick.setTableName("Car");

                    mCarClick.delete(MyCarsActivity.this, new DeleteListener() {
                        @Override
                        public void onSuccess() {

                            Toast.makeText(MyCarsActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();

                            toBack();

                            mPtrFrame.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //开始刷新
                                    new CarsAsync().execute();
                                }
                            }, 500);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            Toast.makeText(MyCarsActivity.this, "删除失败!", Toast.LENGTH_SHORT).show();

                            Log.e("here>>>" + i, s);
                        }
                    });

                } else {

                    Intent mIntnet = new Intent(MyCarsActivity.this, AddCarsActivity.class);

                    startActivity(mIntnet);
                }
            }
        });
    }

    private void toDialog() {

        mDialog.setVisibility(View.VISIBLE);
        mActionFlag = true;
        mAction.setImageResource(R.drawable.ic_cancel_80);
    }

    private void toBack() {

        mDialog.setVisibility(View.INVISIBLE);
        mActionFlag = false;
        mAction.setImageResource(R.drawable.ic_add);
    }

    //initPtr
    private void initPtr() {

        mPtrFrame.setLastUpdateTimeRelateObject(this);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                //开始刷新
                new CarsAsync().execute();

            }
        });

        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                //开始刷新
                new CarsAsync().execute();
            }
        }, 1000);

    }

    private void initListView() {

        mAdapter = new CarsAdapter(MyCarsActivity.this);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                toDialog();

                CircleImageView imageView = (CircleImageView) view.findViewById(R.id.activity_my_cars_item_image);

                Bitmap bt = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                mImageView.setImageBitmap(bt);

                mCarClick = mCarData.get(position);

                Model model = mModelData.get(mCarClick.getCar_ModelType());

                if (model != null) {

                    mBrand.setText(model.getBrand_Name());
                }

                mNum.setText(mCarClick.getCar_Num());
                mNick.setText(mCarClick.getCar_Nick());
                mRack.setText(mCarClick.getCar_RackNum());
                mEngine.setText(mCarClick.getCar_EngineNum());
                mMileage.setText(mCarClick.getCar_Mileage() + "");
                mModel.setText(mCarClick.getCar_ModelType());
            }
        });

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toBack();
            }
        });
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

                    MyCarsActivity.this.finish();
                }
            });
        }
    }

    //
    class CarsAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        public CarsAdapter(Context context) {

            this.context = context;

            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mCarData.size();
        }

        @Override
        public Object getItem(int position) {
            return mCarData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = layoutInflater.inflate(R.layout.activity_my_cars_item, null);

            initView(convertView, position);

            return convertView;
        }

        //initView
        private void initView(View view, int i) {

            final CircleImageView mAvatorView = (CircleImageView) view.findViewById(R.id.activity_my_cars_item_image);
            TextView mNum = (TextView) view.findViewById(R.id.activity_my_cars_item_num);
            TextView mNick = (TextView) view.findViewById(R.id.activity_my_cars_item_nick);

            Car mCar = mCarData.get(i);

            Model mModel = mModelData.get(mCar.getCar_ModelType());

            if (mModel != null) {

                Brand mBrand = mBrandData.get(mModel.getBrand_Name());

                if (mBrand != null) {

                    mBrand.getBrand_Sign().download(MyCarsActivity.this, new DownloadFileListener() {
                        @Override
                        public void onSuccess(String s) {

                            Bitmap bt = BitmapFactory.decodeFile(s);//图片地址

                            mAvatorView.setImageBitmap(bt);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            mNum.setText(mCar.getCar_Num());

            mNick.setText(mCar.getCar_Nick());
        }
    }

    class ModelFindListener extends FindListener<Model> {

        @Override
        public void onSuccess(List<Model> list) {

            if (list.size() > 0) {

                Model model = list.get(0);
                mModelData.put(model.getModel_Name(), model);

                if (mBrandData.get(model.getBrand_Name()) == null) {

                    BmobQuery<Brand> query2 = new BmobQuery<>("Brand");

                    query2.addWhereMatches("Brand_Name", model.getBrand_Name());

                    query2.findObjects(MyCarsActivity.this, MyCarsActivity.this.mBrandListener);
                }
            }
        }

        @Override
        public void onError(int i, String s) {

        }
    }

    class BrandFindListener extends FindListener<Brand> {

        @Override
        public void onSuccess(List<Brand> list) {

            if (list.size() > 0) {

                Brand brand = list.get(0);

                mBrandData.put(brand.getBrand_Name(), brand);
            }
        }

        @Override
        public void onError(int i, String s) {

        }
    }

    class CarsAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            beginToRefresh();

            return null;
        }

        private void beginToRefresh() {

            final BmobQuery<Car> query = new BmobQuery<>("Car");

            query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

            query.findObjects(MyCarsActivity.this, new FindListener<Car>() {
                @Override
                public void onSuccess(List<Car> list) {

                    if (list.size() > 0) {

                        mCarData = list;

                        mModelData.clear();
                        mBrandData.clear();

                        for (Car i : list) {

                            //没有这个型号
                            if (mModelData.get(i.getCar_ModelType()) == null) {

                                BmobQuery<Model> query1 = new BmobQuery<>("Model");

                                query1.addWhereMatches("Model_Name", i.getCar_ModelType());

                                query1.findObjects(MyCarsActivity.this, MyCarsActivity.this.mModelListener);
                            }
                        }
                    }


                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(MyCarsActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            mAdapter.notifyDataSetChanged();

            mPtrFrame.refreshComplete();

            Log.e("here", "comes");
        }
    }

    private void beginToRefreshX() {

        final BmobQuery<Car> query = new BmobQuery<>("Car");

        query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

        query.findObjects(MyCarsActivity.this, new FindListener<Car>() {
            @Override
            public void onSuccess(List<Car> list) {

                if (list.size() > 0) {

                    mCarData = list;

                    mModelData.clear();
                    mBrandData.clear();

                    for (Car i : list) {

                        //没有这个型号
                        if (mModelData.get(i.getCar_ModelType()) == null) {

                            BmobQuery<Model> query1 = new BmobQuery<>("Model");

                            query1.addWhereMatches("Model_Name", i.getCar_ModelType());

                            query1.findObjects(MyCarsActivity.this, MyCarsActivity.this.mModelListener);
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyCarsActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void succeedAddCars() {

        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                //开始刷新
                new CarsAsync().execute();
            }
        }, 500);
    }
}
