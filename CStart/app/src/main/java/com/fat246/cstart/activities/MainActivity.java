package com.fat246.cstart.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cheshouye.api.client.WeizhangIntentService;
import com.fat246.cstart.R;
import com.fat246.cstart.fragments.main.VolMainFragment;
import com.fat246.cstart.utils.BottomBarFrgmentUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity implements OnMenuTabClickListener
        , VolMainFragment.VolMainFragmentCallback {

    //获得手机权限
    public static final int MY_PERMISSION_ERQUSET_PHONE = 1;

    //BottomBar
    private BottomBar mBottomBar;

    //MaterialDrawer
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;

    //Toolbar
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        initBottomBar(savedInstanceState);

        initMaterialDrawer(savedInstanceState);
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

        // Create the AccountHeader
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)


                        //false if you have not consumed the event and it should close the drawer
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
                        new PrimaryDrawerItem().withName("Hello").withDescription("HelloAgain").withIcon(GoogleMaterial.Icon.gmd_sun).withIdentifier(1).withSelectable(false)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

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

    //VolMainFragment 回调
    @Override
    public void startedWeiZhangService() {

        //判断当前版本是否大于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //判断是否具有该权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.PHONE},
                        MY_PERMISSION_ERQUSET_PHONE);

            } else {

                initCheShouYe();

            }
        } else {

            initCheShouYe();

        }
    }

    //初始化车首页服务
    private void initCheShouYe() {

        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);

        // 您的appId
        weizhangIntent.putExtra("appId", 1626);
        // 您的appKey
        weizhangIntent.putExtra("appKey", "624fbe005c8138700f31bdfca484f878");

        //开启服务
        startService(weizhangIntent);
    }

    //获取权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSION_ERQUSET_PHONE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initCheShouYe();
                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.PHONE}
                            , MY_PERMISSION_ERQUSET_PHONE);
                }
            }
            break;
        }
    }
}
