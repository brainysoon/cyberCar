package cn.coolbhu.snailgo.activities.musics;

import android.support.annotation.Nullable;

import com.afollestad.appthemeengine.ATEActivity;

import cn.coolbhu.snailgo.utils.Helpers;

public class BaseThemedActivity extends ATEActivity {

    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }
}
