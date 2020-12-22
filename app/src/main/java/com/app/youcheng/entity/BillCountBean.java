package com.app.youcheng.entity;


import java.io.Serializable;

public class BillCountBean implements Serializable {
    /**
     * noPayBillCount : 0
     * overBillCount : 0
     */

    private String noPayBillCount;
    private String overBillCount;

    public String getNoPayBillCount() {
        return noPayBillCount;
    }

    public void setNoPayBillCount(String noPayBillCount) {
        this.noPayBillCount = noPayBillCount;
    }

    public String getOverBillCount() {
        return overBillCount;
    }

    public void setOverBillCount(String overBillCount) {
        this.overBillCount = overBillCount;
    }
}
