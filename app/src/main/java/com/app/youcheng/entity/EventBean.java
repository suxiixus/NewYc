package com.app.youcheng.entity;


import java.io.Serializable;

public class EventBean implements Serializable {

    private int type;//0登录成功,1退出登录,2微信支付成功,3账单跳转
    private String billId;

    public EventBean(int type) {
        this.type = type;
    }

    public EventBean(int type, String billId) {
        this.type = type;
        this.billId = billId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
