package com.app.youcheng.utils;


import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.zhouyou.http.exception.ApiException.ERROR;

import static com.app.youcheng.GlobalConstant.ERROR_500;


/**
 * 请求错误
 */

public class NetCodeUtils {

    public static void checkedErrorCode(int code, String toastMessage) {
        switch (code) {
            case ERROR.PARSE_ERROR:
                toastMessage = MyApplication.getApplication().getResources().getString(R.string.str_parse_error);
                break;
            case ERROR.NETWORD_ERROR:
                toastMessage = MyApplication.getApplication().getResources().getString(R.string.str_network_error);
                break;
            case ERROR.TIMEOUT_ERROR:
                toastMessage = MyApplication.getApplication().getResources().getString(R.string.connection_out);
                break;
            case ERROR.UNKNOWNHOST_ERROR:
                toastMessage = MyApplication.getApplication().getResources().getString(R.string.str_network_error);
                break;
            case ERROR.UNKNOWN:
                toastMessage = MyApplication.getApplication().getResources().getString(R.string.str_unknown_error);
                break;
            case ERROR_500:
                toastMessage = MyApplication.getApplication().getResources().getString(R.string.str_server_error);
                break;
        }

        if (StringUtils.isNotEmpty(toastMessage)) {
            ToastUtils.showToast(toastMessage);
        }
    }

    public static void checkedErrorCode(String code, String toastMessage) {
        if (StringUtils.isNotEmpty(toastMessage)) {
            ToastUtils.showToast(toastMessage);
        }
    }


}
