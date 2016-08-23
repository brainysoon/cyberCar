package com.fat246.servicecar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import cn.bmob.v3.listener.UpdateListener;

public class ServiceMileageActivity extends AppCompatActivity {

    public static String Status_Mileage = "1";
    public static String Status_Gas = "2";
    public static String Status_Engine = "3";
    public static String Status_Speed = "4";
    public static String Status_Light = "5";

    private ListView mListView;
    private Button mStart;

    private List<Msg> mDataList = new ArrayList<>();

    private MileageAdapter mAdapter;

    private ProgressDialog progDialog;

    boolean flag = true;

    android.os.Handler mHandler = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_mileage);

        initListView();

        mStart = (Button) findViewById(R.id.start);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    initData();

                showProgressDialog();
            }
        });

    }

    //显示进度条
    private void showProgressDialog() {

        try {

            if (progDialog == null)
                progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(false);
            progDialog.setMessage("正在推送维护信息:\n" + "....");
            progDialog.show();
        }catch (Exception ex){

            ex.printStackTrace();

        }
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

                Double mlieage = mCar.getCar_Mileage();

                int maxMil = mlieage.intValue();



                Double nowMil = maxMil - mCar.getMileage_Times()* 15000;
                if (nowMil > 15000) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Mileage, mCar.getCar_Num());

                    mMsgs.add(msg);
                }

                //油量
                if (mCar.getCar_Gas() < 20) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Gas, mCar.getCar_Num());

                    mMsgs.add(msg);
                }

                //发动机
                if (mCar.getCar_EngineStatus() <0) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Engine, mCar.getCar_Num());

                    mMsgs.add(msg);
                }

                //变速器
                if (mCar.getCar_SpeedStatus() <0) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Speed, mCar.getCar_Num());

                    mMsgs.add(msg);
                }

                //车灯
                if (mCar.getCar_LightStatus() < 0) {

                    Msg msg = new Msg(mCar.getUser_Tel(), Status_Light, mCar.getCar_Num());

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
                bmobPush.pushMessage(msg.getMsg_Content() + ":" + msg.getCar_Num());

                Log.e("msg>>" + i, msg.getUser_Tel() + ":" + msg.getMsg_Content());

                mDataList.add(msg);
            }

            mAdapter.notifyDataSetChanged();
            progDialog.dismiss();
        }
    }

}
