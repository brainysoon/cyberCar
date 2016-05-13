package com.fat246.cybercar.activities.moregas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.GasStationInfo;
import com.fat246.cybercar.utils.PostUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BookGasActivity extends AppCompatActivity implements PostUtils.handBookGasPost {

    //GasStation
    private GasStationInfo mGasStation;

    //View
    private TextView mTitile;
    private Spinner mType;
    private TextView mPrice;
    private EditText mNum;
    private TextView mTime;
    private Button mSubmit;

    //ArrayAdapter
    private ArrayAdapter<String> mAdapter;

    //Map
    private Map<String, String> mPriceMap;
    private String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_gas);

        //toolbar
        initToolbar();

        //gas
        initGasStation();

        //initview
        initView();

    }

    //初始化View
    private void initView() {

        findView();

        setView();

        setListener();
    }

    //设置监听器
    private void setListener() {

        //提交
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    JSONObject params = new JSONObject();

                    params.put(GasStationInfo.GAS_STATION_NAME, mGasStation.getGas_station_name());

                    String key = mType.getSelectedItem().toString();

                    String value = mPriceMap.get(key);

                    params.put(key, value);

                    params.put("num", mNum.getText().toString());

                    params.put("time", mTime.getText().toString());

//                    params.put(UserInfo.User_Name, MyApplication.mUserInfo.getUserName());

                    //发送请求
                    PostUtils.sendBookGasPost(params, BookGasActivity.this);

                } catch (JSONException ex) {

                    ex.printStackTrace();
                }
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(BookGasActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mTime.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });

        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String value = mPriceMap.get(items[position]);

                mPrice.setText(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //setVeiw
    private void setView() {

        mTitile.setText(mGasStation.getGas_station_name());

        mType.setAdapter(initAdapter());

        String str = mPriceMap.get(mType.getSelectedItem().toString());
        mPrice.setText(str);

        //时间
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        mTime.setText(mYear + "-" + mMonth + "-" + mDay);
    }

    //初始化 Spinner 的Adapter
    private ArrayAdapter<String> initAdapter() {

        String str = mGasStation.getGas_station_gastprice();

        mPriceMap = new HashMap<>();

        try {

            JSONObject jsonObject = new JSONObject(str);

            Iterator<String> it = jsonObject.keys();

            while (it.hasNext()) {

                String key = it.next();
                String value = jsonObject.getString(key);

                mPriceMap.put(key, value);
            }
        } catch (JSONException ex) {

            ex.printStackTrace();
        }

        //String []
        items = new String[mPriceMap.size()];

        int i = 0;
        for (Map.Entry<String, String> entry : mPriceMap.entrySet()) {

            items[i++] = entry.getKey();
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);

        return mAdapter;
    }


    //找到 View
    private void findView() {

        mTitile = (TextView) findViewById(R.id.activity_book_gas_textveiw_title);
        mType = (Spinner) findViewById(R.id.activity_book_gas_spinner_type);
        mPrice = (TextView) findViewById(R.id.activity_book_gas_textview_price);
        mNum = (EditText) findViewById(R.id.activity_book_gas_edittext_num);
        mTime = (TextView) findViewById(R.id.activity_book_gas_textview_time);
        mSubmit = (Button) findViewById(R.id.activity_book_gas_button_submit);
    }

    //初始化GasStation
    private void initGasStation() {

        Intent mIntent = getIntent();

        String json = mIntent.getStringExtra(GasStationInfo.GAS_STATION_JSON_STRING);

        if (json != null) {

            try {

                JSONObject mJson = new JSONObject(json);

                mGasStation = new GasStationInfo(mJson);

            } catch (JSONException ex) {

                ex.printStackTrace();
            }
        } else {

            mGasStation = null;
        }
    }

    //toolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_book_gas_toolbar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BookGasActivity.this.finish();
                }
            });
        }

    }

    //实现提交订单的回掉接口
    @Override
    public void handBookGasPostResult(JSONObject jsonObject) {

    }

    @Override
    public void handBookGasPostError(VolleyError volleyError) {

    }
}
