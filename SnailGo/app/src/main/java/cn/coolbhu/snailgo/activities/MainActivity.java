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
import android.util.Log;
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
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.musics.BaseActivity;
import cn.coolbhu.snailgo.activities.regulations.MyRegulationsActivity;
import cn.coolbhu.snailgo.fragments.main.HomeMainFragment;
import cn.coolbhu.snailgo.fragments.main.VolMainFragment;
import cn.coolbhu.snailgo.helpers.MusicPlayer;
import cn.coolbhu.snailgo.utils.AutoUpdateManager;
import cn.coolbhu.snailgo.utils.BottomBarFrgmentUtils;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

public class MainActivity extends BaseActivity implements OnMenuTabClickListener
        , VolMainFragment.VolMainFragmentCallback, Drawer.OnDrawerItemClickListener
        , AutoUpdateManager.AfterUpdate {

    //DrawerItem
    public static final int DRAWER_ITEM_MY_REGULATION = 6;
    public static final int DRAWER_ITEM_ABOUT = 7;
    public static final int DRAWER_ITEM_FEEDBACK = 8;
    public static final int DRAWER_ITEM_SETTING = 9;
    public static final int DRAWER_ITME_UPDATE = 10;

    public static final int PROFILE_ITEM_NO_USER = 21;

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

    //Drawer跳转
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        initBottomBar(savedInstanceState);

        initMaterialDrawer(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        //设置不是第一次启动App
        if (PreferencesUtils.getInstance(this).setIsFirstLoad()) {

//            showcaseView();
            //setNot
            PreferencesUtils.getInstance(this).setNotFirstLoad();
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
        PrimaryDrawerItem itemMyRegulation = new PrimaryDrawerItem();
        itemMyRegulation.withIdentifier(DRAWER_ITEM_MY_REGULATION);
        itemMyRegulation.withName(R.string.drawer_item_my_regulation);
        itemMyRegulation.withIcon(GoogleMaterial.Icon.gmd_account_box);

        SecondaryDrawerItem itemSetting = new SecondaryDrawerItem();
        itemSetting.withIdentifier(DRAWER_ITEM_SETTING);
        itemSetting.withName(R.string.drawer_item_setting);
        itemSetting.withIcon(GoogleMaterial.Icon.gmd_settings);

        SecondaryDrawerItem itemAbout = new SecondaryDrawerItem();
        itemAbout.withIdentifier(DRAWER_ITEM_ABOUT);
        itemAbout.withName(R.string.drawer_item_about);
        itemAbout.withIcon(GoogleMaterial.Icon.gmd_info);

        SecondaryDrawerItem itemFeedBack = new SecondaryDrawerItem();
        itemFeedBack.withIdentifier(DRAWER_ITEM_FEEDBACK);
        itemFeedBack.withName(R.string.drawer_item_feedback);
        itemFeedBack.withIcon(GoogleMaterial.Icon.gmd_adb);

        SecondaryDrawerItem itemUpdate = new SecondaryDrawerItem();
        itemUpdate.withIdentifier(DRAWER_ITME_UPDATE);
        itemUpdate.withName(R.string.drawer_item_update);
        itemUpdate.withIcon(GoogleMaterial.Icon.gmd_refresh);

        //Profile
        

        // Create the AccountHeader
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withIdentifier(PROFILE_ITEM_NO_USER).withName("未登录")
                                .withEmail("请点击登录或注册").withIcon(R.drawable.profile)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

                        Log.e("this-L>>>>>>>", current + "lll");

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(mAccountHeader) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("我的信息").withIcon(GoogleMaterial.Icon.gmd_account).withIdentifier(1),
                        itemMyRegulation,
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
        if (drawerItem.getIdentifier() == DRAWER_ITEM_MY_REGULATION) {

            runnable = nagToMyRegulation;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_SETTING) {

            runnable = nagToSetting;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_ABOUT) {

            runnable = nagToAbout;
        } else if (drawerItem.getIdentifier() == DRAWER_ITME_UPDATE) {

            runnable = nagToUpdate;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_FEEDBACK) {

            runnable = nagToFeedBack;
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

            //退到后台
            moveTaskToBack(true);
        }
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
}
