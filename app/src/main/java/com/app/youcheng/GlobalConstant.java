package com.app.youcheng;

import android.os.Environment;

/**
 * Created by Administrator on 2017/5/5.
 */

public class GlobalConstant {
    public static final boolean isDebug = true; // 是否代开log日志
    public static final String WXAPPID = "wx6d3b6c0ae07bd88a";

    /**
     * 保存路径
     */
    public static final String myDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youcheng/";

    public static final int TAKE_PHOTO = 10;
    public static final int CHOOSE_ALBUM = 11;

    /**
     * 自定义的常量
     */
    public static final int pageSize = 10;
    public static final String JSON_ERROR = "-9999"; // 解析出错
    public static final int ERROR_500 = 500; //服务器异常
    public static final int OK = 200;
    public static final int ERROR_401 = 401;//登录失效
    public static final int SDK_PAY_FLAG = 6;

    /**
     * 存储文件名
     */
    public static final String UserSaveFileName = "USER.INFO"; // 保存的文件file
    public static final String UserSaveFileDirName = "USER"; // 保存文件file的目录

    public static final String IS_FIRST_LOGIN = "IS_FIRST_LOGIN"; // 是否为第一次登录
    public static final String SP_KEY_MONEY_SHOW_TYPE = "SP_KEY_MONEY_SHOW_TYPE"; // 明文显示，密文显示
    public static final String WAKE_UP = "WAKE_UP"; // 是否开启唤醒保护
    public static final String SP_KEY_IS_NEED_SHOW_LOCK = "SP_KEY_IS_NEED_SHOW_LOCK";//是否处于过后台


}
