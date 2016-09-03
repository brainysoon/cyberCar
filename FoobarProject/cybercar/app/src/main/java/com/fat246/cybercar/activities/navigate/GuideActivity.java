package com.fat246.cybercar.activities.navigate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;
import com.fat246.cybercar.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {


    //对于导航模块有两种方式来实现发起导航： 1.使用通用接口来实现 2.使用传统接口来实现
    private boolean isUseCommonInterface = true;

    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;

    //Handler
    public static final String RESET_NODE_LAT = "RESET_NODE_LAT";
    public static final String RESET_NODE_LON = "RESET_NODE_LON";
    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private static final int MSG_RESET_NODE = 3;
    private Handler hd = null;

    //NavigationLister
    private BNRouteGuideManager.OnNavigationListener mOnNavigationListener = new BNRouteGuideManager.OnNavigationListener() {
        @Override
        public void onNaviGuideEnd() {

            finish();
        }

        @Override
        public void notifyOtherAction(int i, int i1, int i2, Object o) {

            Log.e("Action", i + ">>>" + i1 + ">>>" + i2 + ">>>" + o.toString());
        }
    };

    //View
    private View mView = null;

    //Node
    private BNRoutePlanNode mNode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //创建Handler
        createHandler();

        //通用接口
        if (isUseCommonInterface) {

            mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
                    NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
                    BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE,
                    mOnNavigationListener
            );

            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onCreate();

                mView = mBaiduNaviCommonModule.getView();
            }
        } else {

            mView = BNRouteGuideManager.getInstance().onCreate(this, mOnNavigationListener);
        }

        //setView
        if (mView != null) {

            setContentView(mView);
        } else {

            setContentView(R.layout.activity_guide);
        }

        Intent mIntent = getIntent();

        if (mIntent != null) {

            Bundle mBundle = mIntent.getExtras();

            if (mBundle != null) {

                mNode = (BNRoutePlanNode) mBundle.getSerializable(OpenActivity.ROUTE_PLAN_NODE);
            }
        }
    }

    private void addCustomizedLayerItems(){

        List<BNRouteGuideManager.CustomizedLayerItem> mItems=new ArrayList<>();

        BNRouteGuideManager.CustomizedLayerItem item1=null;

        if (mBaiduNaviCommonModule!=null){

            item1=new BNRouteGuideManager.CustomizedLayerItem(mNode.getLongitude(),mNode.getLatitude(),
                    mNode.getCoordinateType(),getResources().getDrawable(R.drawable.csy_ic_launcher),
                    BNRouteGuideManager.CustomizedLayerItem.ALIGN_CENTER);

            mItems.add(item1);

            BNRouteGuideManager.getInstance().setCustomizedLayerItems(mItems);

            BNRouteGuideManager.getInstance().showCustomizedLayer(true);
        }
    }

    //careteHandler
    private void createHandler() {

        if (hd == null) {

            hd = new Handler(getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {

                    if (msg.what == MSG_SHOW) {

                        addCustomizedLayerItems();
                    } else if (msg.what == MSG_HIDE) {

                        BNRouteGuideManager.getInstance().showCustomizedLayer(false);
                    } else if (msg.what == MSG_RESET_NODE) {

                        Bundle mBundle = msg.getData();

                        BNRoutePlanNode rNode = new BNRoutePlanNode(mBundle.getDouble(RESET_NODE_LAT),
                                mBundle.getDouble(RESET_NODE_LON), "", null,
                                BNRoutePlanNode.CoordinateType.BD09LL);

                        BNRouteGuideManager.getInstance().resetEndNodeInNavi(rNode);
                    }
                }
            };
        }
    }

    //联动生命周期
    @Override
    protected void onResume() {
        super.onResume();

        if (isUseCommonInterface) {

            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onResume();
            }
        } else {

            BNRouteGuideManager.getInstance().onResume();
        }

        if (hd != null) {

            hd.sendEmptyMessageAtTime(MSG_SHOW, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isUseCommonInterface) {

            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onPause();
            }
        } else {

            BNRouteGuideManager.getInstance().onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isUseCommonInterface) {
            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onDestroy();
            }
        } else {

            BNRouteGuideManager.getInstance().onDestroy();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isUseCommonInterface) {

            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onStop();
            } else {

                BNRouteGuideManager.getInstance().onStop();
            }
        }

    }

    @Override
    public void onBackPressed() {

        if (isUseCommonInterface) {

            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onBackPressed(false);
            }
        } else {

            BNRouteGuideManager.getInstance().onBackPressed(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (isUseCommonInterface) {

            if (mBaiduNaviCommonModule != null) {

                mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
            }
        } else {

            BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        }
    }
}
