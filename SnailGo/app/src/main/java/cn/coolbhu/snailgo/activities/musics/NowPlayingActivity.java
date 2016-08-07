package cn.coolbhu.snailgo.activities.musics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.utils.Constants;
import cn.coolbhu.snailgo.utils.NavigationUtils;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

public class NowPlayingActivity extends BaseActivity implements ATEActivityThemeCustomizer, ATEToolbarCustomizer {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowplaying);
        SharedPreferences prefs = getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE);
        String fragmentID = prefs.getString(Constants.NOWPLAYING_FRAGMENT_ID, Constants.TIMBER3);

        Fragment fragment = NavigationUtils.getFragmentForNowplayingID(fragmentID);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();

    }

    @StyleRes
    @Override
    public int getActivityTheme() {
//        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ? R.style.AppTheme_FullScreen_Dark : R.style.AppTheme_FullScreen_Light;

        return R.style.AppTheme_FullScreen_Light;
    }

    @Override
    public int getLightToolbarMode() {
        return Config.LIGHT_TOOLBAR_AUTO;
    }

    public int getToolbarColor() {
        return Color.TRANSPARENT;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesUtils.getInstance(this).didNowplayingThemeChanged()) {
            PreferencesUtils.getInstance(this).setNowPlayingThemeChanged(false);
            recreate();
        }
    }
}
