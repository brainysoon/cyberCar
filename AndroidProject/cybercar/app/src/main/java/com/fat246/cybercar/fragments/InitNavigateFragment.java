package com.fat246.cybercar.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.navigate.InitNavigateActivity;
import com.fat246.cybercar.activities.navigate.NavigateActivity;

import java.util.ArrayList;
import java.util.List;

public class InitNavigateFragment extends Fragment {

    //poi
    private PoiSearch mPoiSearch;

    //location
    private LocationClient mLocation = null;
    private BDLocation mNowLocation = null;

    //poiInfo
    private List<PoiInfo> mPoiInfo = new ArrayList<>();

    //first
    private boolean isFirstLocation = false;

    //View
    private EditText mStart;
    private EditText mEnd;
    private Button mNav;
    private ImageView mRef;
    private ListView mListView;
    private FloatingActionButton mAction;
    private ImageView mSwap;

    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    //位置
    private LatLng mStartLocation;
    private LatLng mEndLocation;

    //callback
    private canToPrefer mCallBack;

    public InitNavigateFragment() {
        // Required empty public constructor
    }

    public static InitNavigateFragment newInstance() {
        InitNavigateFragment fragment = new InitNavigateFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        mCallBack = (InitNavigateActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_init_navigate, container, false);

        initRootView(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPoiSearch();

        initLocation();
    }

    //初始化定位
    private void initLocation() {

        mLocation = new LocationClient(getContext());

        //option
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); //打开GPS

        option.setCoorType("gcj02"); //设置坐标类型
        option.setScanSpan(1000);   //定位间隙

        option.setIsNeedAddress(true);

        mLocation.setLocOption(option);

        mLocation.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                if (isFirstLocation) {

                    mLocation.stop();
                } else {

                    mNowLocation = bdLocation;

                    mStartLocation = new LatLng(bdLocation.getLatitude()
                            , bdLocation.getLongitude());

                    mStart.setText(bdLocation.getStreet());

                    isFirstLocation = true;
                }
            }
        });
    }

    //初始化poi
    private void initPoiSearch() {

        //初始化poi
        mPoiSearch = PoiSearch.newInstance();

        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

                mPoiInfo = poiResult.getAllPoi();

                if (mPoiInfo != null) {

                    List<String> list = new ArrayList<>();


                    for (PoiInfo pi : mPoiInfo) {

                        list.add(pi.name);
                    }

                    ArrayAdapter<String> array = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, list);

                    mListView.setAdapter(array);
                }

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                Toast.makeText(getContext(), poiDetailResult.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    //initRootView
    private void initRootView(View rootView) {

        findView(rootView);

        setListener();
    }

    //setListener
    private void setListener() {

        mNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toPlan();

                if (mStartLocation != null && mEndLocation != null) {

                    Intent mIntent = new Intent(getContext(), NavigateActivity.class);

                    Bundle mBundle = new Bundle();

                    mBundle.putDouble(NavigateActivity.sLat, mStartLocation.latitude);
                    mBundle.putDouble(NavigateActivity.sLng, mStartLocation.longitude);
                    mBundle.putDouble(NavigateActivity.eLat, mEndLocation.latitude);
                    mBundle.putDouble(NavigateActivity.eLng, mEndLocation.longitude);

                    mIntent.putExtras(mBundle);

                    startActivity(mIntent);
                }

                backPlan();
            }
        });

        //编写
        mEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String str = s.toString().trim();

                //编写如内容了
                if (!str.equals("")) {

                    PoiNearbySearchOption option = new PoiNearbySearchOption();

                    //检索关键字
                    option.keyword(str);

                    //当前位置
                    option.location(new LatLng(mNowLocation.getLatitude(), mNowLocation.getLongitude()));

                    //分页编号
                    option.pageNum(1);

                    //检索半径
                    option.radius(5000);

                    mPoiSearch.searchNearby(option);
                }
            }
        });

        //刷新按钮
        mRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFirstLocation = false;

                mLocation.start();
            }
        });

        //Item 点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PoiInfo poiInfo = mPoiInfo.get(position);

                mEnd.setText(poiInfo.name);

                mEndLocation = poiInfo.location;
            }
        });

        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallBack.toPrefer();

            }
        });

        //交换
        mSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEndLocation == null) {

                    Toast.makeText(getContext(), "请先选择终点！", Toast.LENGTH_SHORT).show();
                } else if (mStartLocation == null) {

                    Toast.makeText(getContext(), "请先选择起点！", Toast.LENGTH_SHORT).show();
                } else {

                    LatLng temp = mStartLocation;

                    mStartLocation = mEndLocation;

                    mEndLocation = temp;

                    String str = mStart.getText().toString().trim();

                    mStart.setText(mEnd.getText());

                    mEnd.setText(str);
                }
            }
        });
    }

    //findView
    private void findView(View rootView) {

        mStart = (EditText) rootView.findViewById(R.id.fragment_init_navigate_auto_start);
        mEnd = (EditText) rootView.findViewById(R.id.fragment_init_navigate_auto_end);
        mNav = (Button) rootView.findViewById(R.id.fragment_init_navigate_button_nav);
        mRef = (ImageView) rootView.findViewById(R.id.fragment_init_navigate_img_ref);
        mListView = (ListView) rootView.findViewById(R.id.fragment_init_navigate_list_advice);
        mAction = (FloatingActionButton) rootView.findViewById(R.id.fragment_init_navigate_action_settings);
        mSwap = (ImageView) rootView.findViewById(R.id.fragment_init_navigate_img_swap);

        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_init_navigate_progress_bar)
                .findViewById(R.id.progressbar);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_init_navigate_re_layout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //毁掉
        mPoiSearch.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        mLocation.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        mLocation.stop();
    }

    //跳转到设置偏好
    public interface canToPrefer {

        void toPrefer();
    }

    //算路等待
    public void toPlan() {

        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
    }

    public void backPlan() {

        progressBar.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    //算路
    public void routeplanToNavi() {

    }
}
