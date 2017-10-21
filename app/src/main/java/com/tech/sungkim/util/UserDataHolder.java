package com.tech.sungkim.util;

/**
 * Created by HP-PC on 05-04-2017.
 */


public class UserDataHolder {
    private String mUserId;
    private String name;
    private String mEmail;

    private static final UserDataHolder holder = new UserDataHolder();
    public static UserDataHolder getInstance() {return holder;}
    public  String getmUserId() {
        return mUserId;
    }

    public  void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public  String getmName() {
        return name;
    }

    public  void setmName(String mName) {
        this.name = mName;
    }

    public  String getmEmail() {
        return mEmail;
    }

    public  void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
