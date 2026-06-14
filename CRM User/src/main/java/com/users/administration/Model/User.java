package com.users.administration.Model;

import java.util.Date;

public class User {
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public User(String userName, long userID, String role, String userEmail) {
        this.UserName = userName;
        this.UserID = userID;
        this.Role = role;
        this.UserEmail = userEmail;
    }
    public User(){

    }
    private String UserName;
    private long UserID;
    private String Role;
    private String UserEmail;
}
