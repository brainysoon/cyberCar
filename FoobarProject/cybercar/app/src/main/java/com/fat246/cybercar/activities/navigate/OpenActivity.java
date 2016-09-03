package com.fat246.cybercar.activities.navigate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.fat246.cybercar.R;
import com.fat246.cybercar.application.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenActivity extends AppCompatActivity {

    public static final String ROUTE_PLAN_NODE = "ROTE_PLAN_NODE";

    public static final String sLat = "sLat";
    public static final String sLng = "sLng";
    public static final String eLat = "eLat";
    public static final String eLng = "eLng";

    private boolean mlsEngineInitSuccees = false;

    //外部存储的位置
    private String mSDCardPath = null;

    //View
    private Button btn;
    private RadioButton mRadioNewMen;
    private RadioButton mRadioProfessor;
    private RadioButton mRadioNone;
//    private CheckBox mCheckBoxEye;
//    private CheckBox mCheckBoxSpeed;
//    private CheckBox mCheckBoxSafe;
//    private CheckBox mCheckBoxCondition;
//    private CheckBox mCheckBoxStraght;
    private Switch mSwitchCondition;
    private Switch mSwitchFullCondition;
    private Switch mSwitchDayNight;
    private Switch mSwitchPower;

    private ProgressBar progressBar;
    private LinearLayout linearLayout;


    //ttsHandler
    private Handler ttsHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int type = msg.what;

            switch (type) {

                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {

                    showToastMsg("开始语音播报");
                    break;
                }

                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {

                    showToastMsg("语音播报结束");
                    break;
                }

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        initToolbar();

        BNOuterLogUtil.setLogSwitcher(true);

        initView();

        if (initSDCardDirs()) {

            initNavi();
        }
    }

    //initView
    private void initView() {

        btn = (Button) findViewById(R.id.activity_open_btn);
        mRadioNewMen = (RadioButton) findViewById(R.id.activity_open_radio_newmen);
        mRadioProfessor = (RadioButton) findViewById(R.id.activity_open_radio_professor);
        mRadioNone = (RadioButton) findViewById(R.id.activity_open_radio_none);
//        mCheckBoxEye = (CheckBox) findViewById(R.id.activity_open_checkbox_eye);
//        mCheckBoxSpeed = (CheckBox) findViewById(R.id.activity_open_checkbox_speed);
//        mCheckBoxSafe = (CheckBox) findViewById(R.id.activity_open_checkbox_safe);
//        mCheckBoxCondition = (CheckBox) findViewById(R.id.activity_open_checkbox_condition);
//        mCheckBoxStraght = (CheckBox) findViewById(R.id.activity_open_checkbox_straght);
        mSwitchCondition = (Switch) findViewById(R.id.activity_open_switch_condition);
        mSwitchFullCondition = (Switch) findViewById(R.id.activity_open_switch_full_condition);
        mSwitchDayNight = (Switch) findViewById(R.id.activity_open_switch_day_night);
        mSwitchPower = (Switch) findViewById(R.id.activity_open_switch_power);


        progressBar = (ProgressBar) findViewById(R.id.activity_open_pro_bar).
                findViewById(R.id.progressbar);
        linearLayout = (LinearLayout) findViewById(R.id.activity_open_linear);


        //按钮
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BaiduNaviManager.isNaviInited()) {

//                    BNRoutePlanNode sNode=new BNRoutePlanNode(Double.parseDouble(sNodeStart.getText().toString().trim()),
//                            Double.parseDouble(sNodeEnd.getText().toString().trim()),
//                            "",null, BNRoutePlanNode.CoordinateType.BD09LL);
//                    BNRoutePlanNode eNode=new BNRoutePlanNode(Double.parseDouble(eNodeStart.getText().toString().trim()),
//                            Double.parseDouble(eNodeEnd.getText().toString().trim()),
//                            "",null, BNRoutePlanNode.CoordinateType.BD09LL);

                    Intent mIntent = getIntent();

                    Bundle mBundle = mIntent.getExtras();

                    BNRoutePlanNode sNode = new BNRoutePlanNode(mBundle.getDouble(OpenActivity.sLng),
                            mBundle.getDouble(OpenActivity.sLat),
                            "", null, BNRoutePlanNode.CoordinateType.BD09LL);
                    BNRoutePlanNode eNode = new BNRoutePlanNode(mBundle.getDouble(OpenActivity.eLng),
                            mBundle.getDouble(OpenActivity.eLat),
                            "", null, BNRoutePlanNode.CoordinateType.BD09LL);

                    //重新设置
                    initSetting();

                    showBar();

                    //开始算路
                    routePlanToNavi(sNode, eNode);
                }
            }
        });
    }

    //initToolbar
    private void initToolbar() {

        View rootView = findViewById(R.id.activity_open_bar);

        if (rootView != null) {

            Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

            toolbar.setTitle("设置导航");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OpenActivity.this.finish();
                }
            });
        }
    }


    //初始化外部存储
    private boolean initSDCardDirs() {

        mSDCardPath = getSDCardPath();

        if (mSDCardPath == null) {

            return false;
        }

        File f = new File(mSDCardPath, MyApplication.APP_NAME);

        if (!f.exists()) {

            try {

                f.mkdir();
            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    //获得外部存储的位置
    private String getSDCardPath() {

        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {

            return Environment.getExternalStorageDirectory().toString();
        }

        return null;
    }

    //初始化Navi
    private void initNavi() {

        //tts回调
        BNOuterTTSPlayerCallback ttsCallback = null;

        //init
        BaiduNaviManager.getInstance().init(this, mSDCardPath, MyApplication.APP_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int i, String s) {

                Log.e("AuthResult", i + ">>>>>" + s);
            }

            @Override
            public void initStart() {

                Log.e("Start>>>", "initStart");

                showBar();
            }

            @Override
            public void initSuccess() {

                Log.e("succeed", "succeed");

                //Enable  导航按钮
                hideBar();
            }

            @Override
            public void initFailed() {

                Log.e("Failed", "initFailed");
                hideBar();

                OpenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(OpenActivity.this, "网络错误，请稍候再试！", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, null, null, null);
    }

    //showToast
    private void showToastMsg(final String msg) {

        OpenActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OpenActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //初始化设置
    private void initSetting() {


        //设置语音播报模式
        int voice;

        //新手模式 老手模式 静音模式
        if (mRadioNewMen.isChecked()) {

            voice = BNaviSettingManager.VoiceMode.Veteran;
        } else if (mRadioProfessor.isChecked()) {

            voice = BNaviSettingManager.VoiceMode.Quite;
        } else {

            voice = BNaviSettingManager.VoiceMode.Novice;
        }

        //设置播报模式
        BNaviSettingManager.setVoiceMode(voice);

        //语音播报内容

        //设置时时路况 开 关
        if (mSwitchCondition.isChecked()) {

            BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        } else {

            BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_OFF);
        }

        //设置全程路况显示
        if (mSwitchFullCondition.isChecked()) {

            BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.
                    PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        } else {

            BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.
                    PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_OFF);
        }

        //是否为夜晚模式
        if (mSwitchDayNight.isChecked()) {

            //设置为夜晚模式
            BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_NIGHT);
        } else {

            //设置为白天模式
            BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        }

        //是否设置为省电模式
        if (mSwitchPower.isChecked()) {

            //设置省电模式
            BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.ENABLE_MODE);
        } else {

            //设置非省电模式
            BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        }
    }

    //rote
    private void routePlanToNavi(BNRoutePlanNode sNode, BNRoutePlanNode eNode) {

        //
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<>();

            list.add(sNode);
            list.add(eNode);

            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new MyRoutePlanListener(sNode));
        }
    }

    public class MyRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode sNode = null;

        public MyRoutePlanListener(BNRoutePlanNode sNode) {

            this.sNode = sNode;
        }

        @Override
        public void onJumpToNavigator() {

            Intent mIntent = new Intent(OpenActivity.this, GuideActivity.class);

            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) sNode);

            mIntent.putExtras(mBundle);

            startActivity(mIntent);

            hideBar();
        }

        @Override
        public void onRoutePlanFailed() {
            Log.e("RoutePlanFailed", "RoutePlanFailed");

            hideBar();

            OpenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(OpenActivity.this, "网络错误，请稍候再试！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //show bar
    private void showBar() {

        this.progressBar.setVisibility(View.VISIBLE);
        this.linearLayout.setVisibility(View.INVISIBLE);
    }

    //hide bar
    private void hideBar() {

        this.progressBar.setVisibility(View.INVISIBLE);
        this.linearLayout.setVisibility(View.VISIBLE);
    }
}
