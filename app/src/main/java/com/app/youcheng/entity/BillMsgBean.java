package com.app.youcheng.entity;


import java.io.Serializable;

public class BillMsgBean implements Serializable {

//    "id": "469864348493422592",
//            "content": "你的上游有一张账单账期已结束(即将进入付款期)",
//            "billId": "468270074790289408",
//            "readFlag": 0,
//            "userId": "467933370460020736",
//            "createTime": "2020-09-27"

    private String billId;
    private String content;
    private String createTime;
    private String id;
    private int readFlag;
    private String userId;

    public String getBillId() {
        return billId;
    }

    public String getContent() {
        return content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getId() {
        return id;
    }

    public int getReadFlag() {
        return readFlag;
    }

    public String getUserId() {
        return userId;
    }
}
