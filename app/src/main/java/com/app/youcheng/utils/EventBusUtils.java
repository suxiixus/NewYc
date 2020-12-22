package com.app.youcheng.utils;


import com.app.youcheng.entity.EventBean;

import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {
    /**
     * 注册EventBus
     *
     * @param object
     */
    public static void register(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().register(object);
        }
    }


    /**
     * 反注册EventBus
     *
     * @param object
     */
    public static void unRegister(Object object) {
        if (EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().unregister(object);
        }
    }

    /**
     * @param eventBean
     */
    public static void postEvent(EventBean eventBean) {
        EventBus.getDefault().post(eventBean);
    }


}
