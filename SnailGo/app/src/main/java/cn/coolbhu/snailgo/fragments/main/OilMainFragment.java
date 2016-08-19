package cn.coolbhu.snailgo.fragments.main;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.LoginActivity;
import cn.coolbhu.snailgo.activities.moregas.MoreGasActivity;
import cn.coolbhu.snailgo.beans.Brand;
import cn.coolbhu.snailgo.beans.Car;
import cn.coolbhu.snailgo.beans.Model;
import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class OilMainFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String MORE_GAS_SLECTED_CAR = "select_car";
    public static final String MORE_CAS_SLECTED_GAS = "last_gas";

    //Adapter
    private CarsAdapter mAdapter;

    //View
    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;

    //View
    private Button buttonMoreGas;
    private View layoutOut;
    private View layoutIn;
    private View rootView;
    private Button buttonLogin;

    //Check
    private int checkedNum = -1;
    private View checkedView = null;
    private int color = 0xFF4081;
    private int colorBK = 0xFFF5F5F5;

    //data
    private List<Car> mCarData = new ArrayList<>();
    private HashMap<String, Model> mModelData = new LinkedHashMap<>();
    private HashMap<String, Brand> mBrandData = new LinkedHashMap<>();
    private HashMap<String, Bitmap> mBrandSign = new LinkedHashMap<>();

    //Listener
    private ModelFindListener mModelListener = new ModelFindListener();
    private BrandFindListener mBrandListener = new BrandFindListener();
    private CarFindListener mCarFindListener = new CarFindListener();

    public static OilMainFragment newInstance() {
        OilMainFragment fragment = new OilMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oil_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View rootView) {

        buttonMoreGas = (Button) rootView.findViewById(R.id.my_button);
        mListView = (ListView) rootView.findViewById(R.id.my_cars_list);
        mPtrFrame = (PtrClassicFrameLayout) rootView.findViewById(R.id.ptr_layout);
        layoutIn = rootView.findViewById(R.id.layout_login);
        layoutOut = rootView.findViewById(R.id.layout_logout);
        buttonLogin = (Button) rootView.findViewById(R.id.to_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), LoginActivity.class);

                startActivity(intent);
            }
        });

        buttonMoreGas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(getContext(), MoreGasActivity.class);

                if (checkedNum >= 0) {

                    Car mCar = mCarData.get(checkedNum);

                    if (mCar != null) {

                        mIntent.putExtra(MORE_GAS_SLECTED_CAR, mCar.getCar_Num());
                        mIntent.putExtra(MORE_CAS_SLECTED_GAS, mCar.getCar_Gas());
                    }
                }

                startActivity(mIntent);
            }
        });

        //list
        mListView.setOnItemClickListener(this);

        initPtr();

        initList();
    }

    //initList
    private void initList() {

        mAdapter = new CarsAdapter(getContext());

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

                if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {


                    final BmobQuery<Car> query = new BmobQuery<>("Car");

                    query.addWhereMatches("User_Tel", MyApplication.mUser.getUser_Tel());

                    query.findObjects(getContext(), mCarFindListener);
                } else {


                    mCarData.clear();
                    mPtrFrame.refreshComplete();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MyApplication.isLoginSucceed) {

            layoutOut.setVisibility(View.INVISIBLE);
            layoutIn.setVisibility(View.VISIBLE);

            buttonMoreGas.setText(R.string.oil_button_bookgas);
        } else {

            layoutOut.setVisibility(View.VISIBLE);
            layoutIn.setVisibility(View.INVISIBLE);

            buttonMoreGas.setText(R.string.oil_button_see_station);
        }

        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                mPtrFrame.autoRefresh();
            }
        }, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Car mCar = mCarData.get(i);

        if (mCar != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(R.string.notice);

            builder.setIcon(R.mipmap.ic_launcher);

            builder.setMessage("你的车牌号为：" + mCar.getCar_Num() + "\n" +
                    "汽车剩余油量为：" + mCar.getCar_Gas().toString() + "%");

            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            builder.create().show();

            try {

                if (checkedNum >= 0 && checkedView != null) {

                    checkedView.setBackgroundColor(getResources().getColor(R.color.window_background));
                }

                checkedNum = i;
                checkedView = view;

                checkedView.setBackgroundColor(getResources().getColor(R.color.item_seleted_color));
            } catch (Exception ex) {

                ex.printStackTrace();
            }
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

                    query2.findObjects(getContext(), OilMainFragment.this.mBrandListener);
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

                brand.getBrand_Sign().download(getContext(), new DownloadFileListener() {
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

                        query1.findObjects(getContext(), OilMainFragment.this.mModelListener);
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
            mPtrFrame.refreshComplete();

            checkedNum = -1;
            checkedView = null;
        }

        @Override
        public void onError(int i, String s) {

            try {

                Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();

                mAdapter.notifyDataSetChanged();
                mPtrFrame.refreshComplete();
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
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
            TextView mGas = (TextView) view.findViewById(R.id.text_notice);

            Car mCar = mCarData.get(i);

            if (mCar != null) {

                Model mModel = mModelData.get(mCar.getCar_ModelType());

                if (mModel != null) {

                    Brand mBrand = mBrandData.get(mModel.getBrand_Name());

                    if (mBrand != null) {

                        Bitmap bt = mBrandSign.get(mBrand.getBrand_Name());

                        mAvatorView.setImageBitmap(bt);
                    }
                }

                mNum.setText(mCar.getCar_Num());

                mGas.setText(mCar.getCar_Gas().toString() + "%");
            }
        }
    }

}
