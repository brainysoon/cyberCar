package cn.coolbhu.snailgo.activities.moregas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.qrcode.QRCodeActivity;
import cn.coolbhu.snailgo.beans.Order;
import cn.coolbhu.snailgo.utils.HelpInitBoomButton;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MyOrdersActivity extends AppCompatActivity implements BoomMenuButton.OnSubButtonClickListener {


    private PtrClassicFrameLayout mPtrFrame;
    private ListView mListView;

    private List<Order> mDataList = new ArrayList<>();

    //Adapter
    private OrderAdapter mAdapter;

    //BoomData
    //BoomData
    private String[] title = new String[]{
            "详细信息",
            "二维码",
            "取消订单"
    };

    private String[] titleGood = new String[]{
            "详细信息",
            "二维码",
    };

    private int[][] colors;

    private int[][] colorsGood;

    //Resource
    private static int[] drawablesResource = new int[]{
            R.drawable.ic_info,
            R.drawable.ic_qrcode,
            R.drawable.ic_delete
    };

    private Drawable[] drawablesGood;

    private Drawable[] drawables;

    public static Integer posi;
    public static Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        initToolbar();

        initBoom();

        findView();

        initList();

        initPtr();

        setListener();
    }

    private void initBoom() {

        colors = new int[3][2];
        colorsGood=new int[2][2];

        //随机数
        Random mRandom = new Random();

        for (int i = 0; i < 3; i++) {

            int r = Math.abs(mRandom.nextInt() % HelpInitBoomButton.Colors.length);

            colors[i][1] = Color.parseColor(HelpInitBoomButton.Colors[r]);

            colors[i][0] = Util.getInstance().getPressedColor(colors[i][1]);

            if (i<2){

                colorsGood[i][1] = Color.parseColor(HelpInitBoomButton.Colors[r]);

                colorsGood[i][0] = Util.getInstance().getPressedColor(colors[i][1]);
            }
        }

        //加载图片资源
        drawables = new Drawable[3];
        drawablesGood=new Drawable[2];

        for (int i = 0; i < 3; i++) {

            drawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

            if (i<2){

                drawablesGood[i]=ContextCompat.getDrawable(this, drawablesResource[i]);
            }
        }
    }

    //setListener
    private void setListener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                toQRCode(position);
            }
        });
    }

    //toQRCode
    private void toQRCode(int position) {

        Log.e("position>>>", position + "");

        //倒转
        position = mDataList.size() - position - 1;

        Order order = mDataList.get(position);

        Intent mIntent = new Intent(MyOrdersActivity.this, QRCodeActivity.class);

        Bundle mBundle = new Bundle();

        mBundle.putInt(QRCodeActivity.Action, 0);
        mBundle.putString("Order_ID", order.getOrder_ID());
        mBundle.putString("User_Tel", order.getUser_Tel());
        mBundle.putDouble("Order_GasPrice", order.getOrder_GasPrice());
        mBundle.putDouble("Order_GasNum", order.getOrder_GasNum());
        mBundle.putString("Car_Num",order.getCar_Num());

        mIntent.putExtras(mBundle);

        startActivity(mIntent);
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
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //可以设置自动刷新
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                mPtrFrame.autoRefresh();
            }
        }, 100);
    }

    //findView
    private void findView() {

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.activity_my_orders_ptr);
        mListView = (ListView) findViewById(R.id.activity_my_orders_list);
    }

    //initToolbar
    private void initToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {

            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //开始刷新
    private void beginToRefresh() {

        BmobQuery<Order> query = new BmobQuery<>("Order");

        if (MyApplication.isLoginSucceed) {

            String tel = MyApplication.mUser.getUser_Tel();

            query.addWhereEqualTo("User_Tel", tel);

            query.findObjects(MyOrdersActivity.this, new FindListener<Order>() {
                @Override
                public void onSuccess(List<Order> list) {

                    mDataList = list;

                    mAdapter.notifyDataSetChanged();

                    mPtrFrame.refreshComplete();

                    Toast.makeText(MyOrdersActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int i, String s) {

                    Toast.makeText(MyOrdersActivity.this, "刷新失败,请稍后重试!", Toast.LENGTH_SHORT).show();

                    mPtrFrame.refreshComplete();
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

        private void initConvertView(View convertView, final int position) {

            TextView mOrder_ID = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_id);
            TextView mOrder_AllPrice = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_all);
            TextView mOrder_Status = (TextView) convertView.findViewById(R.id.activity_my_orders_textview_status);
            TextView mStation_Name = (TextView) convertView.findViewById(R.id.station_name);
            TextView mCarNum=(TextView)convertView.findViewById(R.id.car_num);
            final BoomMenuButton menuButton = (BoomMenuButton) convertView.findViewById(R.id.activity_my_orders_item_boom_ham);

            Order order = mDataList.get(mDataList.size() - position - 1);

            final boolean isPay=order.getOrder_Status()>0;

            menuButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menuButton.init(
                            isPay?drawablesGood:drawables, // The drawables of images of sub buttons. Can not be null.
                            isPay?titleGood:title,     // The texts of sub buttons, ok to be null.
                            isPay?colorsGood:colors,          // The colors of sub buttons, including pressed-state and normal-state.
                            ButtonType.HAM,        // The button type.
                            BoomType.PARABOLA,        // The boom type.
                            isPay?PlaceType.HAM_2_1:PlaceType.HAM_3_1,     // The place type.
                            null,                     // Ease type to move the sub buttons when showing.
                            null,                     // Ease type to scale the sub buttons when showing.
                            null,                     // Ease type to rotate the sub buttons when showing.
                            null,                     // Ease type to move the sub buttons when dismissing.
                            null,                     // Ease type to scale the sub buttons when dismissing.
                            null,                     // Ease type to rotate the sub buttons when dismissing.
                            null                      // Rotation degree.
                    );

                    menuButton.setSubButtonShadowOffset(
                            Util.getInstance().dp2px(2), Util.getInstance().dp2px(2));
                }
            }, 1);

            menuButton.setTag(new Integer(position));

            menuButton.setOnSubButtonClickListener(MyOrdersActivity.this);
            menuButton.setOnClickListener(new BoomMenuButton.OnClickListener() {
                @Override
                public void onClick() {

                    Integer i = (Integer) menuButton.getTag();
                    posi = mDataList.size() - i - 1;

                    mOrder = mDataList.get(posi);

                    Log.e("i++", menuButton.getTag().toString());
                }
            });

            mCarNum.setText(order.getCar_Num());

            mOrder_ID.setText(order.getOrder_ID());
            mStation_Name.setText(order.getOrder_Station());

            Float price = order.getOrder_GasPrice();
            Float num = order.getOrder_GasNum();

            mOrder_AllPrice.setText(price * num + "");
            mOrder_Status.setText(getStatus(order.getOrder_Status()));
        }
    }

    @Override
    public void onClick(int buttonIndex) {

        switch (buttonIndex) {

            case 0:

                toOrderDetail();
                break;

            case 1:

                toQRCode(posi);
                break;

            case 2:

                deleteOrder(posi);
                break;
        }
    }

    //订单详细信息
    private void toOrderDetail() {

        Intent mIntent = new Intent(MyOrdersActivity.this, OrderDetailActivity.class);

        startActivity(mIntent);
    }

    //删除订单
    private void deleteOrder(final int posi) {

        Order order = mDataList.get(posi);

        if (order.getOrder_Status() > 4) {

            Toast.makeText(MyOrdersActivity.this, "已完成的订单无法删除！", Toast.LENGTH_SHORT).show();

            return;
        }

        order.setTableName("Order");

        order.delete(MyOrdersActivity.this, new DeleteListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(MyOrdersActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                mDataList.remove(posi);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, String s) {

                Toast.makeText(MyOrdersActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //订单状态
    public static String getStatus(int i) {

        switch (i) {

            case 1:

                return "已支付";

            case 0:

                return "支付失败";

            case -1:

                return "未支付";
        }

        return "未支付";
    }
}
