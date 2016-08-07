package cn.coolbhu.snailgo.fragments.muisc.nowplaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.coolbhu.snailgo.R;

public class PlayingTheme3 extends BaseNowplayingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_playing_theme3, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        return rootView;
    }

}
