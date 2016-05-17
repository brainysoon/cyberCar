package com.fat246.cybercar.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.fat246.cybercar.R;
import com.fat246.cybercar.activities.Register.RegisterActivity;
import com.fat246.cybercar.activities.carmusics.BaseActivity;
import com.fat246.cybercar.activities.carmusics.NowPlayingActivity;
import com.fat246.cybercar.activities.moregas.MoreGasActivity;
import com.fat246.cybercar.activities.navigate.InitNavigateActivity;
import com.fat246.cybercar.activities.regulations.CustomRegulationActivity;
import com.fat246.cybercar.activities.regulations.MyRegulationsActivity;
import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.fragments.AlbumDetailFragment;
import com.fat246.cybercar.fragments.ArtistDetailFragment;
import com.fat246.cybercar.fragments.MainFragment;
import com.fat246.cybercar.fragments.PlaylistFragment;
import com.fat246.cybercar.fragments.QueueFragment;
import com.fat246.cybercar.openwidgets.CircleImageView;
import com.fat246.cybercar.permissions.Nammu;
import com.fat246.cybercar.permissions.PermissionCallback;
import com.fat246.cybercar.slidinguppanel.SlidingUpPanelLayout;
import com.fat246.cybercar.tools.HelpInitBoomButton;
import com.fat246.cybercar.tools.MusicPlayer;
import com.fat246.cybercar.utils.Constants;
import com.fat246.cybercar.utils.NavigationUtils;
import com.fat246.cybercar.utils.PreferencesUtility;
import com.fat246.cybercar.utils.TimberUtils;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.listener.DownloadFileListener;


public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer {

    //主Activity 实例
    private static MainActivity mMainActivity;

    //View
    private BoomMenuButton mBoomMenuButton;
    private CircleImageView mAvator;
    private TextView mAccount;
    private TextView mRegister;

    //Timber View 相关 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private SlidingUpPanelLayout mPanelLayout;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    //Action
    String action;

    //上一次摁推出建的时间
    private long exitTime = 0;

    //Map
    Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();

    Handler navDrawerRunnable = new Handler();

    Runnable runnable;


    final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };


    //确定当前主题
    private boolean isDarkTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化Activity 的一些参数
        initMainActivity();

        //初始化Map
        initNavigationMap();

        //找到 View
        findView();

        //初始化 BoomMenuButton
        initBoomMenu();

        //初始化headr
        initMenuHeader(mNavigationView.getHeaderView(0));

        //设置 PanelLayout
        setPanelSlideListeners(mPanelLayout);

        //初始化Drawer
        initDrawer();

        //初始化权限
        initPermission();

        addBackstackListener();

        //响应Action
        toAction();

//        Toast.makeText(this, MyApplication.mUserInfo.getUserID() + "", Toast.LENGTH_LONG).show();
    }

    //初始化BoomMenuButton
    private void initBoomMenu() {

        HelpInitBoomButton.initBoomButton(this, mBoomMenuButton, 6);

        //设置监听器
        mBoomMenuButton.setOnSubButtonClickListener(new BoomMenuButton.OnSubButtonClickListener() {
            @Override
            public void onClick(int buttonIndex) {

                runnable = null;

                switch (buttonIndex) {

                    //用户信息
                    case HelpInitBoomButton.INDEX_USERINFO:
                        runnable = navigateMyInfo;

                        break;

                    //车辆信息
                    case HelpInitBoomButton.INDEX_CARINFOS:
                        runnable = navigateMyCars;

                        break;

                    //加油
                    case HelpInitBoomButton.INDEX_MOREGAS:
                        runnable = navigateMoreGas;

                        break;

                    //违章信息
                    case HelpInitBoomButton.INDEX_REGULATIONS:
                        runnable = navigateCustomRegulations;

                        break;


                    //导航
                    case HelpInitBoomButton.INDEX_NAVIGATE:
                        runnable = navigateNavigate;

                        break;

                    //设置
                    case HelpInitBoomButton.INDEX_SETTING:
                        runnable = navigateSetting;

                        break;
                }

                if (runnable != null) {

                    mDrawerLayout.closeDrawers();
                    Handler handler = new Handler();

                    //这个时间间隔要设置到那个动画刚好结束才行
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                        }
                    }, 1000);
                }
            }
        });
    }

    //响应Action
    private void toAction() {
        if (Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.clearQueue();
                    MusicPlayer.openFile(getIntent().getData().getPath());
                    MusicPlayer.playOrPause();
                    navigateNowplaying.run();
                }
            }, 350);
        }
    }

    //检查权限  START +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void initPermission() {

        if (TimberUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            loadEverything();
        }
    }

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }

        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(mPanelLayout, "Timber will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Nammu.askForPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                Nammu.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
            }
        }
    }
    //END OF IT ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //初始化 Drawer START +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void initDrawer() {

        //推迟执行
        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(mNavigationView);
                setupNavigationIcons(mNavigationView);
            }
        }, 700);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    private void setupNavigationIcons(NavigationView navigationView) {

        //material-icon-lib currently doesn't work with navigationview of design support library 22.2.0+
        //set icons manually for now
        //https://github.com/code-mc/material-icon-lib/issues/15

        if (!isDarkTheme) {

            //用户信息
            navigationView.getMenu().findItem(R.id.nav_myinfo).setIcon(R.drawable.ic_account_circle_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_myorders).setIcon(R.drawable.ic_description_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_mycars).setIcon(R.drawable.ic_directions_car_2x);
            navigationView.getMenu().findItem(R.id.nav_myregulations).setIcon(R.drawable.ic_gavel_2x);

            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);

            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_help).setIcon(R.drawable.help_circle);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
        } else {

            //用户信息
            navigationView.getMenu().findItem(R.id.nav_myinfo).setIcon(R.drawable.ic_account_circle_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_myorders).setIcon(R.drawable.ic_description_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_mycars).setIcon(R.drawable.ic_directions_car_white_2x);
            navigationView.getMenu().findItem(R.id.nav_myregulations).setIcon(R.drawable.ic_gavel_white_24dp);

            //音乐
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music_white);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music_white);

            //App信息
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.nav_help).setIcon(R.drawable.help_circle_white);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information_white);
        }

    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {

            //用户信息
            case R.id.nav_myinfo:
                runnable = navigateMyInfo;

                break;

            //用户订单
            case R.id.nav_myorders:
                runnable = navigateMyOrders;

                break;

            //用户车辆
            case R.id.nav_mycars:
                runnable = navigateMyCars;

                break;

            //用户违章信息
            case R.id.nav_myregulations:
                runnable = navigateMyRegulations;

                break;

            case R.id.nav_library:
                runnable = navigateLibrary;

                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;

                break;
            case R.id.nav_nowplaying:
                NavigationUtils.navigateToNowplaying(MainActivity.this, false);
                break;
            case R.id.nav_queue:
                runnable = navigateQueue;

                break;
            case R.id.nav_settings:
                runnable = navigateSetting;

                break;
            case R.id.nav_help:
                runnable = navigateFeedback;

                break;

            case R.id.nav_about:
//                mDrawerLayout.closeDrawers();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Helpers.showAbout(MainActivity.this);
//                    }
//                }, 350);

                runnable = navigateAbout;

                break;

        }

        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }
    // END OF IT +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //初始化headerlayout
    private void initMenuHeader(View mHeader) {

        mAvator = (CircleImageView) mHeader.findViewById(R.id.menu_header_imageview_avator);
        mAccount = (TextView) mHeader.findViewById(R.id.menu_header_textview_account);
        mRegister = (TextView) mHeader.findViewById(R.id.menu_header_textview_register);

//        //判断用户是否登陆
//        if (MyApplication.isLoginSucceed) {
//
//            mAvator.setImageResource(R.drawable.avator);
//
//            mAccount.setText(MyApplication.mUserInfo.getUserName());
//
//            mEmail.setText(MyApplication.mUserInfo.getUserPassword());
//        }

        //头像点击事件
        mAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(mIntent);
            }
        });

        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(mIntent);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(mIntent);
            }
        });
    }

    //findView
    private void findView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawerlayout_drawer);
        mPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mNavigationView = (NavigationView) findViewById(R.id.activity_main_navigationview_nav);

        mBoomMenuButton = (BoomMenuButton) findViewById(R.id.activity_main_boommenubutton_menu);
    }

    //初始化Navigation Map
    private void initNavigationMap() {

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
    }

    //一些初始化动作
    private void initMainActivity() {

        //初始化实例
        mMainActivity = this;

        //判断是自动播放音乐
        if (PreferencesUtility.getInstance(this).isSettingsMusicAuto()) {

            if (!MusicPlayer.isPlaying()) {

                MusicPlayer.playOrPause();
            }
        }

        //获取Action
        action = getIntent().getAction();

        //判断当前主题
        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //Drawer
        if (mNavigationView.isShown()) {

            mDrawerLayout.closeDrawers();

            return;
        }

        //BoomMenu
        if (!mBoomMenuButton.isClosed()) {

            mBoomMenuButton.dismiss();

            return;
        }

        //是否退出
        if (System.currentTimeMillis() - exitTime > 3000) {

            Toast.makeText(this, "再按一次，就走！", Toast.LENGTH_SHORT).show();

            exitTime = System.currentTimeMillis();
        } else {

            super.onBackPressed();
        }
    }

    //    public void setDetailsToHeader() {
//        String name = MusicPlayer.getTrackName();
//        String artist = MusicPlayer.getArtistName();
//
//        if (name != null && artist != null) {
//            songtitle.setText(name);
//            songartist.setText(artist);
//        }
//        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
//                new DisplayImageOptions.Builder().cacheInMemory(true)
//                        .showImageOnFail(R.drawable.ic_empty_music2)
//                        .resetViewBeforeLoading(true)
//                        .build());
//    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
//        setDetailsToHeader();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainActivity = this;

        if (MyApplication.isLoginSucceed) {

            setLogin();
        } else {

            backToUnLogin();
        }
    }

    //回到未登录
    private void backToUnLogin() {

        mAccount.setText("立即登录");
        mRegister.setText("立即注册");

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(mIntent);
            }
        });

        mAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);

                        startActivity(mIntent);
                    }
                }
        );

        mAvator.setImageResource(R.drawable.avator);

        mAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(mIntent);
            }
        });
    }

    //设置登陆
    private void setLogin() {

        mAccount.setText(MyApplication.mUser.getUser_NickName());

        MyApplication.mUser.getUser_Avator().download(this, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {


                Bitmap bt = BitmapFactory.decodeFile(s);//图片地址

                mAvator.setImageBitmap(bt);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent=new Intent(MainActivity.this,MyInfoActivity.class);

                startActivity(mIntent);
            }
        });

        mRegister.setText("注销");
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backToUnLogin();
            }
        });

        mAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(MainActivity.this,MyInfoActivity.class);

                startActivity(mIntent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment || currentFragment instanceof QueueFragment
                || currentFragment instanceof PlaylistFragment);
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }

    //返回当前Activity 的Theme
    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    //返回当前MainActivity的Instance
    public static MainActivity getInstance() {
        return mMainActivity;
    }

    //onStop


    @Override
    protected void onStop() {
        super.onStop();

        //是否继续播放音乐
        if (!PreferencesUtility.getInstance(this).isSettingsMusicContinue()) {

            if (MusicPlayer.isPlaying()) {

                MusicPlayer.playOrPause();
            }
        }
    }

    /***********************************************************************************************
     * navigate... MainActivity 下面的各种Runnable
     */
    Runnable navigateLibrary = new Runnable() {
        public void run() {
            mNavigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        }
    };

    //NowplayingActivity
    Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigateLibrary.run();
            startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
        }
    };

    Runnable navigatePlaylist = new Runnable() {
        public void run() {
            mNavigationView.getMenu().findItem(R.id.nav_playlists).setChecked(true);
            Fragment fragment = new PlaylistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };
    Runnable navigateQueue = new Runnable() {
        public void run() {
            mNavigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
            Fragment fragment = new QueueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };
    Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = AlbumDetailFragment.newInstance(albumID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };
    Runnable navigateArtist = new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = ArtistDetailFragment.newInstance(artistID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    //我的信息
    Runnable navigateMyInfo = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, MyInfoActivity.class);

            startActivity(mIntent);
        }
    };

    //我的订单
    Runnable navigateMyOrders = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, MyOrdersActivity.class);

            startActivity(mIntent);
        }
    };

    //我的汽车
    Runnable navigateMyCars = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, MyCarsActivity.class);

            startActivity(mIntent);
        }
    };

    //我的违章
    Runnable navigateMyRegulations = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, MyRegulationsActivity.class);

            startActivity(mIntent);
        }
    };

    //预约加油
    Runnable navigateMoreGas = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, MoreGasActivity.class);

            startActivity(mIntent);
        }
    };

    //导航
    Runnable navigateNavigate = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, InitNavigateActivity.class);

            startActivity(mIntent);
        }
    };

    //自定义查询违章信息
    Runnable navigateCustomRegulations = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, CustomRegulationActivity.class);

            startActivity(mIntent);
        }
    };

    //设置
    Runnable navigateSetting = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, SettingsActivity.class);

            startActivity(mIntent);
        }
    };

    //关于
    Runnable navigateAbout = new Runnable() {
        @Override
        public void run() {

            Intent mIntent = new Intent(MainActivity.this, AboutActivity.class);

            startActivity(mIntent);

        }
    };

    //用户反馈
    Runnable navigateFeedback = new Runnable() {
        @Override
        public void run() {

            //这里有一个Bug 没有  Activity 来接收这个   Intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:kensoon918@163.com");

            //需要判断一下是否有接受这个Intent
            intent.setData(data);
            startActivity(intent);
        }
    };
    /***********************************************************************************************
     */
}