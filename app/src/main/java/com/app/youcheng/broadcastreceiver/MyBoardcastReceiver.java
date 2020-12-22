package com.app.youcheng.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import cn.jpush.android.service.PushService;

public class MyBoardcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pushintent = new Intent(context, PushService.class);//启动极光推送的服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pushintent);
        } else {
            context.startService(pushintent);
        }
    }

}
