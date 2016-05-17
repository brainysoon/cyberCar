package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.CarInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCarsFragment extends Fragment implements CarInfo.canHandCarInfosPostResult {

    //ListView
    private ListView mCarsList;

    //List
    private List<CarInfo> mCars = new ArrayList<>();

    //Adapter
    private CarsListAdapter mAdapter;

    //AddButton
    private FloatingActionButton mAdd;

    public MyCarsFragment() {
        // Required empty public constructor
    }

    public static MyCarsFragment newInstance() {
        MyCarsFragment fragment = new MyCarsFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_my_cars, container, false);

        initRootView(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //发送请求数据
        sendPost();
    }

    //sendPost
    private void sendPost() {

//        try {
//
//            JSONObject jsonObject = new JSONObject();
//
////            jsonObject.put(UserInfo.User_Name, MyApplication.mUserInfo.getUserName());
//
//            CarInfo.sendCarInfoPost(jsonObject, this);
//
//        } catch (JSONException ex) {
//
//            ex.printStackTrace();
//        }
    }

    //initRootView
    private void initRootView(View rootView) {

        findView(rootView);

        setView();

        setListener();
    }

    //设置监听器
    private void setListener() {

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //setView
    private void setView() {

        mAdapter = new CarsListAdapter();

        mCarsList.setAdapter(mAdapter);
    }

    //findView
    private void findView(View rootView) {

        mCarsList = (ListView) rootView.findViewById(R.id.fragment_my_cars_listview_cars);

        mAdd = (FloatingActionButton) rootView.findViewById(R.id.fragment_my_cars_floating_add);
    }


    //回掉
    @Override
    public void handCarInfosPostError(VolleyError volleyError) {

        Log.e("ex", volleyError.toString());
    }

    @Override
    public void handCarInfosPostResutl(JSONObject jsonObject) {

        try {

            mCars = new ArrayList<>();

            JSONArray jsonArray = jsonObject.getJSONArray("");

            for (int i = 0; i < jsonArray.length(); i++) {

                CarInfo carInfo = new CarInfo(jsonArray.getJSONObject(i));

                mCars.add(carInfo);
            }

            mAdapter.notifyDataSetChanged();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public class CarsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCars.size();
        }

        @Override
        public Object getItem(int position) {
            return mCars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            convertView = layoutInflater.inflate(R.layout.fragment_my_cars_item, parent, false);

            initConvertView(convertView, position);

            return convertView;
        }

        private void initConvertView(View convertView, int position) {

            TextView mID = (TextView) convertView.findViewById(R.id.fragment_my_cars_textview_id);
            TextView mBrand = (TextView) convertView.findViewById(R.id.fragment_my_cars_textview_brand);
            TextView mFramType = (TextView) convertView.findViewById(R.id.fragment_my_cars_textview_framenum);
            TextView mType = (TextView) convertView.findViewById(R.id.fragment_my_cars_textview_type);

            //data
            CarInfo carInfo = mCars.get(position);

            mID.setText(carInfo.getCarNum());
            mBrand.setText(carInfo.getCarGrand());
            mFramType.setText(carInfo.getCarBodyType());
            mType.setText(carInfo.getCarType());

        }
    }
}
