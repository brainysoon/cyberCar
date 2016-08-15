package cn.coolbhu.snailgo.activities;

import android.os.Bundle;
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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.musics.BaseActivity;
import cn.coolbhu.snailgo.fragments.main.HomeMainFragment;
import cn.coolbhu.snailgo.fragments.main.VolMainFragment;
import cn.coolbhu.snailgo.helpers.MusicPlayer;
import cn.coolbhu.snailgo.utils.BottomBarFrgmentUtils;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

public class MainActivity extends BaseActivity implements OnMenuTabClickListener
        , VolMainFragment.VolMainFragmentCallback {

    //BottomBar
    private BottomBar mBottomBar;

    //MaterialDrawer
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;

    //Toolbar
    private Toolbar mToolbar;

    //上一次摁退出建的时间
    private long exitTime = 0;

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
}
