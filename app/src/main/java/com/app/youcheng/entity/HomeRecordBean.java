package com.app.youcheng.entity;


import java.io.Serializable;
import java.util.List;

public class HomeRecordBean implements Serializable {


    /**
     * enterpriseId : 1
     * enterpriseName : 小米
     * enterpriseLogoPicture : 821
     * enterpriseRank : 0
     * overdueLoan : 199
     * status : 2
     * companyScore : 1
     * companyContacts : 821
     * companyTel : 821821821
     * companyAddress : 821
     * showContacts : 0
     * showTel : 0
     * showAddress : 0
     */

    private String enterpriseId;
    private String enterpriseName;
    private String enterpriseLogoPicture;
    private int enterpriseRank;
    private String overdueLoan;
    private int status;//(0:未注册，1已注册,2未通过审核)
    private String companyScore;//0 优质企业 1，虚假企业 2 不诚实企业
    private String companyContacts;
    private String companyTel;
    private String companyAddress;
    private int showContacts;
    private int showTel;
    private int showAddress;
    private String labels;
    private boolean isSelected = false;
    private List<String> labelValues;

    public List<String> getLabelValues() {
        return labelValues;
    }

    public String getLabels() {
        return labels;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEnterpriseLogoPicture() {
        return enterpriseLogoPicture;
    }

    public void setEnterpriseLogoPicture(String enterpriseLogoPicture) {
        this.enterpriseLogoPicture = enterpriseLogoPicture;
    }

    public int getEnterpriseRank() {
        return enterpriseRank;
    }

    public void setEnterpriseRank(int enterpriseRank) {
        this.enterpriseRank = enterpriseRank;
    }

    public String getOverdueLoan() {
        return overdueLoan;
    }

    public void setOverdueLoan(String overdueLoan) {
        this.overdueLoan = overdueLoan;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCompanyScore() {
        return companyScore;
    }

    public void setCompanyScore(String companyScore) {
        this.companyScore = companyScore;
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

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
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

    public int getShowAddress() {
        return showAddress;
    }

    public void setShowAddress(int showAddress) {
        this.showAddress = showAddress;
    }
}
