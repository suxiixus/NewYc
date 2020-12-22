package com.app.youcheng.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.app.youcheng.R;


public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.customDialog);
        setCanceledOnTouchOutside(false); // dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
        setCancelable(true); // dialog弹出后会点击屏幕或物理返回键，dialog不消失
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_loading);
    }
}
