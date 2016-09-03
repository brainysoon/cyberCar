package com.fat246.cybercar.activities.carmusics;

import android.support.annotation.Nullable;

import com.afollestad.appthemeengine.ATEActivity;
import com.fat246.cybercar.utils.Helpers;

/**
 * Created by naman on 31/12/15.
 */
public class BaseThemedActivity extends ATEActivity {

    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }
}
