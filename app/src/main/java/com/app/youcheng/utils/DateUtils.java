package com.app.youcheng.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式化工具类
 */

public class DateUtils {

    public static String getCurFormatMonth() {
        return getMyFormatTime("yyyy-MM", System.currentTimeMillis() + "");
    }

    public static String getCurFormatTime() {
        return getMyFormatTime("yyyy-MM-dd", System.currentTimeMillis() + "");
    }

    /**
     * 将时间戳转化成固定格式（默认 yyyy-MM-dd HH:mm:ss 当前时间 ）
     */
    public static String getMyFormatTime(String format, String time) {
        if (StringUtils.isNotEmpty(time)) {
            Date date = new Date(Long.valueOf(time));

            if (StringUtils.isEmpty(format)) {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            if (date == null) {
                date = new Date();
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String formatTime = sdf.format(date);
            return formatTime;
        } else {
            return "";
        }
    }

    /**
     * 将固定格式转化成时间戳（默认 yyyy-MM-dd HH:mm:ss）
     */
    public static long getTimeMillis(String format, String dateString) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //time1小于等于time2返回true
    public static boolean compareDate(String time1, String time2) {
        if (StringUtils.isNotEmpty(time1, time2)) {
            if (getTimeMillis("yyyy-MM-dd", time1) <= getTimeMillis("yyyy-MM-dd", time2)) {
                return true;
            } else {
                return false;
            }
        } else if (StringUtils.isNotEmpty(time1)) {
            return true;
        } else if (StringUtils.isNotEmpty(time2)) {
            return true;
        } else {
            return false;
        }
    }

    //time1小于time2返回true
    public static boolean compareDate2(String time1, String time2) {
        if (StringUtils.isNotEmpty(time1, time2)) {
            if (getTimeMillis("yyyy-MM-dd", time1) < getTimeMillis("yyyy-MM-dd", time2)) {
                return true;
            } else {
                return false;
            }
        } else if (StringUtils.isNotEmpty(time1)) {
            return true;
        } else if (StringUtils.isNotEmpty(time2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将时间戳转date
     */
    public static Date getDate(String pattern, Long dateString) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String d = format.format(dateString);
        Date date = null;
        try {
            date = format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * string转date
     *
     * @param strTime
     * @param formatType
     * @return
     */
    public static Date getDateTransformString(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day) throws ParseException {
        if (StringUtils.isEmpty(day))
            return false;
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    public static String getCurrent12MonthAfter(String dataTime, int yue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date data = sdf.parse(dataTime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(data);
            if ((Calendar.MONTH + yue) <= 12) {
                rightNow.add(Calendar.YEAR, 0);// 年份不变
                rightNow.add(Calendar.MONTH, +yue);
                Date dataNow = rightNow.getTime();
                String nowCurrentTime = sdf.format(dataNow);
                return nowCurrentTime;
            } else {
                rightNow.add(Calendar.YEAR, +1);//  年份加1
                rightNow.add(Calendar.MONTH, yue);
                Date dataNow = rightNow.getTime();
                String nowCurrentTime = sdf.format(dataNow);
                return nowCurrentTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrent12MonthAfter2(String dataTime, int yue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01", Locale.getDefault());
        try {
            Date data = sdf.parse(dataTime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(data);
            if ((Calendar.MONTH + yue) <= 12) {
                rightNow.add(Calendar.YEAR, 0);// 年份不变
                rightNow.add(Calendar.MONTH, +yue);
                Date dataNow = rightNow.getTime();
                String nowCurrentTime = sdf.format(dataNow);
                return nowCurrentTime;
            } else {
                rightNow.add(Calendar.YEAR, +1);//  年份加1
                rightNow.add(Calendar.MONTH, yue);
                Date dataNow = rightNow.getTime();
                String nowCurrentTime = sdf.format(dataNow);
                return nowCurrentTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDayAfter(String dataTime, int tian) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date data = sdf.parse(dataTime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(data);

            rightNow.add(Calendar.DAY_OF_MONTH, tian);
            Date dataNow = rightNow.getTime();
            String nowCurrentTime = sdf.format(dataNow);
            return nowCurrentTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


}
