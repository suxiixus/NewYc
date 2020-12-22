package com.app.youcheng.entity;


import java.io.Serializable;
import java.util.List;

public class FirmChangeParam implements Serializable {
    /**
     * billSureSet : 0
     * companyAddress :
     * companyContacts :
     * companyTel :
     * enterpriseId :
     * enterpriseLogoPicture :
     * labelEntityList : []
     * showAddress : 0
     * showContacts : 0
     * showTel : 0
     */

    private int billSureSet;//账单确认收款设置
    private String companyAddress;
    private String companyContacts;//企业联系人姓名
    private String companyTel;
    private String enterpriseId;
    private String enterpriseLogoPicture;
    private int showAddress;
    private int showContacts;
    private int showTel;
    private List<String> labelEntityList;

    public int getBillSureSet() {
        return billSureSet;
    }

    public void setBillSureSet(int billSureSet) {
        this.billSureSet = billSureSet;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyContacts() {
        return companyContacts;
    }

    public void setCompanyContacts(String companyContacts) {
        this.companyContacts = companyContacts;
    }

    public String getCompanyTel() {
        return companyTel;
    }

    public void setCompanyTel(String companyTel) {
        this.companyTel = companyTel;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseLogoPicture() {
        return enterpriseLogoPicture;
    }

    public void setEnterpriseLogoPicture(String enterpriseLogoPicture) {
        this.enterpriseLogoPicture = enterpriseLogoPicture;
    }

    public int getShowAddress() {
        return showAddress;
    }

    public void setShowAddress(int showAddress) {
        this.showAddress = showAddress;
    }

    public int getShowContacts() {
        return showContacts;
    }

    public void setShowContacts(int showContacts) {
        this.showContacts = showContacts;
    }

    public int getShowTel() {
        return showTel;
    }

    public void setShowTel(int showTel) {
        this.showTel = showTel;
    }

    public List<String> getLabelEntityList() {
        return labelEntityList;
    }

    public void setLabelEntityList(List<String> labelEntityList) {
        this.labelEntityList = labelEntityList;
    }
}
