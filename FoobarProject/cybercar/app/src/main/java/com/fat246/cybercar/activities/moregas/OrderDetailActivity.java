package com.fat246.cybercar.activities.moregas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.MyOrdersActivity;
import com.fat246.cybercar.beans.Order;

public class OrderDetailActivity extends AppCompatActivity {


    private TextView mOrder_ID;
    private TextView mOrder_All;
    private TextView mOrder_Status;
    private TextView mOrder_Type;
    private TextView mOrder_Prices;
    private TextView mOrder_Time;
    private TextView mOrder_Num;
    private TextView mOrder_Station;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initToolbar();

        findView();

        initView();
    }

    //initView
    private void initView() {

        if (MyOrdersActivity.mOrder != null) {

            Order order = MyOrdersActivity.mOrder;

            mOrder_ID.setText(order.getOrder_ID());
            mOrder_All.setText((order.getOrder_GasNum() * order.getOrder_GasPrice()) + "");
            mOrder_Status.setText(MyOrdersActivity.getStatus(order.getOrder_Status()));
            mOrder_Type.setText(order.getOrder_GasClass());
            mOrder_Prices.setText(order.getOrder_GasPrice() + "");
            mOrder_Time.setText(order.getOrder_Time().getDate());
            mOrder_Num.setText(order.getOrder_GasNum() + "");
            mOrder_Station.setText(order.getOrder_Station());
        } else {

            Toast.makeText(OrderDetailActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
        }
    }

    //findView
    private void findView() {

        mOrder_ID = (TextView) findViewById(R.id.activity_orders_detail_id);
        mOrder_All = (TextView) findViewById(R.id.activity_orders_detail_all);
        mOrder_Status = (TextView) findViewById(R.id.activity_orders_detail_status);
        mOrder_Type = (TextView) findViewById(R.id.activity_orders_detail_type);
        mOrder_Prices = (TextView) findViewById(R.id.activity_orders_detail_prices);
        mOrder_Time = (TextView) findViewById(R.id.activity_orders_detail_time);
        mOrder_Num = (TextView) findViewById(R.id.activity_orders_detail_num);
        mOrder_Station = (TextView) findViewById(R.id.activity_orders_detail_station);

    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_orders_detail_bar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("订单详细信息");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OrderDetailActivity.this.finish();
                }
            });
        }
    }
}
