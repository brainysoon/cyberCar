package com.fat246.cybercar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.QRCode.QRCodeActivity;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.Order;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MyOrdersActivity extends AppCompatActivity {


    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;

    private List<Order> mDataList = new ArrayList<>();

    //Adapter
    private OrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        initToolbar();

        findView();

        initList();

        initPtr();

        beginToRefresh();

        setListener();
    }

    //setListener
    private void setListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Order order = mDataList.get(position);

                Intent mIntent = new Intent(MyOrdersActivity.this, QRCodeActivity.class);

                Bundle mBundle = new Bundle();

                mBundle.putString("Order_ID", order.getOrder_ID());
                mBundle.putString("User_Tel", order.getUser_Tel());
                mBundle.putDouble("Order_GasPrice", order.getOrder_GasPrice());
                mBundle.putDouble("Order_GasNum", order.getOrder_GasNum());

                mIntent.putExtras(mBundle);

                startActivity(mIntent);
            }
        });
    }

    //initList
    private void initList() {

        mAdapter = new OrderAdapter(MyOrdersActivity.this);

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
                beginToRefresh();

                mPtrFrame.refreshComplete();
            }
        });

        //可以设置自动刷新
//        mPtrFrame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                //自动刷新
//            }
//        },1000);

    }

    //findView
    private void findView() {

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.activity_my_orders_ptr);
        mListView = (ListView) findViewById(R.id.activity_my_orders_list);
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_my_orders_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("我的订单");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MyOrdersActivity.this.finish();
                }
            });
        }
    }

    //开始刷新
    private void beginToRefresh() {

        BmobQuery<Order> query = new BmobQuery<>("Order");

        if (MyApplication.isLoginSucceed) {

            String tel = MyApplication.mUser.getUser_Tel();

            query.addWhereMatches("User_Tel", tel);

            query.findObjects(MyOrdersActivity.this, new FindListener<Order>() {
                @Override
                public void onSuccess(List<Order> list) {

                    mDataList = list;

                    mAdapter.notifyDataSetChanged();

                    Toast.makeText(MyOrdersActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int i, String s) {

                    Toast.makeText(MyOrdersActivity.this, "刷新失败,请稍后重试!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class OrderAdapter extends BaseAdapter {

        private Context context;

        private LayoutInflater layoutInflater;

        public OrderAdapter(Context context) {

            this.context = context;

            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = layoutInflater.inflate(R.layout.activity_my_orders_item, null);

            initConvertView(convertView, position);

            return convertView;
        }

        private void initConvertView(View convertView, int position) {

            TextView mOrder_ID = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_id);
            TextView mOrder_GasType = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_type);
            TextView mOrder_AllPrice = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_all);
            TextView mOrder_GasNum = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_num);
            TextView mOrder_Status = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_status);

            Order order = mDataList.get(mDataList.size() - position - 1);

            mOrder_ID.setText(order.getOrder_ID());
            mOrder_GasType.setText(order.getOrder_GasClass());

            Float price = order.getOrder_GasPrice();
            Float num = order.getOrder_GasNum();

            mOrder_AllPrice.setText(price * num + "");
            mOrder_GasNum.setText(num + "");
            mOrder_Status.setText(order.getOrder_Status() + "");
        }
    }
}
