package com.app.youcheng.entity;


import java.io.Serializable;
import java.util.List;

public class FirmDetailBean implements Serializable {


    /**
     * enterpriseInfo : {"enterpriseId":"1","enterpriseName":"小米","enterpriseLogoPicture":"821","enterpriseRank":0,"overdueLoan":"199","status":2,"companyScore":1,"companyContacts":"821","companyTel":"821821821","companyAddress":"821","showContacts":0,"showTel":0,"showAddress":0}
     * enterpriseRank : [{"rankId":"1","enterpriseId":"1","enterpriseName":"小米","settlementTime":"2020-05-28","settlementReason":1,"rankCalculation":1,"del":1},{"rankId":"3","enterpriseId":"1","enterpriseName":"小米","settlementTime":"2020-08-12","settlementReason":12,"rankCalculation":1,"del":1}]
     * evaluation : [{"adminEvaluationId":"1","enterpriseId":"1","adminEvaluationContent":"1","createTime":"2020-05-21","userId":"1","userName":"admin","logo":null}]
     * labelEntities : []
     * friends : 0
     */

    private EnterpriseInfoBean enterpriseInfo;
    private int friends;//是否企业圈好友(0:否,1:是)
    private List<EnterpriseRankBean> enterpriseRank;
    private List<EvaluationBean> evaluation;
    private List<LabelEntityBean> labelEntities;

    public EnterpriseInfoBean getEnterpriseInfo() {
        return enterpriseInfo;
    }

    public int getFriends() {
        return friends;
    }

    public List<EnterpriseRankBean> getEnterpriseRank() {
        return enterpriseRank;
    }

    public List<EvaluationBean> getEvaluation() {
        return evaluation;
    }

    public List<LabelEntityBean> getLabelEntities() {
        return labelEntities;
    }

    public static class EnterpriseInfoBean implements Serializable {
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
        private int status;
        private String companyScore;
        private String companyContacts;
        private String companyTel;
        private String companyAddress;
        private int showContacts;
        private int showTel;
        private int showAddress;

        public String getEnterpriseId() {
            return enterpriseId;
        }

        public String getEnterpriseName() {
            return enterpriseName;
        }

        public String getEnterpriseLogoPicture() {
            return enterpriseLogoPicture;
        }

        public int getEnterpriseRank() {
            return enterpriseRank;
        }

        public String getOverdueLoan() {
            return overdueLoan;
        }

        public int getStatus() {
            return status;
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
    }

    public static class EnterpriseRankBean implements Serializable {
        /**
         * rankId : 1
         * enterpriseId : 1
         * enterpriseName : 小米
         * settlementTime : 2020-05-28
         * settlementReason : 1
         * rankCalculation : 1
         * del : 1
         */

        private String rankId;
        private String enterpriseId;
        private String enterpriseName;
        private String settlementTime;
        private int settlementReason;//0逾期确认，1按期支付,2平台增加，3平台扣除)
        private int rankCalculation;//企业分值加减
        private int del;
        private String sendEnterpriseName;
        private String billAmountMoney;

        public String getSendEnterpriseName() {
            return sendEnterpriseName;
        }

        public String getBillAmountMoney() {
            return billAmountMoney;
        }

        public String getRankId() {
            return rankId;
        }

        public String getEnterpriseId() {
            return enterpriseId;
        }

        public String getEnterpriseName() {
            return enterpriseName;
        }

        public String getSettlementTime() {
            return settlementTime;
        }

        public int getSettlementReason() {
            return settlementReason;
        }

        public int getRankCalculation() {
            return rankCalculation;
        }

        public int getDel() {
            return del;
        }
    }

    public static class EvaluationBean implements Serializable {
        /**
         * adminEvaluationId : 1
         * enterpriseId : 1
         * adminEvaluationContent : 1
         * createTime : 2020-05-21
         * userId : 1
         * userName : admin
         * logo : null
         */

        private String adminEvaluationId;
        private String enterpriseId;
        private String adminEvaluationContent;
        private String createTime;
        private String userId;
        private String userName;
        private String logo;

        public String getAdminEvaluationId() {
            return adminEvaluationId;
        }

        public String getEnterpriseId() {
            return enterpriseId;
        }

        public String getAdminEvaluationContent() {
            return adminEvaluationContent;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getLogo() {
            return logo;
        }
    }

    public static class LabelEntityBean implements Serializable {
        /**
         * createTime :
         * enterpriseId :
         * labelId :
         * labelValue :
         */

        private String createTime;
        private String enterpriseId;
        private String labelId;
        private String labelValue;

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


}
