package com.app.youcheng.entity;


import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {


    /**
     * enterpriseId : 456707903497314304
     * enterpriseName : XXX有限公司
     * enterpriseCode : 987654
     * enterpriseRealName : XXX
     * card : 12345678
     * cardPictureFront : http://47.112.32.114:8080/profile/upload/2020/08/21/51ab187bede7efe75d8cf7ba882e9d0e.jpg
     * cardPictureBack : http://47.112.32.114:8080/profile/upload/2020/08/21/8bcb47de325bf30ba4baddbea76729ce.jpg
     * businessLicensePicture : http://47.112.32.114:8080/profile/upload/2020/08/21/b0b3403b4e2c7bd89246c9e895d20079.jpg
     * enterpriseLogoPicture : http://47.112.32.114:8080/profile/upload/2020/08/21/ff7717f41dc73a4b50bec8b2eaff4820.jpg
     * registerPassTime : null
     * registerTime : 2020-08-21
     * updateTime : null
     * checkUserId : null
     * notes : null
     * status : 1
     * enterpriseLabelIds : [{"labelId":null,"labelValue":null,"createTime":null,"enterpriseId":"456707903497314304"}]
     * shareCode : 7SRGES
     * sendMoney : 200
     * accountMoney : 0
     * useReset : 0
     * companyRank : 100
     * overdueLoan : 0
     * companyScore : null
     * companyContacts : null
     * companyTel : null
     * companyAddress : null
     * showContacts : 0
     * showTel : 0
     * showAddress : 0
     * nickName : XXX
     * userType : 02
     */

    private String enterpriseId;
    private String enterpriseName;
    private String enterpriseCode;
    private String enterpriseRealName;
    private String card;
    private String cardPictureFront;
    private String cardPictureBack;
    private String businessLicensePicture;
    private String enterpriseLogoPicture;
    private String registerPassTime;
    private String registerTime;
    private String updateTime;
    private String checkUserId;
    private String notes;
    private int status;//0:未审核，1通过审核,2未通过审核
    private String shareCode;
    private String sendMoney;
    private String accountMoney;
    private int useReset;
    private int companyRank;
    private int overdueLoan;
    private String companyScore;
    private String companyContacts;
    private String companyTel;
    private String companyAddress;
    private int showContacts;
    private int showTel;
    private int showAddress;
    private String nickName;
    private int userType;//（00系统用户 01 客服，02 用户管理员，03 企业员工）
    private List<EnterpriseLabelIdsBean> enterpriseLabelIds;

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public String getEnterpriseRealName() {
        return enterpriseRealName;
    }

    public String getCard() {
        return card;
    }

    public String getCardPictureFront() {
        return cardPictureFront;
    }

    public String getCardPictureBack() {
        return cardPictureBack;
    }

    public String getBusinessLicensePicture() {
        return businessLicensePicture;
    }

    public String getEnterpriseLogoPicture() {
        return enterpriseLogoPicture;
    }

    public String getRegisterPassTime() {
        return registerPassTime;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getCheckUserId() {
        return checkUserId;
    }

    public String getNotes() {
        return notes;
    }

    public int getStatus() {
        return status;
    }

    public String getShareCode() {
        return shareCode;
    }

    public String getSendMoney() {
        return sendMoney;
    }

    public String getAccountMoney() {
        return accountMoney;
    }

    public int getUseReset() {
        return useReset;
    }

    public int getCompanyRank() {
        return companyRank;
    }

    public int getOverdueLoan() {
        return overdueLoan;
    }

    public String getCompanyScore() {
        return companyScore;
    }

    public String getCompanyContacts() {
        return companyContacts;
    }

    public String getCompanyTel() {
        return companyTel;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public int getShowContacts() {
        return showContacts;
    }

    public int getShowTel() {
        return showTel;
    }

    public int getShowAddress() {
        return showAddress;
    }

    public String getNickName() {
        return nickName;
    }

    public int getUserType() {
        return userType;
    }

    public List<EnterpriseLabelIdsBean> getEnterpriseLabelIds() {
        return enterpriseLabelIds;
    }

    public static class EnterpriseLabelIdsBean implements Serializable {
        /**
         * labelId : null
         * labelValue : null
         * createTime : null
         * enterpriseId : 456707903497314304
         */

        private String labelId;
        private String labelValue;
        private String createTime;
        private String enterpriseId;

        public String getLabelId() {
            return labelId;
        }

        public String getLabelValue() {
            return labelValue;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getEnterpriseId() {
            return enterpriseId;
        }
    }
}
