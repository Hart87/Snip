package com.hart.core;

/**
 * Created by jameshart on 9/16/18.
 */
public class LoginStruct {
    private String auth;
    private String userName;

    public LoginStruct(String auth, String userName) {
        this.auth = auth;
        this.userName = userName;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
