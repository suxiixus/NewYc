package com.app.youcheng.entity;


import java.io.Serializable;

public class BillOrderBean implements Serializable {
    /**
     * billId : 104
     * sendEnterpriseId : 426534278681206784
     * sendEnterpriseName : 哈哈哈有意思吗
     * sendUserId : 103
     * receiveEnterpriseId : 1
     * receiveEnterpriseName : 小米
     * sendTime : 2020-08-24
     * sendStatus : 0
     * receiveStatus : 1
     * overdueTime : 2020-09-24
     * billTime : 2020-07-24
     * billAmountMoney : 200
     * billType : 2
     * paymentTerm : 5
     * notes : null
     * companyRank : null
     * billPeriod : 1
     * billQuestion : null
     */

    private String billId;
    private String sendEnterpriseId;
    private String sendEnterpriseName;
    private String sendUserId;
    private String receiveEnterpriseId;
    private String receiveEnterpriseName;
    private String sendTime;
    private int sendStatus;
    private int receiveStatus;//(0 已支付,1,未支付,2,已逾期,3申请复核,4待评价,5已评价)
    private String overdueTime;
    private String billTime;
    private String billAmountMoney;
    private int billType;
    private int paymentTerm;
    private String notes;
    private String companyRank;
    private int billPeriod;
    private String billQuestion;
    private int appeal;

    public int getAppeal() {
        return appeal;
    }

    public String getBillId() {
        return billId;
    }

    public String getSendEnterpriseId() {
        return sendEnterpriseId;
    }

    public String getSendEnterpriseName() {
        return sendEnterpriseName;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public String getReceiveEnterpriseId() {
        return receiveEnterpriseId;
    }

    public String getReceiveEnterpriseName() {
        return receiveEnterpriseName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public int getReceiveStatus() {
        return receiveStatus;
    }

    public String getOverdueTime() {
        return overdueTime;
    }

    public String getBillTime() {
        return billTime;
    }

    public String getBillAmountMoney() {
        return billAmountMoney;
    }

    public int getBillType() {
        return billType;
    }

    public int getPaymentTerm() {
        return paymentTerm;
    }

    public String getNotes() {
        return notes;
    }

    public String getCompanyRank() {
        return companyRank;
    }

    public int getBillPeriod() {
        return billPeriod;
    }

    public String getBillQuestion() {
        return billQuestion;
    }
}
