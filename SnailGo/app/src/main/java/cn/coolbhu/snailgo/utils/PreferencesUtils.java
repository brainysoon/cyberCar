package cn.coolbhu.snailgo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ken on 16-7-29.
 */
public class PreferencesUtils {

    private static final String SETTINGS_KEY_REGULATION_LOCATION = "settings_key_regulation_location";

    //Instance
    private static PreferencesUtils mInstance;

    //SharedPreferences
    private static SharedPreferences mPreferences;

    //Single
    private PreferencesUtils(final Context context) {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //getInstance
    public static PreferencesUtils getInstance(final Context context) {

        if (mInstance == null) {

            mInstance = new PreferencesUtils(context);
        }

        return mInstance;
    }

    //自定义查询违章信息是否需要定位
    public boolean isSettingsRegulationLocation() {

        return mPreferences.getBoolean(SETTINGS_KEY_REGULATION_LOCATION, true);
    }


}
