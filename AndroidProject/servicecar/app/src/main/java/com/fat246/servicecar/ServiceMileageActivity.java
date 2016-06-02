package com.fat246.servicecar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fat246.servicecar.beans.Car;
import com.fat246.servicecar.beans.Msg;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ServiceMileageActivity extends AppCompatActivity {

    public static String Status_Mileage = "该检验一下车子了！";
    public static String Status_Gas = "没有油了，该加油了！";
    public static String Status_Engine = "发动机该检查维修了！";
    public static String Status_Speed = "变速器该检查维修了！";
    public static String Status_Light = "车灯该检查维修了！";

    private ListView mListView;

    private List<Msg> mDataList = new ArrayList<>();

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

                new DataAsync().execute(list);
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

            Msg msg = mDataList.get(position);

            textView.setText(msg.getUser_Tel() + ":" + msg.getMsg_Content());

            return textView;
        }
    }

    class DataAsync extends AsyncTask<List<Car>, Void, List<Msg>> {

        @Override
        protected List<Msg> doInBackground(List<Car>... params) {

            List<Car> mCars = params[0];

            List<Msg> mMsgs = new ArrayList<>();

            for (int i = 0; i < mCars.size(); i++) {

                Car mCar = mCars.get(i);

                //给予维护汽车
                if (mCar.getCar_Mileage() > 15000) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Mileage);

                    mMsgs.add(msg);
                }

                //油量
                if (mCar.getCar_Gas() <10){

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Gas);

                    mMsgs.add(msg);
                }

                //发动机
                if (mCar.getCar_EngineStatus() > 10) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Engine);

                    mMsgs.add(msg);
                }

                //变速器
                if (mCar.getCar_SpeedStatus() > 10) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Speed);

                    mMsgs.add(msg);
                }

                //车灯
                if (mCar.getCar_LightStatus() > 10) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Light);

                    mMsgs.add(msg);
                }
            }

            return mMsgs;
        }

        @Override
        protected void onPostExecute(List<Msg> msgs) {

            Log.e("here", "here");

            BmobPushManager bmobPush = new BmobPushManager(ServiceMileageActivity.this);

            for (int i = 0; i < msgs.size(); i++) {

                Msg msg = msgs.get(i);

                BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();

                query.addWhereEqualTo("uid", msg.getUser_Tel());
                bmobPush.setQuery(query);
                bmobPush.pushMessage(msg.getMsg_Content());

                Log.e("msg>>" + i, msg.getUser_Tel() + ":" + msg.getMsg_Content());

                mDataList.add(msg);
            }

            mAdapter.notifyDataSetChanged();
        }
    }

}
