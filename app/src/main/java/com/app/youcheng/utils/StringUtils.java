package com.app.youcheng.utils;

import android.widget.EditText;

/**
 * 操作字符串的工具类
 */
public class StringUtils {
    private StringUtils() {
        throw new AssertionError();
    }

    public static int length(CharSequence str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 判断给定的字符串是否为null或者是空的
     *
     * @param string 给定的字符串
     */
    public static boolean isEmpty(String string) {
        return string == null || "".equals(string.trim()) || "null".equals(string.trim());
    }


    /**
     * 判断给定的字符串是否不为null且不为空
     *
     * @param string 给定的字符串
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }


    /**
     * 判断给定的字符串数组中的所有字符串是否都为null或者是空的
     *
     * @param strings 给定的字符串
     */
    public static boolean isEmpty(String... strings) {
        for (String string : strings) {
            if (isNotEmpty(string)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断给定的字符串数组中是否全部都不为null且不为空
     *
     * @param strings 给定的字符串数组
     * @return 是否全部都不为null且不为空
     */
    public static boolean isNotEmpty(String... strings) {
        boolean result = true;
        for (String string : strings) {
            if (isEmpty(string)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static String getString(EditText editText) {
        return editText.getText().toString();
    }


    public static boolean isLegalId(String id) {
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLegalPwd(String pwd) {
        if (pwd.length() >= 6 && pwd.length() <= 12) {
            return true;
        } else {
            return false;
        }
    }

}
