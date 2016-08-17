package cn.coolbhu.snailgo.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/5/14.
 */
public class FormatUtils {

    //判断是不是11位手机号码
    public static boolean isTel(String tel) {

        if (tel.length() != 11) {

            Log.e(">>>>>here", "length");

            return false;
        }

        if (!(tel.charAt(0) == '1')) {

            Log.e(">>>>>here", "zero");

            return false;
        }

        for (int i = 0; i < tel.length(); i++) {

            if (!(tel.charAt(i) >= '0' && tel.charAt(i) <= '9')) {

                Log.e(">>>>>here", "i=" + i);

                return false;
            }
        }

        return true;
    }

    //验证昵称
    public static boolean isNick(String nick) {

        //昵称必须为三到八位
        if (!(nick.length() >= 2 && nick.length() <= 8)) {

            return false;
        }

        //不包含特殊字符
        for (int i = 0; i < nick.length(); i++) {

            char t = nick.charAt(i);
            if (!((t <= 256 && ((t >= '0' && t <= '9') || (t >= 'a' && t <= 'z') || (t >= 'A' && t <= 'Z') || t == '_')) || (t > 256))) {

                return false;
            }
        }

        return true;
    }

    //验证密码
    public static boolean isPassword(String password) {

        boolean count1 = false;
        boolean count2 = false;
        boolean count3 = false;


        if (!(password.length() >= 6 && password.length() <= 16)) {

            return false;
        }

        for (int i = 0; i < password.length(); i++) {

            char t = password.charAt(i);

            if (t >= '0' && t <= '9') {

                count1 = true;
            } else if ((t >= 'a' && t >= 'z') || (t >= 'A' && t <= 'Z')) {

                count2 = true;
            } else if (t == ' ') {

                return false;
            } else {

                count3 = true;
            }
        }

        int count = 0;

        if (count1) count++;
        if (count2) count++;
        if (count3) count++;

        if (count >= 2) return true;
        else return false;

    }

    //验证验证码
    public static boolean isCode(String code) {

        if (!(code.length() == 6)) {

            return false;
        }

        for (int i = 0; i < 5; i++) {

            char t = code.charAt(i);

            if (!(t >= '0' && t <= '9')) {

                return false;
            }
        }

        return true;
    }
}
