package cn.coolbhu.snailgo.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cn.coolbhu.snailgo.R;

/**
 * Created by ken on 2016/8/22.
 */
public class SettingFragment extends PreferenceFragment{

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        addPreferencesFromResource(R.xml.matence);
    }
}
