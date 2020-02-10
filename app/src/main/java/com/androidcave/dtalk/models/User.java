package com.androidcave.dtalk.models;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable {
    String UID,userName,userEmail,userLastMsg,userProfileImgURL,userStatus,userCatagory,gender,userBio,userCountry,userType;
    int unreadMessages;

    public User(String UID, String userName, String userEmail,int unreadMessages,
                String userLastMsg, String userProfileImgURL, String userStatus ) {
        this.UID = UID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userLastMsg = userLastMsg;
        this.userProfileImgURL = userProfileImgURL;
        this.userStatus = userStatus;
        this.unreadMessages = unreadMessages;
    }
// here is constructor for setup profile activity


    public User(String UID, String userName, String userEmail, String userLastMsg, String userProfileImgURL, String userStatus,
                String userCatagory, String gender, String userBio, String userCountry, String userType, int unreadMessages) {
        this.UID = UID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userLastMsg = userLastMsg;
        this.userProfileImgURL = userProfileImgURL;
        this.userStatus = userStatus;
        this.userCatagory = userCatagory;
        this.gender = gender;
        this.userBio = userBio;
        this.userCountry = userCountry;
        this.userType = userType;
        this.unreadMessages = unreadMessages;
    }

    public User() {
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserLastMsg() {
        return userLastMsg;
    }

    public void setUserLastMsg(String userLastMsg) {
        this.userLastMsg = userLastMsg;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public String getUserProfileImgURL() {
        return userProfileImgURL;
    }

    public void setUserProfileImgURL(String userProfileImgURL) {
        this.userProfileImgURL = userProfileImgURL;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserCatagory() {
        return userCatagory;
    }

    public void setUserCatagory(String userCatagory) {
        this.userCatagory = userCatagory;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
