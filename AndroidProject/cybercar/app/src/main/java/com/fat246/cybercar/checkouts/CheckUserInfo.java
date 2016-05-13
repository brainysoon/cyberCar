package com.fat246.cybercar.checkouts;

import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Pattern;

public class CheckUserInfo {

    // 正则表达式：验证用户名
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

    // 正则表达式：验证密码
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

    // 正则表达式：验证手机号
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    //正则表达式：验证邮箱
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    //what
    public static final int STATUS_ACCTOUNT = 0;
    public static final int STATUS_MOBILE = 1;
    public static final int STATUS_EMAIL = 2;

    /**
     * 校验用户名
     *
     * @param account
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isAccount(String account) {
        return Pattern.matches(REGEX_USERNAME, account);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    private CheckUserInfo() {
    }

    //校验用户名
    public static boolean checkAccount(String account, EditText Account) {

        if (!isAccount(account)) {
            Account.setError("亲，请输入正确的用户名！");
            return false;
        }

        return true;
    }

    //校验手机号
    public static boolean checkNumber(String number, EditText Number) {

        if (!isMobile(number)) {

            Number.setError("亲，这是你的手机号么！");
            return false;
        }

        return true;
    }

    //校验邮箱
    public static boolean checkEmail(String email, EditText Email) {

        if (!isEmail(email)) {

            Email.setError("亲，这那是邮箱呀！");
            return false;
        }

        return true;
    }

    //校验密码
    public static boolean checkPassword(String password, EditText Password) {

        if (!isPassword(password)) {

            Password.setError("亲，密码格式不对哦！");
            return false;
        }

        return true;
    }

    //校验两个密码是否相同
    public static boolean checkIsSamePassword(String password, String Password, EditText mConfirmPassword) {

        //防止越界
        if (password.length() > Password.length()) {

            mConfirmPassword.setTextColor(Color.RED);

            return false;
        }

        for (int i = 0; i < password.length(); i++) {

            if (password.charAt(i) != Password.charAt(i)) {

                mConfirmPassword.setTextColor(Color.RED);
                return false;
            }
        }

        mConfirmPassword.setTextColor(Color.BLACK);
        return true;

    }

    //判断是否具备注册的条件
    public static boolean canRegister(EditText mUserName, EditText mPassword, EditText mConfirmPassword, int what) {

        String userName = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confrimPassrod = mConfirmPassword.getText().toString().trim();

        if (what == STATUS_ACCTOUNT) {

            if (!checkAccount(userName, mUserName)) {

                return false;
            }

        }

        if (what == STATUS_EMAIL) {

            if (!checkEmail(userName, mUserName)) {

                return false;
            }
        }

        if (what == STATUS_MOBILE) {

            if (!checkNumber(userName, mUserName)) {

                return false;
            }
        }

        if (!checkPassword(password, mPassword)) {

            return false;
        }

        Log.e("this", password + "||" + confrimPassrod);

        if (!checkIsSamePassword(confrimPassrod, password, mConfirmPassword) || !password.equals(confrimPassrod)) {

            mConfirmPassword.setError("亲，这个对不上号哦！");

            return false;
        }

        return true;
    }
}
