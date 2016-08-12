package cn.coolbhu.snailgo.activities.moregas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.GasStationInfo;

public class BookGasActivity extends AppCompatActivity {

    //GasStation
    private GasStationInfo mGasStation;

    //View
    private TextView mTitile;
    private Spinner mType;
    private TextView mPrice;
    private EditText mNum;
    private TextView mTime;
    private Button mSubmit;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

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

    //confirm num
    private float confirmNum() {

        try {

            float i = Float.parseFloat(mNum.getText().toString().trim());

            if (i < 0) {

                Toast.makeText(this, "请正确输入加油数量！", Toast.LENGTH_SHORT).show();

            }

            return i;

        } catch (Exception ex) {

            Toast.makeText(this, "请正确输入加油数量！", Toast.LENGTH_SHORT).show();
        }

        return 0;
    }

    //设置监听器
    private void setListener() {

        //提交
//        mSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showBar();
//
//                try {
//
//                    Float i = confirmNum();
//
//                    if (i > 0) {
//
//                        //判断是否登陆
//                        if (MyApplication.isLoginSucceed) {
//
//                            Date date = new Date();
//
//                            final String id = new SimpleDateFormat("yyyyMMddHHmmss").format(date) + ((int) (Math.random() * 10000));
//
//                            String Order_Station = mGasStation.getGas_station_name();
//
//                            String Order_GasClass = mType.getSelectedItem().toString();
//
//                            String Order_GasPrice = mPrice.getText().toString().trim();
//
//                            Float price = Float.parseFloat(Order_GasPrice);
//
//                            final Order order = new Order(MyApplication.mUser.getUser_Tel(), id,
//                                    Order_Station, 1, new BmobDate(date), Order_GasClass, price, i);
//
//                            order.save(BookGasActivity.this, new SaveListener() {
//                                @Override
//                                public void onSuccess() {
//
//                                    Toast.makeText(BookGasActivity.this, "成功生成订单！", Toast.LENGTH_SHORT).show();
//
//                                    Intent mIntent = new Intent(BookGasActivity.this, QRCodeActivity.class);
//
//                                    Bundle mBundle = new Bundle();
//
//                                    mBundle.putString("Order_ID", order.getOrder_ID());
//                                    mBundle.putString("User_Tel", order.getUser_Tel());
//                                    mBundle.putDouble("Order_GasPrice", order.getOrder_GasPrice());
//                                    mBundle.putDouble("Order_GasNum", order.getOrder_GasNum());
//
//                                    mIntent.putExtras(mBundle);
//
//                                    startActivity(mIntent);
//
//                                    BookGasActivity.this.finish();
//                                }
//
//                                @Override
//                                public void onFailure(int i, String s) {
//
//                                    Toast.makeText(BookGasActivity.this, "添加失败，请稍后再试！", Toast.LENGTH_SHORT).show();
//                                    hideBar();
//                                }
//                            });
//
//                        } else {
//
//                            Toast.makeText(BookGasActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
//                            hideBar();
//                        }
//                    }
//                } catch (Exception e) {
//
//                    hideBar();
//                }
//            }
//        });

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
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        linearLayout = (LinearLayout) findViewById(R.id.activity_book_gas_linear);
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


        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    //forword
    private void showBar() {

        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
    }

    //back
    private void hideBar() {

        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
    }

}
