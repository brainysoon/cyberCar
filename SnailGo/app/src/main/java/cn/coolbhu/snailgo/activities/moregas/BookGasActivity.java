package cn.coolbhu.snailgo.activities.moregas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.LoginActivity;
import cn.coolbhu.snailgo.activities.qrcode.QRCodeActivity;
import cn.coolbhu.snailgo.activities.unionpay.BaseActivity;
import cn.coolbhu.snailgo.beans.Car;
import cn.coolbhu.snailgo.beans.GasStationInfo;
import cn.coolbhu.snailgo.beans.Order;
import cn.coolbhu.snailgo.utils.SsX509TrustManager;

public class BookGasActivity extends BaseActivity implements View.OnClickListener
        , DialogInterface.OnClickListener, Response.Listener<String>, Response.ErrorListener {

//    public static final String UNION_PAY_SHOP = "777290058136491";
//    public static final String UNION_REQUEST_CHARSET = "UTF-8";
//    public static final String BACK_END_URL = "http://www.coolbhu.cn";
//    public static final String FONET_END_URL = "";
//    public static final String ORDER_DESCRIPTION = "预约加油";

    //商户参数请求地址
    public static final String SHOP_PARAM_REQUEST_URL = "http://101.231.204.84:8091/sim/getacptn";

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

    //车牌号
    private Spinner mCarNum;

    //ArrayAdapter
    private ArrayAdapter<String> mAdapter;

    //Map
    private Map<String, String> mPriceMap;
    private String[] items;

    //生成的订单
    private Order mOrder = null;

    private ProgressDialog mLoadingDialog = null; // 进度是否是不确定的，这只和创建进度条有关

    //加载车牌号进度条
    private ProgressDialog progDialog;

    //车牌号Adapter
    private ArrayAdapter<String> mCarsAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        initMyCars();

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
        mSubmit.setOnClickListener(this);

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

                        mTime.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
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

    //初始化我的车辆的
    private void initMyCars() {

        showProgressDialog();

        BmobQuery<Car> query = new BmobQuery<>("Car");

        if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

            query.addWhereEqualTo("User_Tel", MyApplication.mUser.getUser_Tel());
        }

        query.findObjects(BookGasActivity.this, new FindListener<Car>() {
            @Override
            public void onSuccess(List<Car> list) {

                if (list.size() > 0) {

                    List<String> numList = new ArrayList<String>();

                    for (int i = 0; i < list.size(); i++) {

                        numList.add(list.get(i).getCar_Num());
                    }

                    mCarsAdapter = new ArrayAdapter<>(BookGasActivity.this, android.R.layout.simple_list_item_1, numList);

                    mCarNum.setAdapter(mCarsAdapter);

                    progDialog.dismiss();
                } else {

                    progDialog.dismiss();

                    android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(BookGasActivity.this);

                    builder.setTitle(R.string.notice)
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage("你还没有车，请添加汽车过后再试！")
                            .setPositiveButton("去登陆", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent intent=new Intent(BookGasActivity.this, LoginActivity.class);

                                    BookGasActivity.this.startActivity(intent);

                                    BookGasActivity.this.finish();
                                }
                            });

                    builder.create().show();
                }
            }

            @Override
            public void onError(int i, String s) {

                Toast.makeText(BookGasActivity.this, "加载汽车失败，请稍后再试！", Toast.LENGTH_SHORT).show();

                progDialog.dismiss();
            }
        });
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
        mCarNum = (Spinner) findViewById(R.id.car_spinner);
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

    @Override
    public void onClick(View view) {

        showBar();

        try {

            Float i = confirmNum();

            if (i > 0) {

                //判断是否登陆
                if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

                    Date date = new Date();

                    final String id = new SimpleDateFormat("yyyyMMddHHmmss").format(date) + ((int) (Math.random() * 10000));

                    String Order_Station = mGasStation.getGas_station_name();

                    String Order_GasClass = mType.getSelectedItem().toString();

                    String Order_GasPrice = mPrice.getText().toString().trim();

                    Float price = Float.parseFloat(Order_GasPrice);

                    String car_num=mCarNum.getSelectedItem().toString();


                    mOrder = new Order(MyApplication.mUser.getUser_Tel(), id,
                            Order_Station, -1, new BmobDate(date), Order_GasClass, price, i,car_num);

                    mOrder.save(BookGasActivity.this, saveListener);

                } else {

                    Toast.makeText(BookGasActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                    hideBar();
                }
            }
        } catch (Exception e) {

            hideBar();

            Toast.makeText(BookGasActivity.this,"添加失败，请稍后再试！",Toast.LENGTH_SHORT).show();
        }
    }

    //银联支付
    @Override
    public void doStartUnionPayPlugin(Activity activity, String tn, String mode) {

        UPPayAssistEx.startPay(activity, null, null, tn, mode);
    }

    //订单生成成功
    private SaveListener saveListener = new SaveListener() {

        @Override
        public void onSuccess() {

            Toast.makeText(BookGasActivity.this, "成功生成订单！", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(BookGasActivity.this);

            builder.setTitle(R.string.order_succeed)
                    .setIcon(R.drawable.unionpay)
                    .setMessage(R.string.order_alert_message)
                    .setPositiveButton(R.string.order_now_pay, BookGasActivity.this)
                    .setNegativeButton(R.string.order_not_pay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            skipToQRCode();
                        }
                    })
                    .setCancelable(false);

            builder.create().show();
        }

        //订单生成失败
        @Override
        public void onFailure(int i, String s) {

            Toast.makeText(BookGasActivity.this, "添加失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            hideBar();
        }
    };


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        mLoadingDialog = ProgressDialog.show(BookGasActivity.this, // context
                "", // title
                "正在努力的获取tn中,请稍候...", // message
                true);

        hideBar();
        sendOrderRequest();
    }

    //向银联发送订单请求
    private void sendOrderRequest() {

        SsX509TrustManager.allowAllSSL();

        //请求
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SHOP_PARAM_REQUEST_URL,
                this, this);


        stringRequest.setTag(mOrder.getOrder_ID());

        //添加到请求队列里面
        MyApplication.getRequestQueue().add(stringRequest);
    }

//    //获得向银联发送请求的参数
//    private Map<String, String> paperOrderParam() {
//
//        Map<String, String> map = new HashMap<>();
//
//        map.put("version", BuildConfig.VERSION_NAME);// 版本号
//
//        map.put("charset", UNION_REQUEST_CHARSET);// 字符编码
//
//        map.put("transType", mMode);// 交易类型
//
//        map.put("merId", UNION_PAY_SHOP);// 商户代码
//
//        map.put("backEndUrl", BACK_END_URL);// 通知URL
//
//        map.put("frontEndUrl", FONET_END_URL);// 前台通知URL(可选)
//
//        map.put("orderDescription", ORDER_DESCRIPTION + mOrder.getOrder_GasNum() + "升");// 订单描述(可选)
//
//        Calendar calendar = Calendar.getInstance();
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//
//        map.put("orderTime", simpleDateFormat.format(calendar.getTime()));// 交易开始日期时间yyyyMMddHHmmss
//
//        calendar.add(Calendar.MINUTE, 30);
//
//        map.put("orderTimeout", simpleDateFormat.format(calendar.getTime()));// 订单超时时间yyyyMMddHHmmss(可选)
//
//        map.put("orderNumber", mOrder.getOrder_ID());// 订单号(商户根据自己需要生成订单号)
//
//        map.put("orderAmount", mOrder.getOrder_GasPrice() * mOrder.getOrder_GasNum() + "");// 订单金额
//
//        map.put("orderCurrency", "156");// 交易币种(可选)
//
//        map.put("reqReserved", "");// 请求方保留域(可选，用于透传商户信息)
//
//        map.put("merReserved", "");// 商户保留域(可选)
//
//        return map;
//    }

    //跳转到订单二维码
    public void skipToQRCode() {

        Intent mIntent = new Intent(BookGasActivity.this, QRCodeActivity.class);

        Bundle mBundle = new Bundle();

        mBundle.putString("Order_ID", mOrder.getOrder_ID());
        mBundle.putString("User_Tel", mOrder.getUser_Tel());
        mBundle.putDouble("Order_GasPrice", mOrder.getOrder_GasPrice());
        mBundle.putDouble("Order_GasNum", mOrder.getOrder_GasNum());
        mBundle.putString("Car_Num",mOrder.getCar_Num());

        mIntent.putExtras(mBundle);

        startActivity(mIntent);

        BookGasActivity.this.finish();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

        volleyError.printStackTrace();

        mLoadingDialog.dismiss();
    }

    @Override
    public void onResponse(String str) {


        mOrder.Pay_Serial_Number = str;

        mOrder.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {

                mLoadingDialog.dismiss();
                UPPayAssistEx.startPayByJAR(BookGasActivity.this,
                        PayActivity.class, null, null, mOrder.getPay_Serial_Number(), mMode);
            }

            @Override
            public void onFailure(int i, String s) {

                mLoadingDialog.dismiss();
                Toast.makeText(BookGasActivity.this, "网络错误，加载失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
                    boolean ret = verify(dataOrg, sign, mMode);
                    if (ret) {
                        // 验证通过后，显示支付结果
                        msg = "支付成功！";

                        mOrder.Order_Status = 1;
                    } else {
                        // 验证不通过后的处理
                        // 建议通过商户后台查询支付结果
                        msg = "支付失败！";

                        mOrder.Order_Status = -1;
                    }
                } catch (JSONException e) {
                }
            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
                msg = "支付成功！";

                mOrder.Order_Status = 1;
            }
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";

            mOrder.Order_Status = -1;
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";

            mOrder.Order_Status = 0;
        }

        mOrder.update(BookGasActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        // builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //验证

    @Override
    public boolean verify(String msg, String sign64, String mode) {
        return true;
    }

    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(BookGasActivity.this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("加载车辆:\n" + "请稍后。。。。");
        progDialog.show();
    }
}
