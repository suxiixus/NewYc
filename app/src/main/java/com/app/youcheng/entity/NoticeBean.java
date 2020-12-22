package com.app.youcheng.entity;


import java.io.Serializable;

public class NoticeBean implements Serializable {


    /**
     * noticeId : 1
     * noticeTitle : 温馨提醒：2018-07-01 若依新版本发布啦
     * noticeType : 2
     * noticeContent : 新版本内容
     * status : 0
     * createBy : admin
     * createTime : 2018-03-16
     * updateBy : ry
     * updateTime : 2018-03-16
     * remark : 管理员
     */

    private String noticeId;
    private String noticeTitle;
    private String noticeType;
    private String noticeContent;
    private String status;
    private String createBy;
    private String createTime;
    private String updateBy;
    private String updateTime;
    private String remark;

    public String getNoticeId() {
        return noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public String getStatus() {
        return status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getRemark() {
        return remark;
    }
}
