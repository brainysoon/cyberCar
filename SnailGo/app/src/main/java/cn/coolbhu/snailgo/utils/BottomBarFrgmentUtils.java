package cn.coolbhu.snailgo.utils;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.main.HomeMainFragment;
import cn.coolbhu.snailgo.main.MusicMainFragment;
import cn.coolbhu.snailgo.main.NagMainFragment;
import cn.coolbhu.snailgo.main.OilMainFragment;
import cn.coolbhu.snailgo.main.VolMainFragment;

/**
 * Created by ken on 16-7-27.
 */
public class BottomBarFrgmentUtils {

    //Fragments
    private static Fragment[] mFragments = new Fragment[5];

    @Nullable
    public static Fragment getFragmentByMenuItemId(int menuItemId) {

        switch (menuItemId) {

            case R.id.menu_home:

                return getFragmentInstanceByIndex(0);

            case R.id.menu_nag:

                return getFragmentInstanceByIndex(1);

            case R.id.menu_music:

                return getFragmentInstanceByIndex(2);

            case R.id.menu_oil:

                return getFragmentInstanceByIndex(3);

            case R.id.menu_vol:

                return getFragmentInstanceByIndex(4);
        }

        return null;
    }

    private static Fragment getFragmentInstanceByIndex(int index) {

        if (mFragments[index] == null) {

            switch (index) {

                case 0:
                    mFragments[index] = HomeMainFragment.newInstance();
                    break;

                case 1:
                    mFragments[index] = NagMainFragment.newInstance();
                    break;

                case 2:
                    mFragments[index] = MusicMainFragment.newInstance();
                    break;

                case 3:
                    mFragments[index] = OilMainFragment.newInstance();
                    break;

                case 4:
                    mFragments[index] = VolMainFragment.newInstance();
                    break;

                default:
                    break;
            }

        }

        return mFragments[index];
    }
}
