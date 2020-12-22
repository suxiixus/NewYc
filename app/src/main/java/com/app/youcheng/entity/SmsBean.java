package com.app.youcheng.entity;


import java.io.Serializable;

public class SmsBean implements Serializable {
    /**
     * systemId : 3
     * userId : 103
     * receiveSms : 1
     * billSmsNotice : 1
     * rankSmsNotice : 1
     * overdueSmsNotice : 1
     */

    private String systemId;
    private String userId;
    private int receiveSms;
    private int billSmsNotice;
    private int rankSmsNotice;
    private int overdueSmsNotice;
    private int showBillAmountMoney;
    private int showSendEnterpriseName;

    public int getShowBillAmountMoney() {
        return showBillAmountMoney;
    }

    public int getShowSendEnterpriseName() {
        return showSendEnterpriseName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getReceiveSms() {
        return receiveSms;
    }

    public void setReceiveSms(int receiveSms) {
        this.receiveSms = receiveSms;
    }

    public int getBillSmsNotice() {
        return billSmsNotice;
    }

    public void setBillSmsNotice(int billSmsNotice) {
        this.billSmsNotice = billSmsNotice;
    }

    public int getRankSmsNotice() {
        return rankSmsNotice;
    }

    public void setRankSmsNotice(int rankSmsNotice) {
        this.rankSmsNotice = rankSmsNotice;
    }

    public int getOverdueSmsNotice() {
        return overdueSmsNotice;
    }

    public void setOverdueSmsNotice(int overdueSmsNotice) {
        this.overdueSmsNotice = overdueSmsNotice;
    }
}
