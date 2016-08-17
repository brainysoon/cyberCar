package cn.coolbhu.snailgo.fragments;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.MainActivity;
import cn.coolbhu.snailgo.utils.MusicUtils;
import cn.coolbhu.snailgo.utils.NavigationUtils;
import cn.coolbhu.snailgo.utils.PreferencesUtils;

public class SettingsFragment extends PreferenceFragment {

    public static final String ACTION_EQUALIZER = "settings_key_music_action_equalizer";

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference.getKey().equals(PreferencesUtils.SETTINGS_KEY_USER_BIRTHDAY)) {

            if (MainActivity.mInstance != null) {

                MainActivity.mInstance.updateUserInfo();
            }
        } else if (preference.getKey().equals(ACTION_EQUALIZER)) {

            if (MusicUtils.hasEffectsPanel(getActivity())) {

                NavigationUtils.navigateToEqualizer(getActivity());
            }
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
