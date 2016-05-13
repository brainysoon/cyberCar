package com.fat246.cybercar.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.OrderInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersFragment extends Fragment implements OrderInfo.handMyOrdersPost {

    //Orders Map
    private List<OrderInfo> mOrders = new ArrayList<>();

    //List
    private ListView mOrdersList;

    private OrdersListAdapter mAdapter;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    public static MyOrdersFragment newInstance() {
        MyOrdersFragment fragment = new MyOrdersFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);

        initRootView(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //请求数据
        sendPost();
    }

    //sendPost
    private void sendPost() {
        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put(OrderInfo.USER_NAME, MyApplication.mUserInfo.getUserName());

            //send
            OrderInfo.sendMyOrdersPost(jsonObject, this);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    //initRootView
    private void initRootView(View rootView) {

        //findView
        findView(rootView);

        setView();
    }

    //setView
    private void setView() {

        mAdapter=new OrdersListAdapter();

        mOrdersList.setAdapter(mAdapter);
    }

    //findView
    private void findView(View rootView) {

        mOrdersList = (ListView) rootView.findViewById(R.id.fragment_my_orders_listview_order);

    }

    //回掉
    @Override
    public void handMyOrdersPostResult(JSONObject jsonObject) {
        try {

            mOrders = new ArrayList<>();

            JSONArray jsonArray = jsonObject.getJSONArray("");


            for (int i = 0; i < jsonArray.length(); i++) {

                OrderInfo orderInfo = new OrderInfo(jsonArray.getJSONObject(i));

                mOrders.add(orderInfo);
            }

            mAdapter.notifyDataSetChanged();
        } catch (JSONException ex) {

            ex.printStackTrace();
        }
    }

    @Override
    public void handMyOrdersPostError(VolleyError volleyError) {

    }

    public class OrdersListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mOrders.size();
        }

        @Override
        public Object getItem(int position) {
            return mOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());

            convertView = inflater.inflate(R.layout.fragment_my_order_item, parent, false);

            initConvertView(convertView, position);

            return convertView;
        }

        private void initConvertView(View rootView, int positon) {

            TextView mID = (TextView) rootView.findViewById(R.id.fragment_my_orders_textview_id);
            TextView mType = (TextView) rootView.findViewById(R.id.fragment_my_orders_textview_type);
            TextView mName = (TextView) rootView.findViewById(R.id.fragment_my_orders_textview_name);
            TextView mNum = (TextView) rootView.findViewById(R.id.fragment_my_orders_textview_num);
            TextView mPhone = (TextView) rootView.findViewById(R.id.fragment_my_orders_textview_phone);
            TextView mStatus = (TextView) rootView.findViewById(R.id.fragment_my_orders_textview_status);

            OrderInfo orderInfo = mOrders.get(positon);

            mID.setText(orderInfo.getOrderID());
            mName.setText(orderInfo.getUserName());
            mPhone.setText(orderInfo.getUserPhone());
            mType.setText(orderInfo.getOrderType());
            mStatus.setText(orderInfo.getOrderStatus());
            mNum.setText(orderInfo.getOrderNum());

        }
    }
}
