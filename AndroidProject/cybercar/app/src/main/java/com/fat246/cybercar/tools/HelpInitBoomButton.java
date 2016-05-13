package com.fat246.cybercar.tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.fat246.cybercar.R;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.Random;

public class HelpInitBoomButton {

    private HelpInitBoomButton() {
    }

    ;

    //菜单名字
    private static final String USERINFO = "个人";
    private static final String CARINFOS = "车辆";
    private static final String MOREGAS = "加油";
    private static final String NAVIGATE = "导航";
    private static final String REGULATIONS = "违章";
    private static final String SETTING = "设置";

    //ButtonIndex
    public static final int INDEX_USERINFO = 0;
    public static final int INDEX_CARINFOS = 1;
    public static final int INDEX_MOREGAS = 2;
    public static final int INDEX_REGULATIONS = 3;
    public static final int INDEX_NAVIGATE = 4;
    public static final int INDEX_SETTING = 5;

    //Resource
    private static int[] drawablesResource = new int[]{
            R.drawable.ic_person,
            R.drawable.ic_car_infos,
            R.drawable.ic_more_gas,
            R.drawable.ic_regulation,
            R.drawable.ic_navigate,
            R.drawable.ic_setting
    };

    //菜单名字数组
    private static String[] subButtonTexts = new String[]{
            USERINFO,
            CARINFOS,
            MOREGAS,
            REGULATIONS,
            NAVIGATE,
            SETTING
    };

    //颜色
    private static String[] Colors = {
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#8BC34A",
            "#CDDC39",
            "#FFEB3B",
            "#FFC107",
            "#FF9800",
            "#FF5722",
            "#795548",
            "#9E9E9E",
            "#607D8B"};

    //是否需要加载资源
    private static boolean isLoad = true;

    public static void initBoomButton(Context context, BoomMenuButton mBoomButton, int mNum) {

        //加载图片资源
        Drawable[] drawables = new Drawable[mNum];

        for (int i = 0; i < mNum; i++) {

            drawables[i] = ContextCompat.getDrawable(context, drawablesResource[i]);
        }

        String[] strings = new String[mNum];

        for (int i = 0; i < mNum; i++) {

            strings[i] = subButtonTexts[i];
        }

        int[][] colors = new int[mNum][2];

        //随机数
        Random mRandom = new Random();

        for (int i = 0; i < mNum; i++) {

            int r = Math.abs(mRandom.nextInt() % Colors.length);

            colors[i][1] = Color.parseColor(Colors[r]);

            colors[i][0] = Util.getInstance().getPressedColor(colors[i][1]);
        }

        //初始化
        mBoomButton.init(
                drawables, // The drawables of images of sub buttons. Can not be null.
                strings,     // The texts of sub buttons, ok to be null.
                colors,    // The colors of sub buttons, including pressed-state and normal-state.
                ButtonType.CIRCLE,     // The button type.
                BoomType.PARABOLA,  // The boom type.
                PlaceType.CIRCLE_6_1,  // The place type.
                null,               // Ease type to move the sub buttons when showing.
                null,               // Ease type to scale the sub buttons when showing.
                null,               // Ease type to rotate the sub buttons when showing.
                null,               // Ease type to move the sub buttons when dismissing.
                null,               // Ease type to scale the sub buttons when dismissing.
                null,               // Ease type to rotate the sub buttons when dismissing.
                null                // Rotation degree.
        );

        mBoomButton.setSubButtonShadowOffset(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2));
    }
}
