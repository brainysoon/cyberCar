package cn.coolbhu.snailgo.activities.regulations;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.Brand;
import cn.coolbhu.snailgo.beans.Car;
import cn.coolbhu.snailgo.beans.Model;
import cn.coolbhu.snailgo.utils.IntentUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MyRegulationsActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AMapLocationListener {

    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;
    private Button mCustom;

    //data
    private List<Car> mCarData = new ArrayList<>();
    private HashMap<String, Model> mModelData = new LinkedHashMap<>();
    private HashMap<String, Brand> mBrandData = new LinkedHashMap<>();
    private HashMap<String, Bitmap> mBrandSign = new LinkedHashMap<>();

    private CarsAdapter mAdapter;

    //Listener
    private ModelFindListener mModelListener = new ModelFindListener();
    private BrandFindListener mBrandListener = new BrandFindListener();
    private CarFindListener mCarFindListener = new CarFindListener();

    //只需要一次定位成功就行
    public int isFirstSucceed = 0;

    private String quertCityStr = null;
    private int quertCityIdStr;

    private Handler mHandler = new Handler();

    //搞得定位
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_regulations);

        initToolbar();

        findView();

        initPtr();

        initList();

        setListener();

        //初始化定位
        MyRegulationsActivityPermissionsDispatcher.initLoacationWithCheck(this);
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

                final BmobQuery<Car> query = new BmobQuery<>("Car");

                query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

                query.findObjects(MyRegulationsActivity.this, mCarFindListener);
            }
        });

        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
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

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
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

            convertView = layoutInflater.inflate(R.layout.activity_regulation_item, null);

            initView(convertView, position);

            return convertView;
        }

        //initView
        private void initView(View view, int i) {

            CircleImageView mAvatorView = (CircleImageView) view.findViewById(R.id.activity_my_cars_item_image);
            TextView mNum = (TextView) view.findViewById(R.id.activity_my_cars_item_num);
            TextView mNick = (TextView) view.findViewById(R.id.activity_my_cars_item_nick);

            Car mCar = mCarData.get(i);

            Model mModel = mModelData.get(mCar.getCar_ModelType());

            if (mModel != null) {

                Brand mBrand = mBrandData.get(mModel.getBrand_Name());

                if (mBrand != null) {

                    Bitmap bt = mBrandSign.get(mBrand.getBrand_Name());

                    mAvatorView.setImageBitmap(bt);
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

                final Brand brand = list.get(0);

                mBrandData.put(brand.getBrand_Name(), brand);

                brand.getBrand_Sign().download(MyRegulationsActivity.this, new DownloadFileListener() {
                    @Override
                    public void onSuccess(String s) {

                        Bitmap bt = BitmapFactory.decodeFile(s);

                        mBrandSign.put(brand.getBrand_Name(), bt);

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        }

        @Override
        public void onError(int i, String s) {

        }
    }

    class CarFindListener extends FindListener<Car> {

        @Override
        public void onSuccess(List<Car> list) {

            if (list.size() > 0) {

                mCarData = list;

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
            mPtrFrame.refreshComplete();
        }

        @Override
        public void onError(int i, String s) {
            Toast.makeText(MyRegulationsActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
            mPtrFrame.refreshComplete();
        }
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

            isFirstSucceed++;

            String province = aMapLocation.getProvince();

            String city = aMapLocation.getCity();

            province = province.substring(0, 2);

            city = city.substring(0, 2);


            Log.e("here_>>>>", city + province);

            Log.e("city_code", aMapLocation.getCityCode());

            int cityId = CustomRegulationActivity.getCityId(province, city);

            if (cityId < 0) return;

            quertCityIdStr = cityId;

            quertCityStr = city;

            locationClient.stopLocation();
        }
    }

    //开始定位
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initLoacation() {

        //初始化
        locationClient = new AMapLocationClient(this.getApplicationContext());

        //初始化车首页
        initCheShouYe();

        locationClientOption = new AMapLocationClientOption();


        // 设置定位模式为低功耗模式
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置只定位一次
//        locationClientOption.setOnceLocation(true);

        locationClientOption.setInterval(1000);

        // 设置定位监听
        locationClient.setLocationListener(this);

        //开始定位
        locationClient.startLocation();
    }

    public void initCheShouYe() {

        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);

        // 您的appId
        weizhangIntent.putExtra("appId", 1626);
        // 您的appKey
        weizhangIntent.putExtra("appKey", "624fbe005c8138700f31bdfca484f878");

        //开启服务
        startService(weizhangIntent);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showLoacationRationale(final PermissionRequest request) {

        new AlertDialog.Builder(this)
                .setTitle(R.string.request_permission)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.request_location_permission)
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

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showLoacationDenied() {

        Snackbar.make(this.findViewById(R.id.rootView), R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(MyRegulationsActivity.this);
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationClient != null) {

            locationClient.onDestroy();
            locationClient = null;
        }

        locationClientOption = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MyRegulationsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
