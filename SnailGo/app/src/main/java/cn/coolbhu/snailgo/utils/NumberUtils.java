package cn.coolbhu.snailgo.utils;

import java.security.InvalidParameterException;

import cn.coolbhu.snailgo.utils.number.Eight;
import cn.coolbhu.snailgo.utils.number.Five;
import cn.coolbhu.snailgo.utils.number.Four;
import cn.coolbhu.snailgo.utils.number.Nine;
import cn.coolbhu.snailgo.utils.number.Null;
import cn.coolbhu.snailgo.utils.number.One;
import cn.coolbhu.snailgo.utils.number.Seven;
import cn.coolbhu.snailgo.utils.number.Six;
import cn.coolbhu.snailgo.utils.number.Three;
import cn.coolbhu.snailgo.utils.number.Two;
import cn.coolbhu.snailgo.utils.number.Zero;

public class NumberUtils {

    public static float[][] getControlPointsFor(int start) {
        switch (start) {
            case (-1):
                return Null.getInstance().getControlPoints();
            case 0:
                return Zero.getInstance().getControlPoints();
            case 1:
                return One.getInstance().getControlPoints();
            case 2:
                return Two.getInstance().getControlPoints();
            case 3:
                return Three.getInstance().getControlPoints();
            case 4:
                return Four.getInstance().getControlPoints();
            case 5:
                return Five.getInstance().getControlPoints();
            case 6:
                return Six.getInstance().getControlPoints();
            case 7:
                return Seven.getInstance().getControlPoints();
            case 8:
                return Eight.getInstance().getControlPoints();
            case 9:
                return Nine.getInstance().getControlPoints();
            default:
                throw new InvalidParameterException("Unsupported number requested");
        }
    }
}
