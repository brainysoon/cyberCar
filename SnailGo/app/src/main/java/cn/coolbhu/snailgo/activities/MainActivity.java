package cn.coolbhu.snailgo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.cars.MyCarsActivity;
import cn.coolbhu.snailgo.activities.moregas.MyOrdersActivity;
import cn.coolbhu.snailgo.activities.musics.BaseActivity;
import cn.coolbhu.snailgo.activities.musics.NowPlayingActivity;
import cn.coolbhu.snailgo.activities.register.RegisterActivity;
import cn.coolbhu.snailgo.activities.regulations.MyRegulationsActivity;
import cn.coolbhu.snailgo.fragments.main.HomeMainFragment;
import cn.coolbhu.snailgo.fragments.main.VolMainFragment;
import cn.coolbhu.snailgo.helpers.MusicPlayer;
import cn.coolbhu.snailgo.utils.AutoUpdateManager;
import cn.coolbhu.snailgo.utils.BottomBarFrgmentUtils;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

public class MainActivity extends BaseActivity implements OnMenuTabClickListener
        , VolMainFragment.VolMainFragmentCallback, Drawer.OnDrawerItemClickListener
        , AutoUpdateManager.AfterUpdate, AccountHeader.OnAccountHeaderListener {

    //DrawerItem
    public static final int DRAWER_ITEM_MY_CAR = 3;
    public static final int DRAWER_ITEM_MY_ORDER = 4;
    public static final int DRAWER_ITEM_MY_INFO = 5;
    public static final int DRAWER_ITEM_MY_REGULATION = 6;
    public static final int DRAWER_ITEM_ABOUT = 7;
    public static final int DRAWER_ITEM_FEEDBACK = 8;
    public static final int DRAWER_ITEM_SETTING = 9;
    public static final int DRAWER_ITME_UPDATE = 10;

    public static final int DRAWER_ITEM_NOW_PLAYING = 11;

    public static final int PROFILE_ITEM_NO_USER = 21;
    public static final int PROFILE_ITEM_REGISTER = 22;
    public static final int PROFILE_ITEM_USER = 23;

    public static final int PROFILE_ITEM_DEFAULT_POSITION = 0;

    //MainActivity的实例
    public static MainActivity mInstance = null;

    //BottomBar
    private BottomBar mBottomBar;

    //MaterialDrawer
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;

    //Toolbar
    private Toolbar mToolbar;

    //上一次摁退出建的时间
    private long exitTime = 0;

    //Handler
    private Handler mHandler = new Handler();

    //标记是否处该进入播放音乐
    public boolean shoulPlayMusic = true;

    //播放音乐
    Runnable toPlayMusic = new Runnable() {
        @Override
        public void run() {

            MusicPlayer.playOrPause();
        }
    };

    //我的信息
    Runnable nagToMyInfo = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, MyInfoActivity.class);

            startActivity(intent);
        }
    };

    //立即注册
    Runnable nagToRegister = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

            startActivity(intent);
        }
    };

    //登录
    Runnable nagToLogIn = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };

    //我的违章
    Runnable nagToMyRegulation = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, MyRegulationsActivity.class);
            startActivity(intent);
        }
    };

    //设置
    Runnable nagToSetting = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            startActivity(intent);
        }
    };

    //关于
    Runnable nagToAbout = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, AboutActivity.class);

            startActivity(intent);
        }
    };

    //反馈
    Runnable nagToFeedBack = new Runnable() {
        @Override
        public void run() {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(R.string.drawer_item_feedback);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("你好，你可以将反馈信息发送到 \n邮箱：kensoon918@163.com \n或则直接拨打13166956701");

            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            builder.create().show();
        }
    };

    //检查更新
    Runnable nagToUpdate = new Runnable() {
        @Override
        public void run() {

            AutoUpdateManager autoUpdateManager = new AutoUpdateManager(MainActivity.this);

            autoUpdateManager.beginUpdate(MainActivity.this);

            Toast.makeText(MainActivity.this, "检查更新，请稍候...", Toast.LENGTH_SHORT).show();
        }
    };

    //我的汽车
    Runnable nagToMyCar = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, MyCarsActivity.class);

            startActivity(intent);
        }
    };

    //我的订单
    Runnable nagToMyOrder = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);

            startActivity(intent);
        }
    };

    //正在播放
    Runnable nagToNowPlaying = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);

            startActivity(intent);
        }
    };

    //未登录提示
    public Runnable showNoUserNotice = new Runnable() {
        @Override
        public void run() {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.notice)
                    .setMessage("亲，你还没有登录，请登录后再试！")
                    .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("以后登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });

            builder.create().show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInstance = this;

        initToolbar();

        initBottomBar(savedInstanceState);

        initMaterialDrawer(savedInstanceState);

        toDoBeforeCreated();
    }

    private void toDoBeforeCreated() {

        //设置不是第一次启动App
        if (PreferencesUtils.getInstance(this).setIsFirstLoad()) {

//            showcaseView();
            //setNot
            PreferencesUtils.getInstance(this).setNotFirstLoad();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        toDoBeforeIn();
    }

    //启动之前应该做的
    private void toDoBeforeIn() {

        if (shoulPlayMusic) {

            //判断是自动播放音乐
            if (PreferencesUtils.getInstance(this).isSettingsMusicAuto()) {

                if (!MusicPlayer.isPlaying()) {

                    mHandler.postDelayed(toPlayMusic, 1000);
                }
            }

            shoulPlayMusic = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //保存BottomBar的状态
        mBottomBar.onSaveInstanceState(outState);
    }

    //初始化BottomBar
    private void initBottomBar(Bundle savedInstanceState) {

        mBottomBar = BottomBar.attach(this, savedInstanceState);

        mBottomBar.useFixedMode();
        mBottomBar.setItems(R.menu.menu_bottombar);
        mBottomBar.setOnMenuTabClickListener(this);

        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(4, ContextCompat.getColor(this, R.color.colorPrimary));
    }

    //初始化Toolbar
    private void initToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
    }

    //初始化MaterialDrawer
    private void initMaterialDrawer(Bundle savedInstanceState) {

        //DrawerItem
        PrimaryDrawerItem itemMyOrder = new PrimaryDrawerItem();
        itemMyOrder.withIdentifier(DRAWER_ITEM_MY_ORDER)
                .withName(R.string.drawer_item_my_order)
                .withIcon(GoogleMaterial.Icon.gmd_assignment);

        PrimaryDrawerItem itemMyCar = new PrimaryDrawerItem();
        itemMyCar.withIdentifier(DRAWER_ITEM_MY_CAR)
                .withName(R.string.drawer_item_my_car)
                .withIcon(GoogleMaterial.Icon.gmd_car);

        PrimaryDrawerItem itemMyInfo = new PrimaryDrawerItem();
        itemMyInfo.withIdentifier(DRAWER_ITEM_MY_INFO)
                .withName(R.string.drawer_item_my_info)
                .withIcon(GoogleMaterial.Icon.gmd_account);

        PrimaryDrawerItem itemMyRegulation = new PrimaryDrawerItem();
        itemMyRegulation.withIdentifier(DRAWER_ITEM_MY_REGULATION)
                .withName(R.string.drawer_item_my_regulation)
                .withIcon(GoogleMaterial.Icon.gmd_star);

        PrimaryDrawerItem itemNowPlaying = new PrimaryDrawerItem();
        itemNowPlaying.withIdentifier(DRAWER_ITEM_NOW_PLAYING)
                .withName(R.string.drawer_item_now_playing)
                .withIcon(GoogleMaterial.Icon.gmd_equalizer);

        SecondaryDrawerItem itemSetting = new SecondaryDrawerItem();
        itemSetting.withIdentifier(DRAWER_ITEM_SETTING)
                .withName(R.string.drawer_item_setting)
                .withIcon(GoogleMaterial.Icon.gmd_settings);

        SecondaryDrawerItem itemAbout = new SecondaryDrawerItem();
        itemAbout.withIdentifier(DRAWER_ITEM_ABOUT)
                .withName(R.string.drawer_item_about)
                .withIcon(GoogleMaterial.Icon.gmd_info);

        SecondaryDrawerItem itemFeedBack = new SecondaryDrawerItem();
        itemFeedBack.withIdentifier(DRAWER_ITEM_FEEDBACK)
                .withName(R.string.drawer_item_feedback)
                .withIcon(GoogleMaterial.Icon.gmd_adb);

        SecondaryDrawerItem itemUpdate = new SecondaryDrawerItem();
        itemUpdate.withIdentifier(DRAWER_ITME_UPDATE)
                .withName(R.string.drawer_item_update)
                .withIcon(GoogleMaterial.Icon.gmd_refresh);

        ProfileSettingDrawerItem proRegister = new ProfileSettingDrawerItem();
        proRegister.withIdentifier(PROFILE_ITEM_REGISTER)
                .withName("立即注册")
                .withIcon(GoogleMaterial.Icon.gmd_account_add);

        ProfileDrawerItem proAccount = new ProfileDrawerItem();
        proAccount.withIdentifier(PROFILE_ITEM_NO_USER)
                .withName("未登录")
                .withEmail("点击登录或者注册")
                .withIcon(R.drawable.profile)
                .withNameShown(true);


        // Create the AccountHeader
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        proAccount,
                        proRegister
                )
                .withOnAccountHeaderListener(this)
                .withSavedInstance(savedInstanceState)
                .build();

        //
        if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

            updateUserInfo();
        }

        //Create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(mAccountHeader) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        itemMyInfo,
                        itemMyCar,
                        itemMyOrder,
                        itemMyRegulation,
                        new DividerDrawerItem(),
                        itemNowPlaying,
                        new DividerDrawerItem(),
                        itemAbout,
                        itemSetting,
                        itemFeedBack,
                        itemUpdate
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

        Runnable runnable = null;

        //非用户相关
        if (drawerItem.getIdentifier() == DRAWER_ITEM_NOW_PLAYING) {

            runnable = nagToNowPlaying;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_SETTING) {

            runnable = nagToSetting;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_ABOUT) {

            runnable = nagToAbout;
        } else if (drawerItem.getIdentifier() == DRAWER_ITME_UPDATE) {

            runnable = nagToUpdate;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_FEEDBACK) {

            runnable = nagToFeedBack;
        } else {

            if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

                //用户相关
                if (drawerItem.getIdentifier() == DRAWER_ITEM_MY_CAR) {

                    runnable = nagToMyCar;
                } else if (drawerItem.getIdentifier() == DRAWER_ITEM_MY_ORDER) {

                    runnable = nagToMyOrder;
                } else if (drawerItem.getIdentifier() == DRAWER_ITEM_MY_INFO) {

                    runnable = nagToMyInfo;
                } else if (drawerItem.getIdentifier() == DRAWER_ITEM_MY_REGULATION) {

                    runnable = nagToMyRegulation;
                }
            } else {

                runnable = showNoUserNotice;
            }
        }

        if (runnable != null) {

            mHandler.postDelayed(runnable, 300);

        }

        return false;
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

        Runnable runnable = null;

        //处理点击事件
        if (profile.getIdentifier() == PROFILE_ITEM_USER) {

            runnable = nagToMyInfo;

        } else if (profile.getIdentifier() == PROFILE_ITEM_NO_USER) {

            runnable = nagToLogIn;
        } else if (profile.getIdentifier() == PROFILE_ITEM_REGISTER) {

            runnable = nagToRegister;
        }

        if (runnable != null) {

            mHandler.postDelayed(runnable, 300);
        }


        return false;
    }

    //Impliment OnMenuTabClickListener
    @Override
    public void onMenuTabSelected(@IdRes int menuItemId) {

        Fragment mFragment = BottomBarFrgmentUtils.getFragmentByMenuItemId(menuItemId);

        getSupportFragmentManager().beginTransaction().replace(R.id.continer, mFragment).commit();
    }

    @Override
    public void onMenuTabReSelected(@IdRes int menuItemId) {

    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen()) {

            mDrawer.closeDrawer();

            return;
        }

        switch (mBottomBar.getCurrentTabPosition()) {

            case 0:

                WebView webView = ((HomeMainFragment) BottomBarFrgmentUtils.mFragments[0]).mWebView;

                if (webView != null && webView.canGoBack()) {

                    webView.goBack();
                } else {

                    if (!shouldShowToast()) {

                        toDoBeforeOut();

                        //退到后台
                        moveTaskToBack(true);
                    }
                }
                return;

            default:
                break;
        }

        //显示提示页面
        if (!shouldShowToast()) {

            toDoBeforeOut();

            //退到后台
            moveTaskToBack(true);
        }
    }

    //在退出之前需要做的
    private void toDoBeforeOut() {

        //是否继续播放音乐
        if (!PreferencesUtils.getInstance(this).isSettingsMusicContinue()) {

            if (MusicPlayer.isPlaying()) {

                MusicPlayer.playOrPause();
            }
        }

        shoulPlayMusic = true;
    }

    //提示
    private boolean shouldShowToast() {

        //是否退出
        if (System.currentTimeMillis() - exitTime > 3000) {

            Toast.makeText(this, "再按一次，退出！", Toast.LENGTH_SHORT).show();

            exitTime = System.currentTimeMillis();

            return true;
        } else {

            //是否继续播放音乐
            if (false) {

                if (MusicPlayer.isPlaying()) {

                    MusicPlayer.playOrPause();
                }
            }

            return false;
        }
    }

    @Override
    public void toDoAfterUpdate() {

    }

    @Override
    public void toShowNoNeedUpdate() {

        Toast.makeText(this, "已是最新版本，无需更新！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toShowError(int error) {

        Toast.makeText(this, "网络错误，请稍后重试！", Toast.LENGTH_SHORT).show();
    }

    //登录成功过后
    public void updateUserInfo() {

        if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

            //首先移除原来的
            mAccountHeader.removeProfile(PROFILE_ITEM_DEFAULT_POSITION);

            ProfileDrawerItem proAccount = new ProfileDrawerItem();
            proAccount.withIdentifier(PROFILE_ITEM_USER)
                    .withName(MyApplication.mUser.getUser_NickName())
                    .withIcon(MyApplication.mUser.getUser_Avator().getUrl())
                    .withNameShown(true);

            if (PreferencesUtils.getInstance(this).shouldShowBirthday()) {

                proAccount.withEmail(MyApplication.mUser.getUser_Birthday());
            } else {

                proAccount.withEmail("****-**-**");
            }

            mAccountHeader.addProfile(proAccount, PROFILE_ITEM_DEFAULT_POSITION);
        }
    }

    //退出登录
    public void updateAfterLogOut() {

        if (!MyApplication.isLoginSucceed && MyApplication.mUser == null) {

            //移除原来的Accout
            mAccountHeader.removeProfile(PROFILE_ITEM_DEFAULT_POSITION);

            ProfileDrawerItem proAccount = new ProfileDrawerItem();
            proAccount.withIdentifier(PROFILE_ITEM_NO_USER)
                    .withName("未登录")
                    .withEmail("点击登录或者注册")
                    .withIcon(R.drawable.profile)
                    .withNameShown(true);

            mAccountHeader.addProfile(proAccount, PROFILE_ITEM_DEFAULT_POSITION);
        }
    }
}
