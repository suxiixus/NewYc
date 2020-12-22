package com.app.youcheng.entity;


import java.io.Serializable;

public class LabelBean implements Serializable {

    private String createTime;
    private String enterpriseId;
    private String labelId;
    private String labelValue;

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public String getLabelId() {
        return labelId;
    }

    public String getLabelValue() {
        return labelValue;
    }
}
