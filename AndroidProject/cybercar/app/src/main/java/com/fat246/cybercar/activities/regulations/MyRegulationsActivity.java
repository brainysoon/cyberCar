package com.fat246.cybercar.activities.regulations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cheshouye.api.client.json.CarInfo;
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
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MyRegulationsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;
    private Button mCustom;

    //data
    private List<Car> mCarData = new ArrayList<>();
    private HashMap<String, Model> mModelData = new LinkedHashMap<>();
    private HashMap<String, Brand> mBrandData = new LinkedHashMap<>();

    private CarsAdapter mAdapter;

    //Listener
    private ModelFindListener mModelListener = new ModelFindListener();
    private BrandFindListener mBrandListener = new BrandFindListener();

    //定位
    LocationClient mLocationClient;

    //只需要一次定位成功就行
    public int isFirstSucceed = 0;

    private String quertCityStr = null;
    private int quertCityIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_regulations);

        initToolbar();

        findView();

        initPtr();

        initList();

        setListener();

        beginToRefreshX();

        initLoacation();
    }

    //initLocation
    //开始定位
    private void initLoacation() {

        //初始化
        mLocationClient = new LocationClient(this);

        //注册监听事件
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                //最多定位10次，放弃定位
                if (isFirstSucceed > 10) {

                    mLocationClient.stop();
                } else {

                    isFirstSucceed++;

                    String province = bdLocation.getProvince();

                    String city = bdLocation.getCity();

                    province = province.substring(0, 2);

                    city = city.substring(0, 2);


                    Log.e("here_>>>>", city + province);

                    Log.e("city_code", bdLocation.getCityCode());

                    int cityId = CustomRegulationActivity.getCityId(province, city);

                    if (cityId < 0) return;

                    quertCityIdStr = cityId;

                    quertCityStr = city;

                    //成功就停止定位
                    mLocationClient.stop();
                }
            }
        });

        //设置
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); //打开GPS

        option.setCoorType("gcj02"); //设置坐标类型
        option.setScanSpan(1000);   //定位间隙

        option.setIsNeedAddress(true);

        //绑定设置
        mLocationClient.setLocOption(option);

        //开始定位
        mLocationClient.start();
    }

    //initList
    private void initList() {

        mAdapter = new CarsAdapter(this);

        mListView.setAdapter(mAdapter);
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

    //setListener
    private void setListener() {

        //Button
        mCustom.setOnClickListener(this);

        //list
        mListView.setOnItemClickListener(this);
    }

    //findView
    private void findView() {

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.activity_my_regulations_ptr);
        mListView = (ListView) findViewById(R.id.activity_my_regulations_list);
        mCustom = (Button) findViewById(R.id.activity_my_regulations_button);
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_my_regulation_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("违章");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MyRegulationsActivity.this.finish();
                }
            });
        }
    }

    //OnButtonClick
    @Override
    public void onClick(View v) {

        Intent mInent = new Intent(this, CustomRegulationActivity.class);

        startActivity(mInent);

        this.finish();
    }

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

                    mBrand.getBrand_Sign().download(MyRegulationsActivity.this, new DownloadFileListener() {
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

                    query2.findObjects(MyRegulationsActivity.this, MyRegulationsActivity.this.mBrandListener);
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

            query.findObjects(MyRegulationsActivity.this, new FindListener<Car>() {
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

                                query1.findObjects(MyRegulationsActivity.this, MyRegulationsActivity.this.mModelListener);
                            }
                        }
                    }


                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(MyRegulationsActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            mAdapter.notifyDataSetChanged();

            mPtrFrame.refreshComplete();
        }
    }

    private void beginToRefreshX() {

        final BmobQuery<Car> query = new BmobQuery<>("Car");

        query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

        query.findObjects(MyRegulationsActivity.this, new FindListener<Car>() {
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

                            query1.findObjects(MyRegulationsActivity.this, MyRegulationsActivity.this.mModelListener);
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyRegulationsActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CarInfo car = new CarInfo();

        car.setChejia_no(mCarData.get(position).getCar_RackNum());
        car.setChepai_no(mCarData.get(position).getCar_Num());
        car.setEngine_no(mCarData.get(position).getCar_EngineNum());

        if (checkOk()) {

            car.setCity_id(quertCityIdStr);

            Intent intent = new Intent(MyRegulationsActivity.this, RegulationResultActivity.class);

            Bundle bundle = new Bundle();

            bundle.putString("carInfo", car.toJSONObject().toString());
            intent.putExtras(bundle);

            startActivity(intent);

        }
    }

    //check
    private boolean checkOk() {

        if (isFirstSucceed > 0) {

            return true;
        }

        Toast.makeText(this, "定位没有成功，请稍后再试！", Toast.LENGTH_SHORT).show();
        return false;
    }
}
