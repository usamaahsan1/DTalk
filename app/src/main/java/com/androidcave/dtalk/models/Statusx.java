package com.androidcave.dtalk.models;

import java.util.Date;

public class Statusx {

    String UID, userProfileImageURL, userName, statusImage, statusDesc;

    public Statusx(String UID, String userProfileImageURL, String userName, String statusImage, String statusDesc, Date statusTimeStamp) {
        this.UID = UID;
        this.userProfileImageURL = userProfileImageURL;
        this.userName = userName;
        this.statusImage = statusImage;
        this.statusDesc = statusDesc;
        this.statusTimeStamp = statusTimeStamp;
    }

    Date statusTimeStamp;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUserProfileImageURL() {
        return userProfileImageURL;
    }

    public void setUserProfileImageURL(String userProfileImageURL) {
        this.userProfileImageURL = userProfileImageURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Date getStatusTimeStamp() {
        return statusTimeStamp;
    }

    public void setStatusTimeStamp(Date statusTimeStamp) {
        this.statusTimeStamp = statusTimeStamp;
    }
}
