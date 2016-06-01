package com.fat246.servicecar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fat246.servicecar.beans.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ServiceMileageActivity extends AppCompatActivity {

    private ListView mListView;

    private List<Car> mDataList = new ArrayList<>();

    private MileageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_mileage);

        initListView();

        initData();

    }

    //initData
    private void initData() {

        BmobQuery<Car> query = new BmobQuery<>("Car");

        query.findObjects(this, new FindListener<Car>() {
            @Override
            public void onSuccess(List<Car> list) {

                mDataList = list;

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.e("i>>>" + i, s);
            }
        });
    }

    //initListView
    private void initListView() {

        mListView = (ListView) findViewById(R.id.activity_service_mileage_list);

        mAdapter = new MileageAdapter();

        mListView.setAdapter(mAdapter);
    }

    //apapter
    class MileageAdapter extends BaseAdapter {

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

            TextView textView = new TextView(ServiceMileageActivity.this);

            Car mCar = mDataList.get(position);

            textView.setText(mCar.getUser_Tel() + ":" + mCar.getCar_Num());

            return textView;
        }
    }

}
