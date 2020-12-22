package com.app.youcheng.entity;


import java.io.Serializable;

public class BillBean implements Serializable {
    /**
     * billId : 104
     * enterpriseId : 426534278681206784
     * sendUserName : dd
     * billTime : 2020-07-24
     * billAmountMoney : 200
     * receiveEnterpriseId : 1
     * receiveEnterpriseName : 小米
     * receiveUserName : 雷军
     * receiveUserPhone : 15695536260
     * sendStatus : 0
     * receiveStatus : 1
     * evaluation : null
     */

    private String billId;
    private String enterpriseId;
    private String sendUserName;
    private String billTime;
    private String billAmountMoney;
    private String receiveEnterpriseId;
    private String receiveEnterpriseName;
    private String receiveUserName;
    private String receiveUserPhone;
    private int sendStatus;
    private int receiveStatus;
    private int evaluation;
    private String sendEnterpriseName;
    private String sendUserPhone;
    private String enterpriseRealName;
    private String sendTime;

    public String getSendTime() {
        return sendTime;
    }

    public String getEnterpriseRealName() {
        return enterpriseRealName;
    }

    public String getSendUserPhone() {
        return sendUserPhone;
    }

    public String getSendEnterpriseName() {
        return sendEnterpriseName;
    }

    public String getBillId() {
        return billId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public String getBillTime() {
        return billTime;
    }

    public String getBillAmountMoney() {
        return billAmountMoney;
    }

    public String getReceiveEnterpriseId() {
        return receiveEnterpriseId;
    }

    public String getReceiveEnterpriseName() {
        return receiveEnterpriseName;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public String getReceiveUserPhone() {
        return receiveUserPhone;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public int getReceiveStatus() {
        return receiveStatus;
    }

    public int getEvaluation() {
        return evaluation;
    }
}
