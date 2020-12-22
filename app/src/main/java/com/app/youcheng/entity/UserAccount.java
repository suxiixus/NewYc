package com.app.youcheng.entity;


import java.io.Serializable;

public class UserAccount implements Serializable {


    /**
     * userId : 456707903971270656
     * nickName : XXX
     * phonenumber : 18326539215
     * userType : 02
     */

    private String userId;
    private String nickName;
    private String phonenumber;
    private int userType;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
