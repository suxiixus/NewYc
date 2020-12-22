package com.app.youcheng.entity;


import java.io.Serializable;

public class PayRecordBean implements Serializable {
    /**
     * appOrderId : 202009011300574531739451392
     * orderAmount : 0.01
     * postTime : 2020-09-01 21-07-47
     * payChannel : 0
     * message : 向法人发起账单
     */

    private String appOrderId;
    private String orderAmount;
    private String postTime;
    private int payChannel;//支付渠道(0余额，1,支付宝，2微信)
    private String message;

    public String getAppOrderId() {
        return appOrderId;
    }

    public void setAppOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public int getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(int payChannel) {
        this.payChannel = payChannel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
