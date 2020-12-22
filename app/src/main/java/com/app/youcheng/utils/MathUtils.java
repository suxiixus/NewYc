package com.app.youcheng.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/30.
 */

public class MathUtils {

    /**
     * 过滤多余的0
     */
    public static String subZeroAndDot(String s) {
        if (StringUtils.isNotEmpty(s) && s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
            return s;
        }
        return s;
    }

    /**
     * 截取到n位小数
     */
    public static String getCutNumber(String number, int n) {
        DecimalFormat df;
        String pattern = "0";
        if (n > 0) {
            pattern = "0.";
            for (int j = 0; j < n; j++) {
                pattern += "0";
            }
        }
        df = new DecimalFormat(pattern);
        if (StringUtils.isNotEmpty(number)) {
            BigDecimal resultBD = new BigDecimal(number.trim()).setScale(n, BigDecimal.ROUND_DOWN);//截取
            return df.format(resultBD).replace(",", ".");
        } else {
            return df.format(0).replace(",", ".");
        }
    }

    /**
     * 精确到n位小数
     */
    public static String getBigDecimalRundNumber(String number, int n) {
        String pattern = "0";
        if (n > 0) {
            pattern = "0.";
            for (int j = 0; j < n; j++) {
                pattern += "0";
            }
        }

        DecimalFormat df = new DecimalFormat(pattern);
        if (StringUtils.isNotEmpty(number)) {
            BigDecimal resultBD = new BigDecimal(number.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);//四舍五入
            return df.format(resultBD).replace(",", ".");
        } else {
            return "0";
        }
    }

    /**
     * 货币格式（保留8位）
     * 12,345,678.90000000
     */
    public static String numberFormatMoney(String number) {
        DecimalFormat df = new DecimalFormat(",###,##0.00000000");
        if (StringUtils.isNotEmpty(number)) {
            return df.format(new BigDecimal(number));
        }
        return "0";
    }

    /**
     * 货币格式（保留n位）
     * 12,345,678.90
     */
    public static String numberFormatMoney(String number, int n) {
        String pattern = ",###,##0";
        if (n > 0) {
            pattern = ",###,##0.";
            for (int j = 0; j < n; j++) {
                pattern += "0";
            }
        }

        DecimalFormat df = new DecimalFormat(pattern);
        if (StringUtils.isNotEmpty(number)) {
            return df.format(new BigDecimal(number));
        }
        return "0";
    }

    /**
     * 加
     */
    public static String getBigDecimalAdd(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.add(bBD).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toPlainString();
        }
        return "0";
    }

    /**
     * 减
     */
    public static String getBigDecimalSubtract(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.subtract(bBD).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toPlainString();
        }
        return "0";
    }

    /**
     * 乘
     */
    public static String getBigDecimalMultiply(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.multiply(bBD).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toPlainString();
        }
        return "0";
    }

    /**
     * 除
     */
    public static String getBigDecimalDivide(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2) && getBigDecimalCompareTo(number2, "0", 18) != 0) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.divide(bBD, n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toPlainString();
        }
        return "0";
    }

    //BigDecimal对比 BigDecimal为小于val返回-1，如果BigDecimal为大于val返回1，如果BigDecimal为等于val返回0
    public static int getBigDecimalCompareTo(String number1, String number2, int n) {
        if (StringUtils.isNotEmpty(number1, number2)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal bBD = new BigDecimal(number2.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            return aBD.compareTo(bBD);
        } else {
            return -2;
        }
    }

    //判断是否为数字，包括整数和小数,不包含正负号
    public static boolean isNumber(String str) {
        if (StringUtils.isNotEmpty(str)) {
            //带小数的
            Pattern pattern = Pattern.compile("[0-9]+.*[0-9]*");
            if (pattern.matcher(str).matches()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //BigDecimal乘方
    public static String getBigDecimalPow(String number1, int number2, int n) {
        if (StringUtils.isNotEmpty(number1)) {
            BigDecimal aBD = new BigDecimal(number1.trim()).setScale(n, BigDecimal.ROUND_HALF_UP);
            BigDecimal resultBD = aBD.pow(number2).setScale(n, BigDecimal.ROUND_HALF_UP);
            return resultBD.toPlainString();
        } else {
            return "0";
        }
    }

    //BigDecimal 10的8次方
    public static String getBigDecimal10Pow8() {
        BigDecimal aBD = new BigDecimal("10").setScale(8, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultBD = aBD.pow(8).setScale(8, BigDecimal.ROUND_HALF_UP);
        return resultBD.toPlainString();
    }

    //BigDecimal 10的18次方
    public static String getBigDecimal10Pow18() {
        BigDecimal aBD = new BigDecimal("10").setScale(8, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultBD = aBD.pow(18).setScale(8, BigDecimal.ROUND_HALF_UP);
        return resultBD.toPlainString();
    }


}
